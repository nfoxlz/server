package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class Menu implements Serializable {

    private long menuNo;

    private long parentMenuNo;

    private long sn;

    private String displayName;

    private String toolTip;

    private String pluginSetting;

    private String pluginParameter;

    private long authorition;

    public long getMenuNo() {
        return menuNo;
    }

    public void setMenuNo(long menuNo) {
        this.menuNo = menuNo;
    }

    public long getParentMenuNo() {
        return parentMenuNo;
    }

    public void setParentMenuNo(long parentMenuNo) {
        this.parentMenuNo = parentMenuNo;
    }

    public long getSn() {
        return sn;
    }

    public void setSn(long sn) {
        this.sn = sn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getPluginSetting() {
        return pluginSetting;
    }

    public void setPluginSetting(String pluginSetting) {
        this.pluginSetting = pluginSetting;
    }

    public String getPluginParameter() {
        return pluginParameter;
    }

    public void setPluginParameter(String pluginParameter) {
        this.pluginParameter = pluginParameter;
    }

    public long getAuthorition() {
        return authorition;
    }

    public void setAuthorition(long authorition) {
        this.authorition = authorition;
    }
}
