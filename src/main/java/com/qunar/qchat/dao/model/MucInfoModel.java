package com.qunar.qchat.dao.model;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/1 11:44
 */

public class MucInfoModel {

    private String mucName;
    private String showName;
    private String mucDesc;
    private String mucTitle;
    private String mucPic;
    private String version;

    public String getMucName() {
        return mucName;
    }

    public void setMucName(String mucName) {
        this.mucName = mucName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getMucDesc() {
        return mucDesc;
    }

    public void setMucDesc(String mucDesc) {
        this.mucDesc = mucDesc;
    }

    public String getMucTitle() {
        return mucTitle;
    }

    public void setMucTitle(String mucTitle) {
        this.mucTitle = mucTitle;
    }

    public String getMucPic() {
        return mucPic;
    }

    public void setMucPic(String mucPic) {
        this.mucPic = mucPic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "MucInfoModel{" +
                "mucName='" + mucName + '\'' +
                ", showName='" + showName + '\'' +
                ", mucDesc='" + mucDesc + '\'' +
                ", mucTitle='" + mucTitle + '\'' +
                ", mucPic='" + mucPic + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
