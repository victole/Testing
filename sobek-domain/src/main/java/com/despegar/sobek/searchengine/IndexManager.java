package com.despegar.sobek.searchengine;

import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

public interface IndexManager {

    public QueryResponse getBenefitsByOIDs(List<Long> benefitOIDs);

    public void deleteIndex(String query);

    public QueryResponse query(SolrQuery query);

    void saveIndex(Collection<SolrInputDocument> docs);

    void saveObject(List<?> object);
    
}
