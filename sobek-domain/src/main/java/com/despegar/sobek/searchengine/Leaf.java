package com.despegar.sobek.searchengine;

public class Leaf
    extends Junction {

    private String columnKey;
    private String colummValue;

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnKey() {
        return this.columnKey;
    }

    public void setColummValue(String colummValue) {
        this.colummValue = colummValue;
    }

    public String getColummValue() {
        return this.colummValue;
    }

    @Override
    public String toString() {
        return this.columnKey + ":" + this.colummValue;
    }

    @Override
    public String getOperation() {

        return null;
    }


}
