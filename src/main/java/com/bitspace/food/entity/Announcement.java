package com.bitspace.food.entity;

import java.util.Date;

/**
 * 公告
 *
 * @author saturn
 */
public class Announcement {

    private int id;
    private String title;
    private String body;
    private Long createTime;
    private int timesView;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getTimesView() {
        return timesView;
    }

    public void setTimesView(int timesView) {
        this.timesView = timesView;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
