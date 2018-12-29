package com.qunar.qchat.dao.model;

public class MucIncrementInfo {
    private String muc_name;
    private String domain;
    private String t;
    private int registed_flag;

    public String getMuc_name() {
        return muc_name;
    }

    public void setMuc_name(String muc_name) {
        this.muc_name = muc_name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getRegisted_flag() {
        return registed_flag;
    }

    public void setRegisted_flag(int registed_flag) {
        this.registed_flag = registed_flag;
    }
}
