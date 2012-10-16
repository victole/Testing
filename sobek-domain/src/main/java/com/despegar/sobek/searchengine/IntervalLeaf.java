package com.despegar.sobek.searchengine;

public class IntervalLeaf
    extends Junction {

    private String columnKey;
    private String colummValueInf;
    private String colummValueSup;

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnKey() {
        return this.columnKey;
    }

    public void setColummValueInf(String colummValueInf) {
        this.colummValueInf = colummValueInf;
    }

    public String getColummValueInf() {
        return this.colummValueInf;
    }

    public void setColummValueSup(String colummValueSup) {
        this.colummValueSup = colummValueSup;
    }

    public String getColummValueSup() {
        return this.colummValueSup;
    }

    @Override
    public String toString() {
        return "(" + this.columnKey + ":[" + this.getColummValueInf() + " TO " + this.getColummValueSup() + "]" + ")";
    }

    // key:[colummValue1 TO colummValue2]
    @Override
    public String getOperation() {
        return null;
    }

}
