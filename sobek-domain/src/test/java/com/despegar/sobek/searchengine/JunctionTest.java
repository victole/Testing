package com.despegar.sobek.searchengine;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JunctionTest {

    Leaf leave1 = new Leaf();
    Leaf leave2 = new Leaf();

    @Before
    public void setUp() {

        this.leave1.setColumnKey("columnKey1");
        this.leave1.setColummValue("columna1");

        this.leave2.setColumnKey("columnKey2");
        this.leave2.setColummValue("columna2");

    }


    @Test
    public void junction_creationQueryIntervalLeaf_QueryStringSolr() {
        Junction intervalLeaf = new Negation();
        IntervalLeaf intervalLeafDateFrom = new IntervalLeaf();
        intervalLeafDateFrom.setColumnKey("dateFrom");
        intervalLeafDateFrom.setColummValueInf("*");
        intervalLeafDateFrom.setColummValueSup("1000");

        IntervalLeaf intervalLeafDateTo = new IntervalLeaf();
        intervalLeafDateTo.setColumnKey("dateTo");
        intervalLeafDateTo.setColummValueInf("*");
        intervalLeafDateTo.setColummValueSup("2000");

        intervalLeaf = new Negation();
        intervalLeaf.addLeaf(intervalLeafDateFrom);
        intervalLeaf.addLeaf(intervalLeafDateTo);
        Assert.assertEquals("((dateFrom:[* TO 1000]) NOT (dateTo:[* TO 2000]))", intervalLeaf.toString());
    }

    @Test
    public void junction_creationQueryOr_QueryStringSolr() {


        Junction primerOR = new Disjunction();
        List<Junction> leaf = new LinkedList<Junction>();
        leaf.add(this.leave1);
        leaf.add(this.leave2);
        primerOR.setLeaf(leaf);


        Assert.assertEquals("(columnKey1:columna1 OR columnKey2:columna2)", primerOR.toString());

    }

    @Test
    public void junction_creationQueryNegation_QueryStringSolr() {
        Junction primerAND = new Conjunction();
        primerAND.addLeaf(this.leave1);
        primerAND.addLeaf(this.leave2);
        Junction primerOR = new Disjunction();
        List<Junction> leaf = new LinkedList<Junction>();
        leaf.add(this.leave1);
        leaf.add(this.leave2);
        primerOR.setLeaf(leaf);

        Junction negation = new Negation();
        negation.addLeaf(primerOR);
        negation.addLeaf(primerAND);

        Assert.assertEquals(
            "((columnKey1:columna1 OR columnKey2:columna2) NOT (columnKey1:columna1 AND columnKey2:columna2))",
            negation.toString());
    }

    @Test
    public void junction_creationQueryAnd_QueryStringSolr() {
        Junction primerAND = new Conjunction();
        primerAND.addLeaf(this.leave1);
        primerAND.addLeaf(this.leave2);
        Assert.assertEquals("(columnKey1:columna1 AND columnKey2:columna2)", primerAND.toString());
    }

    @Test
    public void junction_creationQueryDisjunction_QueryStringSolr() {
        Junction primerOR = new Disjunction();
        List<Junction> leaf = new LinkedList<Junction>();
        leaf.add(this.leave1);
        leaf.add(this.leave2);
        primerOR.setLeaf(leaf);
        Junction primerAND = new Conjunction();
        primerAND.addLeaf(this.leave1);
        primerAND.addLeaf(this.leave2);
        Junction primerBigOr = new Disjunction();
        primerBigOr.addLeaf(primerOR);
        primerBigOr.addLeaf(primerAND);

        Assert.assertEquals(
            "((columnKey1:columna1 OR columnKey2:columna2) OR (columnKey1:columna1 AND columnKey2:columna2))",
            primerBigOr.toString());
    }

    @Test
    public void junction_creationQueryConjunction_QueryStringSolr() {
        Junction primerOR = new Disjunction();
        List<Junction> leaf = new LinkedList<Junction>();
        leaf.add(this.leave1);
        leaf.add(this.leave2);
        primerOR.setLeaf(leaf);
        Junction primerAND = new Conjunction();
        primerAND.addLeaf(this.leave1);
        primerAND.addLeaf(this.leave2);

        Junction primerBigAND = new Conjunction();
        primerBigAND.addLeaf(primerOR);
        primerBigAND.addLeaf(primerAND);
        Assert.assertEquals(
            "((columnKey1:columna1 OR columnKey2:columna2) AND (columnKey1:columna1 AND columnKey2:columna2))",
            primerBigAND.toString());
    }

    @Test
    public void junction_creationQuery_QueryStringSolr() {


        Junction primerOR = new Disjunction();
        List<Junction> leaf = new LinkedList<Junction>();
        leaf.add(this.leave1);
        leaf.add(this.leave2);
        primerOR.setLeaf(leaf);

        Junction primerAND = new Conjunction();
        primerAND.addLeaf(this.leave1);
        primerAND.addLeaf(this.leave2);

        Junction primerBigOr = new Disjunction();
        primerBigOr.addLeaf(primerOR);
        primerBigOr.addLeaf(primerAND);

        Junction primerBigAND = new Conjunction();
        primerBigAND.addLeaf(primerOR);
        primerBigAND.addLeaf(primerAND);

        Junction primerSuperBigAND = new Conjunction();
        primerSuperBigAND.addLeaf(primerBigOr);
        primerSuperBigAND.addLeaf(primerBigAND);
        Junction primerSuperBigOR = new Disjunction();
        primerSuperBigOR.addLeaf(primerSuperBigAND);
        primerSuperBigOR.addLeaf(primerOR);
        Assert
            .assertEquals(
                "(((columnKey1:columna1 OR columnKey2:columna2) OR (columnKey1:columna1 AND columnKey2:columna2)) AND ((columnKey1:columna1 OR columnKey2:columna2) AND (columnKey1:columna1 AND columnKey2:columna2)))",
                primerSuperBigAND.toString());
        Assert
            .assertEquals(
                "((((columnKey1:columna1 OR columnKey2:columna2) OR (columnKey1:columna1 AND columnKey2:columna2)) AND ((columnKey1:columna1 OR columnKey2:columna2) AND (columnKey1:columna1 AND columnKey2:columna2))) OR (columnKey1:columna1 OR columnKey2:columna2))",
                primerSuperBigOR.toString());
    }

}
