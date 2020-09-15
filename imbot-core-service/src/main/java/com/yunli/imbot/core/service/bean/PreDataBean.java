package com.yunli.imbot.core.service.bean;

import java.io.Serializable;

/**
 * Created by duanp on 2020/7/25.
 */
public class PreDataBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
