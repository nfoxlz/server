package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class Menu implements Serializable {

    private long menuNo;

    private long parentMenuNo;

    private String displayName;

    private String tooltip;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
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
