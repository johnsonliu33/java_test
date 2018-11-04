package com.bitspace.food.controller.db;

import org.apache.ibatis.session.SqlSession;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.controller.db
 * @class_name
 * @auth erik
 * @create_time 18-3-14 下午4:12
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SessionContext {
    
    private SqlSession session = null;
    
    public SqlSession getSession() {
        if (session == null) {
            session = DataSourceUtil.getSqlSessionFactory().openSession();
            DataSourceUtil.setUtf8mb4(session);
        }
        return session;
    }
    
    public void commit() {
        if (session != null) {
            session.commit();
        }
    }
    
    public void rollback() {
        if (session != null) {
            session.rollback();
        }
    }
    
    public void close() {
        if (session != null) {
            session.close();
            session = null;
        }
    }
    
    public <T> T getMapper(Class<T> clazz) {
        return this.getSession().getMapper(clazz);
    }
}
