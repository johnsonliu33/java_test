package com.bitspace.food.mapper;

import com.bitspace.food.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface AnnouncementMapper {

    // 查看所有公告,首页展示
    @Select({"select id, title, create_time AS createTime from t_announcement where status = 0 order by create_time desc limit #{startNumber}, #{endNumber}"})
    List<LinkedHashMap<String, Object>> listAnnouncement(Map map);

    //查看所有公告数量
    Long listAnnouncementDetailCount(Map map);

    // 公告列表
    List<LinkedHashMap<String, Object>> listAnnouncementDetail(Map map);

    // 根据id查询公告内容
    @Select({"SELECT title, body, create_time AS createTime, times_view AS timesView  FROM t_announcement where id = #{id}"})
    List<LinkedHashMap<String, Object>> getAnnouncementBodyById(@Param("id") int id);

    // 新增公告
    @Insert({"insert into t_announcement(title, body, create_time)" +
            "values (#{title}, #{body}, #{createTime})"})
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    int addAnnouncement(Announcement announcement);

    @Update({"update t_announcement set status = #{status} where id = #{id}"})
    int updateAnnouncementWithStatusById(Map map);

    @Delete({"delete from t_announcement where id = #{id}"})
    int deleteAnnouncementById(@Param("id") int id);

    @Update({"update t_announcement set times_view = (times_view + 1) where id = #{id}"})
    int updateAnnouncementTimesViewById(@Param("id") int id);
}
