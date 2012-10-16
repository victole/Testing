package com.despegar.sobek.searchengine;

import java.util.LinkedList;
import java.util.List;

import com.despegar.framework.utils.string.StringUtils;

public abstract class Junction {

    private List<Junction> leaf;

    public abstract String getOperation();

    public void setLeaf(List<Junction> leaf) {
        this.leaf = leaf;
    }

    public List<Junction> getLeaf() {
        if (this.leaf == null) {
            this.leaf = new LinkedList<Junction>();
        }
        return this.leaf;
    }

    public void addLeaf(Junction junction) {
        this.getLeaf().add(junction);
    }

    @Override
    public String toString() {
        String query = StringUtils.EMTPY_STRING;
        if (this.leaf != null) {
            query = "(";
            Integer count = 0;
            for (Junction junction : this.leaf) {
                if (count.equals(0)) {
                    query = StringUtils.concat(query, junction.toString());
                } else {
                    query = StringUtils.concat(query, this.getOperation(), junction.toString());
                }
                count++;
            }
            query = StringUtils.concat(query, ")");
        }
        return query;
    }
}
