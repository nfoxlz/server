package com.compete.mis.models.viewmodels;

public abstract class ActionDataParameter extends DataParameter {

    private byte[] actionId;

    public byte[] getActionId() {
        return actionId;
    }

    public void setActionId(byte[] actionId) {
        this.actionId = actionId;
    }
}
