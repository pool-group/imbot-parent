package com.yunli.imbot.core.service.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by duanp on 2020/7/25.
 */
public class AfterDataBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String id;
    private double score;

    public AfterDataBean(String id, double score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
