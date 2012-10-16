package com.despegar.sobek.searchengine;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import com.despegar.framework.utils.string.StringUtils;


// TODO:SACAR ESTO AL FRAMEWORK
public abstract class AbstractIndexManager
    implements IndexManager {

    private SolrServer solrConnector;
    private final Logger logger = Logger.getLogger(AbstractIndexManager.class);

    public abstract void index();

    public QueryResponse getDocumentsByField(String field, Long value) {
        SolrQuery query = new SolrQuery();
        query.setQuery(StringUtils.concat(field, ":", value));
        query.setFields("*");
        return this.query(query);
    }

    @Override
    public QueryResponse query(SolrQuery query) {
        QueryResponse queryResponse;
        this.logger.info(StringUtils.concat("Busqueda Solr : ", query));
        try {
            queryResponse = this.solrConnector.query(query);

        } catch (Exception e) {
            this.logger.error(StringUtils.concat("Error al buscar en solr, se busco:", ", Exception: "), e);
            throw new RuntimeException(e);
        }
        return queryResponse;
    }

    @Override
    public void saveIndex(Collection<SolrInputDocument> docs) {
        try {
            this.solrConnector.add(docs);
            this.solrConnector.commit();
            this.logger.info(StringUtils.concat("Se indexo el contenido en SOLR"));
        } catch (Exception e) {
            this.logger.error("Error al guardar los registros en SOLR", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteIndex(String query) {
        try {
            this.solrConnector.deleteByQuery(query);
            this.solrConnector.commit();
            this.logger.info("Delete Indice");
        } catch (Exception e) {
            this.logger.error("Error al borrar un registro de SOLR", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveObject(List<?> object) {
        try {
            this.solrConnector.addBeans(object);
            this.solrConnector.commit();
            this.logger.info(StringUtils.concat("Se indexo el contenido en SOLR"));
        } catch (Exception e) {
            this.logger.error("Error al guardar los registros en SOLR", e);
            throw new RuntimeException(e);
        }
    }

    public void setSolrConnector(SolrServer solrConnector) {
        this.solrConnector = solrConnector;
    }

}
