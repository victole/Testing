package com.despegar.sobek.utility;

import java.math.BigDecimal;

import org.apache.solr.common.SolrDocument;

public class SolrFieldHelper {

    public static String getStringIfNotNull(SolrDocument solrDocument, String fieldName) {
        if (solrDocument.getFieldValue(fieldName) != null) {
            return solrDocument.getFieldValue(fieldName).toString();
        }
        return null;
    }

    public static Boolean getBooleanIfNotNull(SolrDocument solrDocument, String fieldName) {
        if (solrDocument.getFieldValue(fieldName) != null) {
            return new Boolean(solrDocument.getFieldValue(fieldName).toString());
        }
        return null;
    }

    public static Long getLongIfNotNull(SolrDocument solrDocument, String fieldName) {
        if (solrDocument.getFieldValue(fieldName) != null) {
            return new Long(solrDocument.getFieldValue(fieldName).toString());
        }
        return null;
    }

    public static BigDecimal getBigDecimalIfNotNull(SolrDocument solrDocument, String fieldName) {
        if (solrDocument.getFieldValue(fieldName) != null) {
            return new BigDecimal(solrDocument.getFieldValue(fieldName).toString());
        }
        return null;
    }

}
