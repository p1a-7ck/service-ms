package com.epam.java.rt.lab.dao.h2;

import com.epam.java.rt.lab.dao.Dao;
import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.dao.factory.JdbcDaoFactory;
import com.epam.java.rt.lab.dao.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * service-ms
 */
public abstract class JdbcDao implements Dao {
    private static final Logger logger = LoggerFactory.getLogger(JdbcDao.class);
    private static Map<Type, Method> preparedStatementMethodMap = new HashMap<>();
    private static Map<String, PreparedStatement> preparedStatementMap = new HashMap<>();
    private Connection connection = null;
    private Long selectCount;

    private static void initPreparedStatementMethodMap() throws DaoException {
        try {
            if (preparedStatementMethodMap.size() == 0) {
                preparedStatementMethodMap.put(int.class, PreparedStatement.class.getMethod("setInt", int.class, int.class));
                preparedStatementMethodMap.put(Integer.class, PreparedStatement.class.getMethod("setInt", int.class, int.class));
                preparedStatementMethodMap.put(boolean.class, PreparedStatement.class.getMethod("setBoolean", int.class, boolean.class));
                preparedStatementMethodMap.put(Boolean.class, PreparedStatement.class.getMethod("setBoolean", int.class, boolean.class));
                preparedStatementMethodMap.put(byte.class, PreparedStatement.class.getMethod("setByte", int.class, byte.class));
                preparedStatementMethodMap.put(Byte.class, PreparedStatement.class.getMethod("setByte", int.class, byte.class));
                preparedStatementMethodMap.put(short.class, PreparedStatement.class.getMethod("setShort", int.class, short.class));
                preparedStatementMethodMap.put(Short.class, PreparedStatement.class.getMethod("setShort", int.class, short.class));
                preparedStatementMethodMap.put(long.class, PreparedStatement.class.getMethod("setLong", int.class, long.class));
                preparedStatementMethodMap.put(Long.class, PreparedStatement.class.getMethod("setLong", int.class, long.class));
                preparedStatementMethodMap.put(BigDecimal.class, PreparedStatement.class.getMethod("setBigDecimal", int.class, BigDecimal.class));
                preparedStatementMethodMap.put(double.class, PreparedStatement.class.getMethod("setDouble", int.class, double.class));
                preparedStatementMethodMap.put(Double.class, PreparedStatement.class.getMethod("setDouble", int.class, double.class));
                preparedStatementMethodMap.put(float.class, PreparedStatement.class.getMethod("setFloat", int.class, float.class));
                preparedStatementMethodMap.put(Float.class, PreparedStatement.class.getMethod("setFloat", int.class, float.class));
//            preparedStatementMethodMap.put(Time.class, PreparedStatement.class.getMethod("setTime", int.class, Time.class));
//            preparedStatementMethodMap.put(Date.class, PreparedStatement.class.getMethod("setDate", int.class, Date.class));
//            preparedStatementMethodMap.put(Timestamp.class, PreparedStatement.class.getMethod("setTimestamp", int.class, Timestamp.class));
                preparedStatementMethodMap.put(String.class, PreparedStatement.class.getMethod("setString", int.class, String.class));
                preparedStatementMethodMap.put(Blob.class, PreparedStatement.class.getMethod("setBlob", int.class, Blob.class));
                preparedStatementMethodMap.put(Clob.class, PreparedStatement.class.getMethod("setClob", int.class, Clob.class));
            }
        } catch (NoSuchMethodException e) {
            throw new DaoException("exception.dao.jdbc.init-prepared-statement-method-map", e.getCause());
        }
    }

    public JdbcDao(Connection connection) throws DaoException {
        logger.debug("JdbcDao: {}", connection);
        this.connection = connection;
        if (preparedStatementMethodMap.size() == 0) initPreparedStatementMethodMap();
    }

    Connection getConnection() {
        return connection;
    }

    private PreparedStatement getPreparedStatement(Map<String, PreparedStatement> preparedStatementMap, String sqlQuery)
            throws DaoException {
        try {
            PreparedStatement preparedStatement = preparedStatementMap.get(sqlQuery);
            if (preparedStatement != null) {
                preparedStatement.clearParameters();
                preparedStatement.cancel();
                return preparedStatement;
            }
            preparedStatement = this.connection.prepareStatement(sqlQuery);
            preparedStatementMap.put(sqlQuery, preparedStatement);
            return preparedStatement;
        } catch (SQLException e) {
            throw new DaoException("exception.dao.jdbc.get-prepared-statement", e.getCause());
        }
    }

    void setPreparedStatementValues(PreparedStatement preparedStatement,
                                            List<Column> columnList) throws DaoException {
        setPreparedStatementValues(preparedStatement, null, columnList);
    }

