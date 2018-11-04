package com.bitspace.food.mapper;

import com.bitspace.food.entity.Admin;
import com.bitspace.food.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 14:29
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AdminMapper {
    
    /**
     * 查询所有商户信息
     * @return
     */
    @Select("SELECT t.aid,t.name,t.login_name loginName,t.login_pwd loginPwd,t.type,t.status,t.recall_num recallNum," +
            " t.report_num reportNum,t.create_time createTime,t.last_login_time lastLoginTime,t.update_time updateTime" +
            " FROM t_admin t ")
    List<Admin> selectAll();
    
    /**
     * 查询所有商户（aid，与账户）信息
     * @return
     */
    @Select("SELECT t.aid,t.login_name loginName FROM t_admin t ")
    List<Admin> selectAllLoginName();
    
    /**
     * 添加用户
     * @param admin
     * @return
     */
    @Insert("INSERT INTO t_admin(aid, name, login_name, login_pwd, TYPE, STATUS, create_time)" +
            "  values(#{aid}, #{name}, #{loginName}, #{loginPwd}, #{type}, #{status}, #{createTime}) "
    )
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "aid", before = false, resultType = Long.class)
    int insertAdmin(Admin admin);

    /**
     * 冻结/解冻
     * @param map
     * @return
     */
    @Update("UPDATE t_admin t SET t.status = #{status},t.update_time = #{updateTime} WHERE t.aid = #{aid}")
    int updateStatusByAid(Map<String, Object> map);

    /**
     * 审核通过/审核不通过
     * @param map
     * @return
     */
    @Update("UPDATE t_admin t SET t.status = #{status},t.update_time = #{updateTime},t.audit_opinion = #{auditOpinion} WHERE t.aid = #{aid}")
    int updateAuditStatusByAid(Map<String, Object> map);
    
    /**
     * 批量修改召回次数
     * @param list
     * @return
     */
    int batchUpdateRecallNum(List<Long> list);
    
    /**
     * 批量修改举报次数
     * @param list
     * @return
     */
    int batchUpdateReportNum(List<Long> list);
}
