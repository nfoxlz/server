package com.compete.mis.repositories;

public final class SqlConfig {

    private boolean useTransaction;

    public boolean isUseTransaction() {
        return useTransaction;
    }

    public void setUseTransaction(boolean useTransaction) {
        this.useTransaction = useTransaction;
    }
}
