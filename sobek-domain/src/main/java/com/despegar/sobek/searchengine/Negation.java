package com.despegar.sobek.searchengine;

public class Negation
    extends Junction {

    @Override
    public String getOperation() {
        return " NOT ";
    }

}
