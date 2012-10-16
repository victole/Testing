package com.despegar.sobek.searchengine;

public class Conjunction
    extends Junction {

    @Override
    public String getOperation() {
        return " AND ";
    }
}
