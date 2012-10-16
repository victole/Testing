package com.despegar.sobek.utility;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.meetup.memcached.test.UnitTests;

public class RedirectHashGeneratorTest
    extends UnitTests {

    private Set<Long> oneItem = new HashSet<Long>();
    private Set<Long> differentOneItem = new HashSet<Long>();
    private Set<Long> manyItem = new HashSet<Long>();
    private Set<Long> differentManyItem = new HashSet<Long>();
    private RedirectHashGenerator hashGenerator;


    @Before
    public void populateDestinations() {
        this.hashGenerator = new RedirectHashGenerator();
        this.hashGenerator.setPrivateKey("thePrivatekey");

        this.oneItem.add(982L);

        this.differentOneItem.add(4544L);

        this.manyItem.add(123L);
        this.manyItem.add(321L);
        this.manyItem.add(222L);

        this.differentManyItem.add(345L);
        this.differentManyItem.add(234L);
        this.differentManyItem.add(123L);
    }

    @Test
    public void compareHash_oneDestination_FAILURE() {
        String resultA = this.hashGenerator.getHashRedirection("portugues", this.oneItem, "arielIbagaza", "2011-02-02",
            "2011-02-25");
        String resultB = this.hashGenerator.getHashRedirection("portugues", this.differentOneItem, "arielIbagaza",
            "2011-02-02", "2011-02-25");
        Assert.assertTrue(!resultA.equals(resultB));
    }

    @Test
    public void compareHash_oneDestination_SUCCESFUL() {
        String resultA = this.hashGenerator.getHashRedirection("portugues", this.oneItem, "arielIbagaza", "2011-02-02",
            "2011-02-25");
        String resultB = this.hashGenerator.getHashRedirection("portugues", this.oneItem, "arielIbagaza", "2011-02-02",
            "2011-02-25");
        Assert.assertTrue(resultA.equals(resultB));
    }

    @Test
    public void compareHash_manyDestination_SUCCESFUL() {
        String resultA = this.hashGenerator.getHashRedirection("portugues", this.manyItem, "arielIbagaza", null, null);
        String resultB = this.hashGenerator.getHashRedirection("portugues", this.manyItem, "arielIbagaza", null, null);
        Assert.assertTrue(resultA.equals(resultB));
    }

    @Test
    public void compareHash_manyDestination_FAILURE() {
        Set<Long> destinationsA = new HashSet<Long>();
        destinationsA.add(345L);
        destinationsA.add(234L);
        destinationsA.add(123L);

        Set<Long> destinationsB = new HashSet<Long>();
        destinationsB.add(123L);
        destinationsB.add(423L);
        destinationsB.add(111L);

        String resultA = this.hashGenerator.getHashRedirection("portugues", destinationsA, "arielIbagaza", null, null);
        String resultB = this.hashGenerator.getHashRedirection("portugues", destinationsB, "arielIbagaza", null, null);
        Assert.assertTrue(!resultA.equals(resultB));

    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_emptyLangugageArguments_FAILURE() {
        this.hashGenerator.getHashRedirection("", this.oneItem, "arielIbagaza", "2011-10-07", "2011-10-08");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_nullLangugageArguments_FAILURE() {
        this.hashGenerator.getHashRedirection(null, this.oneItem, "arielIbagaza", "2011-10-07", "2011-10-08");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_nullDestinationArguments_FAILURE() {
        this.hashGenerator.getHashRedirection("español", null, "arielIbagaza", "2011-10-10", "2011-02-17");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_emptyLanguageList_FAILURE() {
        this.hashGenerator.getHashRedirection("", this.oneItem, "arielIbagaza", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_emptyDestinationListArguments_FAILURE() {
        this.hashGenerator.getHashRedirection("portugues", new HashSet<Long>(), "arielIbagaza", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_nullLanguageManyDestintion_FAILURE() {
        this.hashGenerator.getHashRedirection(null, this.manyItem, "arielIbagaza", null, null);
    }

    @Test
    public void getHash_nullFullnameList_FAILURE() {
        this.hashGenerator.getHashRedirection("español", this.manyItem, null, "2011", "2012");
    }

    @Test
    public void getHash_nullFullname_FAILURE() {
        this.hashGenerator.getHashRedirection("español", this.oneItem, null, "2011", "2012");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHash_nullDestinationManyDestintion_FAILURE() {
        this.hashGenerator.getHashRedirection("portugues", null, "arielIbagaza", "2011", "2012");
    }



}
