package com.compete.mis.models.viewmodels;

import java.io.Serializable;

public final class SaveData implements Serializable {
    private SimpleDataTable addedTable;

    private SimpleDataTable deletedTable;

    private SimpleDataTable modifiedTable;

    private SimpleDataTable modifiedOriginalTable;

    public SimpleDataTable getAddedTable() {
        return addedTable;
    }

    public void setAddedTable(SimpleDataTable addedTable) {
        this.addedTable = addedTable;
    }

    public SimpleDataTable getDeletedTable() {
        return deletedTable;
    }

    public void setDeletedTable(SimpleDataTable deletedTable) {
        this.deletedTable = deletedTable;
    }

    public SimpleDataTable getModifiedTable() {
        return modifiedTable;
    }

    public void setModifiedTable(SimpleDataTable modifiedTable) {
        this.modifiedTable = modifiedTable;
    }

    public SimpleDataTable getModifiedOriginalTable() {
        return modifiedOriginalTable;
    }

    public void setModifiedOriginalTable(SimpleDataTable modifiedOriginalTable) {
        this.modifiedOriginalTable = modifiedOriginalTable;
    }
}