    void setPreparedStatementValues(PreparedStatement preparedStatement, List<Set> setList,
                                            List<Column> columnList) throws DaoException {
        try {
            int valueIndex = 1;
            Method preparedStatementMethod;
            if (setList != null) {
                for (Set set : setList) {
                    preparedStatementMethod = preparedStatementMethodMap.get(set.value.getClass());
                    if (preparedStatementMethod == null)
                        throw new DaoException("exception.dao.jdbc.set-prepared-statement-values.method");
                    preparedStatementMethod.invoke(preparedStatement, valueIndex, set.value);
                    valueIndex++;
                }
            }
            for (Column column : columnList) {
                if (column.value != null && !column.isColumn) {
                    preparedStatementMethod = preparedStatementMethodMap.get(column.value.getClass());
                    if (preparedStatementMethod == null)
                        throw new DaoException("exception.dao.jdbc.set-prepared-statement-values.method");
                    preparedStatementMethod.invoke(preparedStatement, valueIndex, column.value);
                    valueIndex++;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new DaoException("exception.dao.jdbc.set-prepared-statement-values.invoke");
        }
    }

    <T> T fieldValue(Field field, Object entity) throws IllegalAccessException {
        boolean isAccessible = true;
        try {
            isAccessible = field.isAccessible();
            if (isAccessible) return (T) field.get(entity);
            field.setAccessible(true);
            return (T) field.get(entity);
        } finally {
            if (!isAccessible) field.setAccessible(false);
        }
    }

    private <T> PreparedStatement query(SqlQuery sqlQuery, T entity, String fieldNames, String setNames) throws DaoException {
        try {
            if (setNames != null) {
                setNames = setNames.replaceAll(" ", "");
                for (String setName : setNames.split(",")) {
                    Field field = getField(entity.getClass(), setName);
                    sqlQuery.getSetList().add(getEntitySet(entity, field));
                }
            }
            if (fieldNames != null) {
                fieldNames = fieldNames.replaceAll(" ", "");
                for (String fieldName : fieldNames.split(",")) {
                    Field field = getField(entity.getClass(), fieldName);
                    sqlQuery.getColumnList().add(getEntityColumn(entity, field));
                }
            }
            PreparedStatement preparedStatement = getPreparedStatement
                    (JdbcDao.preparedStatementMap, sqlQuery.create());
            setPreparedStatementValues(preparedStatement, sqlQuery.getSetList(), sqlQuery.getColumnList());
            return preparedStatement;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new DaoException("exception.dao.jdbc.query.get-declared-field", e.getCause());
        }
    }

    private <T> ResultSet selectQuery(T entity, String fieldNames, String columnNames) throws DaoException {
        return selectQuery(entity, fieldNames, columnNames, null, null);
    }

    private <T> ResultSet selectQuery(T entity, String fieldNames, String columnNames, Long offset, Long count)
            throws DaoException {
        try {
            SqlQuery select = new Select(getEntityTableName(), columnNames, offset, count);
            if (offset != null && count != null) {
                Statement statement = connection.createStatement();
                statement.execute(select.createCount());
                if (statement.getResultSet().next())
                    this.selectCount = statement.getResultSet().getLong("count");
                statement.close();
            }
            PreparedStatement preparedStatement = query(select, entity, fieldNames, null);
            preparedStatement.execute();
            return preparedStatement.getResultSet();
        } catch (SQLException e) {
            throw new DaoException("exception.dao.jdbc.selectQuery.get-result-set", e.getCause());
        }
    }

    private Field getField(Class entityClass, String fieldName) throws NoSuchFieldException {
        Field field = null;
        while(entityClass != null) {
            try {
                field = entityClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // do nothing
            }
            if (field != null) return field;
            entityClass = entityClass.getSuperclass();
        }
        throw new NoSuchFieldException("exception.dao.jdbc.get-field");
    }

    PreparedStatement rawQuery(String sqlQuery, List<Column> columnList) throws DaoException {
        PreparedStatement preparedStatement = getPreparedStatement
                (JdbcDao.preparedStatementMap, sqlQuery);
        setPreparedStatementValues(preparedStatement, columnList);
        return preparedStatement;
    }

    @Override
    public <T> T getFirst(T entity, String fieldNames) throws DaoException {
        return getFirst(entity, fieldNames, null);
    }

    @Override
    public <T> T getFirst(T entity, String fieldNames, String columnNames) throws DaoException {
        try {
            ResultSet resultSet = selectQuery(entity, fieldNames, columnNames);
            if (resultSet == null || !resultSet.first()) return null;
            return getEntityFromResultSet(entity, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("exception.dao.jdbc.get-first.get-result-set", e.getCause());
        }
    }

    @Override
    public <T> List<T> getAll() throws DaoException {
        return getAll(null, null, null);
    }

    @Override
    public <T> List<T> getAll(T entity, String fieldNames) throws DaoException {
        return getAll(entity, fieldNames, null, null, null);
    }

    @Override
    public <T> List<T> getAll(T entity, String fieldNames, String columnNames) throws DaoException {
        return getAll(entity, fieldNames, columnNames, null, null);
    }

    @Override
    public <T> List<T> getAll(T entity, String fieldNames, String columnNames, Long offset, Long count) throws DaoException {
        try {
            ResultSet resultSet = selectQuery(entity, fieldNames, columnNames, offset, count);
            if (resultSet == null) return null;
            List<T> entityList = new ArrayList<>();
            while(resultSet.next()) entityList.add(getEntityFromResultSet(entity, resultSet));
            return entityList;
        } catch (SQLException e) {
            throw new DaoException("exception.dao.jdbc.get-all.get-result-set", e.getCause());
        }
    }

    @Override
    public Long getSelectCount() {
        return this.selectCount;
    }

    abstract String getEntityTableName();

    abstract <T> Column getEntityColumn(T entity, Field field) throws DaoException;

    abstract <T> T getEntityFromResultSet(T entity, ResultSet resultSet) throws DaoException;

    private <T> int updateQuery(T entity, String fieldNames, String setNames) throws DaoException {
        try {
            SqlQuery update = new Update(getEntityTableName());
            PreparedStatement preparedStatement = query(update, entity, fieldNames, setNames);
            System.out.println(preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("exception.dao.jdbc.updateQuery.get-result-set", e.getCause());
        }
    }

    @Override
    public <T> int update(T entity, String fieldNames, String setNames) throws DaoException {
        return updateQuery(entity, fieldNames, setNames) + relUpdate(entity, setNames);
    }

    abstract <T> Set getEntitySet(T entity, Field field) throws DaoException;

    <T> int relUpdate(T entity, String setNames) throws DaoException {
        return 0;
    }

}
