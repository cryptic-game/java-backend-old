package net.cryptic_game.backend.base.utils;

import net.cryptic_game.backend.base.sql.models.TableModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public final class SQLUtils {

    private SQLUtils() {
    }

    public static <T extends TableModel> List<T> selectAll(final EntityManager em, final Class<T> type) {
        return SQLUtils.selectAll(em, type, null);
    }

    public static <T extends TableModel> List<T> selectAll(final EntityManager em, final Class<T> type, final String order) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<T> query = cb.createQuery(type);
        final Root<T> root = query.from(type);
        query.select(root);
        if (order != null) query.orderBy(cb.asc(root.get(order)));
        return em.createQuery(query).getResultList();
    }

    public static <T extends TableModel> List<T> selectWhere(final EntityManager em, final Class<T> type, final String column, final Object value) {
        return SQLUtils.selectWhere(em, type, column, null, value);
    }

    public static <T extends TableModel> List<T> selectWhere(final EntityManager em, final Class<T> type, final String column, final String order, final Object value) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<T> query = cb.createQuery(type);
        final Root<T> root = query.from(type);
        query.select(root).where(cb.equal(root.get(column), value));
        if (order != null) query.orderBy(cb.asc(root.get(order)));
        return em.createQuery(query).getResultList();
    }

    public static <T extends TableModel> List<T> search(final EntityManager em, final Class<T> type, final String column, final String value) {
        return SQLUtils.search(em, type, column, null, value);
    }

    public static <T extends TableModel> List<T> search(final EntityManager em, final Class<T> type, final String column, final String order, final String value) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<T> query = cb.createQuery(type);
        final Root<T> root = query.from(type);
        query.select(root).where(cb.like(cb.lower(root.get(column)), value.toLowerCase()));
        if (order != null) query.orderBy(cb.asc(root.get(order)));
        return em.createQuery(query).getResultList();
    }
}
