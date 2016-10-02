package com.epam.java.rt.lab.dao;

import com.epam.java.rt.lab.dao.sql.OrderBy;
import com.epam.java.rt.lab.dao.sql.Update;
import com.epam.java.rt.lab.dao.sql.Where;
import com.epam.java.rt.lab.dao.sql.WildValue;
import com.epam.java.rt.lab.entity.BaseEntity;

/**
 *  used to exchange parameters between service layer and dao layer, and through dao methods
 */
public class DaoParameter {

    private Where.Predicate wherePredicate;
    private OrderBy.Criteria[] orderByCriteriaArray;
    private Long limitOffset;
    private Long limitCount;
    private WildValue[] wildValue;
    private Update.SetValue[] setValueArray;
    private BaseEntity entity;
//    private boolean makeCache;

    public DaoParameter() {
    }

    public Where.Predicate getWherePredicate() {
        return this.wherePredicate;
    }

    public DaoParameter setWherePredicate(Where.Predicate wherePredicate) {
        this.wherePredicate = wherePredicate;
        return this;
    }

    public OrderBy.Criteria[] getOrderByCriteriaArray() {
        return this.orderByCriteriaArray;
    }

    public DaoParameter setOrderByCriteriaArray(OrderBy.Criteria... orderByCriteriaArray) {
        this.orderByCriteriaArray = orderByCriteriaArray;
        return this;
    }

    public DaoParameter setLimit(Long offset, Long count) {
        this.limitOffset = offset;
        this.limitCount = count;
        return this;
    }

    public Long getLimitOffset() {
        return this.limitOffset;
    }

    public Long getLimitCount() {
        return this.limitCount;
    }

    public Update.SetValue[] getSetValueArray() {
        return this.setValueArray;
    }

    public DaoParameter setSetValueArray(Update.SetValue... setValueArray) {
        this.setValueArray = setValueArray;
        return this;
    }

    public BaseEntity getEntity() {
        return entity;
    }

    public DaoParameter setEntity(BaseEntity entity) {
        this.entity = entity;
        return this;
    }

    //    public boolean isMakeCache() {
//        return makeCache;
//    }
//
//    public DaoParameter setMakeCache(boolean makeCache) {
//        this.makeCache = makeCache;
//        return this;
//    }

}
