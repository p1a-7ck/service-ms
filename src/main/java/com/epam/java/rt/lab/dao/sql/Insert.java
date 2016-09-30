package com.epam.java.rt.lab.dao.sql;

import com.epam.java.rt.lab.dao.DaoException;
import com.epam.java.rt.lab.entity.BaseEntity;
import com.epam.java.rt.lab.entity.EntityProperty;
import com.epam.java.rt.lab.util.StringArray;

import java.util.List;

/**
 * service-ms
 */
public class Insert extends Sql {

    private static final String INSERT = "INSERT INTO ";
    private static final String BEFORE_NAMES = " (";
    private static final String BEFORE_VALUES = ") VALUES (";
    private static final String AFTER_VALUES = ")";

    private BaseEntity entity;
    private String table;
    private InsertValue[] insertValueArray;

    Insert(BaseEntity entity) throws DaoException {
        if (entity == null)
            throw new DaoException("exception.dao.sql.insert.empty-entity");
        this.entity = entity;
        this.table = getProperty(entity.getClass().getName());
    }

    public Insert values(InsertValue... insertValueArray) throws DaoException {
        for (InsertValue insertValue : insertValueArray) {
            if (!entity.getClass().equals(insertValue.entityProperty.getEntityClass()))
                throw new DaoException("exception.dao.sql.insert.entity-class-property");
            insertValue.value.link(getWildValueList());
        }
        this.insertValueArray = insertValueArray;
        return this;
    }

    @Override
    public String create() throws DaoException {
        boolean first = true;
        StringBuilder result = new StringBuilder();
        StringBuilder values = new StringBuilder();
        result.append(INSERT).append(table).append(BEFORE_NAMES);
        values.append(BEFORE_VALUES);
        for (InsertValue insertValue : this.insertValueArray) {
            if (first) {
                first = false;
            } else {
                result.append(COMMA_DELIMITER);
                values.append(COMMA_DELIMITER);
            }
            result.append(getColumn(insertValue.entityProperty));
            values.append(insertValue.value.makeWildcard());
        }
        return result.append(values).append(AFTER_VALUES).toString();
    }

    /**
     *
     */
    public static class InsertValue {

        private EntityProperty entityProperty;
        private WildValue value;

        public <T> InsertValue(EntityProperty entityProperty, T value) throws DaoException {
            this.entityProperty = entityProperty;
            this.value = new WildValue(value);
        }


    }
}
