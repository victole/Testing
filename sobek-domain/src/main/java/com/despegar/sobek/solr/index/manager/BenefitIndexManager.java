package com.despegar.sobek.solr.index.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.GroupParams;

import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dao.BenefitIndexDAO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.dto.OrderDTO;
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.searchengine.AbstractIndexManager;
import com.despegar.sobek.searchengine.Conjunction;
import com.despegar.sobek.searchengine.Disjunction;
import com.despegar.sobek.searchengine.IntervalLeaf;
import com.despegar.sobek.searchengine.Junction;
import com.despegar.sobek.searchengine.Leaf;
import com.despegar.sobek.searchengine.Negation;
import com.despegar.sobek.solr.index.data.GeoAreaMemorySnapshot;
import com.despegar.sobek.solr.model.BenefitIndex;
import com.despegar.sobek.translator.BenefitIndexSolrTranslator;

public class BenefitIndexManager
    extends AbstractIndexManager {

    private static final Logger LOGGER = Logger.getLogger(BenefitIndexManager.class);

    private static final String GROUP_FIELD_COLUMN = "oidBenefit";
    private static final String APPLIES_ALL = "ALL";
    private static final String GEO_AREA_ALL = "0";
    private static final Integer GROUP_LIMIT = -1;
    private static final String QUERY_ALL = "*:*";
    private static final String FIELDS_ALL = "*";

    private BenefitIndexSolrTranslator benefitIndexSolrTranslator;
    private BenefitIndexDAO benefitIndexDAO;
    private GeoAreaMemorySnapshot geoAreaMemorySnapshot;
    private Map<String, String> mapSortValues;

    @Override
    public void index() {
        LOGGER.info("Se indexa");
        try {

            this.geoAreaMemorySnapshot.initSnapshot();
            List<Benefit> benefits = this.benefitIndexDAO.getBenefitIndexes(null);
            List<BenefitIndex> benefitIndexs = this.benefitIndexSolrTranslator.translatorList(benefits);
            this.deleteIndex("*:*");
            this.saveObject(benefitIndexs);

        } catch (Exception e) {
            LOGGER.error("Error en generar los indices", e);
            throw new RuntimeException(e);
        } finally {
            this.geoAreaMemorySnapshot.deleteSnapshot();
        }
        LOGGER.info("Termino la indexacion");
    }

    public void update(List<Long> benefitOIDs) {
        if (!benefitOIDs.isEmpty()) {
            LOGGER.info(StringUtils.concat("Se actualiza los oids: ",
                org.apache.commons.lang.StringUtils.join(benefitOIDs, "-")));

            List<Long> OIDs = this.getAppliandeByBenefit(benefitOIDs);
            List<Benefit> benefits = this.benefitIndexDAO.getBenefitIndexes(benefitOIDs);
            List<Long> deleteOIDs = this.getDeleteApplianceOIDs(OIDs, benefits);

            this.delete(deleteOIDs);

            List<BenefitIndex> benefitIndexs = this.benefitIndexSolrTranslator.translatorList(benefits);

            this.saveObject(benefitIndexs);
            LOGGER.info("Termino actualizacion");
        }
    }

    public void delete(List<Long> appliancesOIDs) {
        if (!appliancesOIDs.isEmpty()) {

            Junction delete = this.getDisjunction(appliancesOIDs, "oidAppliance");

            String query = delete.toString();
            if (query != StringUtils.EMTPY_STRING) {
                this.deleteIndex(query);
                LOGGER.info(StringUtils.concat("Se elimino de solr", query));
            }
        } else {
            LOGGER.info(StringUtils.concat("Lista vacia para eliminar en solr"));
        }
    }

    private List<Long> getDeleteApplianceOIDs(List<Long> OIDs, List<Benefit> benefits) {
        List<Long> deleteOIDs = new LinkedList<Long>();
        for (Long oid : OIDs) {
            Boolean delete = true;
            for (Benefit benefit : benefits) {
                for (Appliance appliance : benefit.getAppliance()) {
                    if (appliance.getOID().equals(oid)) {
                        delete = false;
                    }
                }
            }
            if (delete == true) {
                deleteOIDs.add(oid);
            }
        }
        return deleteOIDs;
    }

    private List<Long> getAppliandeByBenefit(List<Long> benefitOIDs) {
        List<Long> listOIDs = new LinkedList<Long>();

        Junction junction = this.getDisjunction(benefitOIDs, "oidBenefit");

        SolrQuery query = new SolrQuery();
        query.setStart(0);
        query.setRows(10000);
        query.setFields("oidAppliance");
        query.setQuery(junction.toString());
        QueryResponse queryResponse = this.query(query);
        if (queryResponse.getResults() != null) {
            for (SolrDocument document : queryResponse.getResults()) {
                listOIDs.add(Long.parseLong(document.getFirstValue("oidAppliance").toString()));
            }
        }
        return listOIDs;
    }

    @Override
    public QueryResponse getBenefitsByOIDs(List<Long> benefitOIDs) {
        Junction junction = this.getDisjunction(benefitOIDs, "oidBenefit");

        SolrQuery query = new SolrQuery();

        query.set(GroupParams.GROUP, true);
        query.set(GroupParams.GROUP_FIELD, GROUP_FIELD_COLUMN);
        query.set(GroupParams.GROUP_LIMIT, 1);
        query.set(GroupParams.GROUP_TOTAL_COUNT, true);

        query.setStart(0);
        query.setRows(10000);
        query.setFields("oidBenefit, linkVoucherES, linkVoucherPT");
        query.setQuery(junction.toString());
        QueryResponse queryResponse = this.query(query);

        return queryResponse;
    }


    public QueryResponse search(BenefitFilterContainerDTO benefitFilterDTO) {
        SolrQuery query = new SolrQuery();
        Integer countStart = (benefitFilterDTO.getPageNumber() - 1) * benefitFilterDTO.getPageSize();
        query.set(GroupParams.GROUP, true);
        query.set(GroupParams.GROUP_FIELD, GROUP_FIELD_COLUMN);
        query.set(GroupParams.GROUP_LIMIT, GROUP_LIMIT);
        query.set(GroupParams.GROUP_TOTAL_COUNT, true);
        query.setFields(FIELDS_ALL);
        query.setStart(countStart);
        query.setRows(benefitFilterDTO.getPageSize());
        if (!benefitFilterDTO.getOrders().isEmpty()) {
            for (OrderDTO orderList : benefitFilterDTO.getOrders()) {
                ORDER order = this.getOrder(orderList);
                query.addSortField(this.mapSortValues.get(orderList.getOrderBy().name()), order);
            }
        } else {
            query.addSortField("isOutstanding", ORDER.desc);
            query.addSortField("relevance", ORDER.asc);
            query.addSortField("publicationDate", ORDER.desc);
        }
        String queryString = this.generateQuery(benefitFilterDTO.getBenefitFilterDTOs());
        query.setQuery(queryString);
        return this.query(query);
    }

    public String generateQuery(Set<BenefitFilterDTO> benefitFilterDTOs) {
        String query = QUERY_ALL;
        Junction conjuntionBenefitFilterDTOs = new Disjunction();
        if (benefitFilterDTOs != null && !benefitFilterDTOs.isEmpty()) {
            for (BenefitFilterDTO benefitFilterDTO : benefitFilterDTOs) {
                Junction conjuntionPartial = new Conjunction();
                Junction conjuntionDateGlobal = null;
                Junction negationFrom = null;
                Junction negationTo = null;
                if (benefitFilterDTO.getDateFrom() != null && benefitFilterDTO.getDateTo() != null) {
                    conjuntionDateGlobal = new Disjunction();
                    conjuntionDateGlobal.addLeaf(this.getDateQuery(benefitFilterDTO.getDateTo()));
                    conjuntionDateGlobal.addLeaf(this.getDateQuery(benefitFilterDTO.getDateFrom()));
                    conjuntionDateGlobal.addLeaf(this.getDateQueryBetween(benefitFilterDTO.getDateFrom(),
                        benefitFilterDTO.getDateTo()));
                } else {
                    negationTo = this.getDateQuery(benefitFilterDTO.getDateTo());
                    negationFrom = this.getDateQuery(benefitFilterDTO.getDateFrom());
                }
                Junction conjuntions = this.getConjuntions(benefitFilterDTO);
                if (conjuntions != null) {
                    conjuntionPartial.addLeaf(conjuntions);
                }
                if (conjuntionDateGlobal != null) {
                    conjuntionPartial.addLeaf(conjuntionDateGlobal);
                } else if (negationFrom != null) {
                    conjuntionPartial.addLeaf(negationFrom);
                } else if (negationTo != null) {
                    conjuntionPartial.addLeaf(negationTo);
                }
                if (!benefitFilterDTO.getAppliesTo().isEmpty()) {
                    benefitFilterDTO.getAppliesTo().add(APPLIES_ALL);
                    conjuntionPartial.addLeaf(this.getDisjunction(benefitFilterDTO.getAppliesTo(), "product"));
                }

                if (!benefitFilterDTO.getBenefitStatusCodes().isEmpty()) {
                    conjuntionPartial.addLeaf(this.getDisjunction(benefitFilterDTO.getBenefitStatusCodes(), "state"));
                }

                if (benefitFilterDTO.getBrandCode() != null) {
                    List<String> brands = new LinkedList<String>();
                    brands.add(APPLIES_ALL);
                    brands.add(benefitFilterDTO.getBrandCode());
                    conjuntionPartial.addLeaf(this.getDisjunction(brands, "brand"));
                }

                if (conjuntionPartial.toString() != StringUtils.EMTPY_STRING) {
                    conjuntionBenefitFilterDTOs.addLeaf(conjuntionPartial);
                }
            }
            if (conjuntionBenefitFilterDTOs.toString() != StringUtils.EMTPY_STRING) {
                query = conjuntionBenefitFilterDTOs.toString();
            }
        }
        LOGGER.info(StringUtils.concat("Query de solr: ", query));
        return query;
    }

    private ORDER getOrder(OrderDTO orderDTO) {
        ORDER order = ORDER.asc;
        if (orderDTO.getOrderDirection().name().equals("ASC")) {
            order = ORDER.asc;
        }
        if (orderDTO.getOrderDirection().name().equals("DESC")) {
            order = ORDER.desc;
        }
        return order;
    }

    private Junction getConjuntions(BenefitFilterDTO benefitFilterDTO) {
        Junction conjuntion = new Conjunction();
        if (benefitFilterDTO.getCategoryCode() != null) {
            conjuntion.addLeaf(this.getConjuntion(benefitFilterDTO.getCategoryCode(), "category"));
        }
        if (benefitFilterDTO.getCompanyOID() != null) {
            conjuntion.addLeaf(this.getConjuntion(benefitFilterDTO.getCompanyOID().toString(), "company"));
        }

        if (benefitFilterDTO.getDestinationType() != null && benefitFilterDTO.getDestinationOID() != null) {

            List<String> destinations = new LinkedList<String>();
            destinations.add(GEO_AREA_ALL);
            destinations.add(benefitFilterDTO.getDestinationOID().toString());
            if (benefitFilterDTO.getDestinationType().equalsIgnoreCase("c")) {
                conjuntion.addLeaf(this.getDisjunction(destinations, "citiesTo"));
            } else {
                conjuntion.addLeaf(this.getDisjunction(destinations, "countryTo"));
            }
        }

        if (benefitFilterDTO.getOriginType() != null && benefitFilterDTO.getOriginOID() != null) {
            List<String> origin = new LinkedList<String>();
            origin.add(GEO_AREA_ALL);
            origin.add(benefitFilterDTO.getOriginOID().toString());
            if (benefitFilterDTO.getOriginType().equalsIgnoreCase("c")) {
                conjuntion.addLeaf(this.getDisjunction(origin, "citiesFrom"));
            } else {
                conjuntion.addLeaf(this.getDisjunction(origin, "countryFrom"));
            }
        }
        if (conjuntion.getLeaf().isEmpty()) {
            conjuntion = null;
        }
        return conjuntion;
    }

    private Junction getConjuntion(String property, String column) {
        Junction conjuntion = new Conjunction();
        Leaf category = new Leaf();
        category.setColumnKey(column);
        category.setColummValue(property);
        conjuntion.addLeaf(category);
        return conjuntion;
    }


    private Junction getDisjunction(List<?> properties, String column) {
        Junction disjunction = new Disjunction();
        for (Object data : properties) {
            Leaf appliesLeave = new Leaf();
            appliesLeave.setColumnKey(column);
            appliesLeave.setColummValue(data.toString());
            disjunction.addLeaf(appliesLeave);
        }
        return disjunction;
    }

    private Junction getDateQuery(Long dateEpoch) {
        Junction negation = null;
        if (dateEpoch != null) {
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateParser.setTimeZone(TimeZone.getTimeZone("Z"));

            Date date = new Date(dateEpoch);
            String dateString = dateParser.format(date);

            IntervalLeaf intervalLeafDateFrom = new IntervalLeaf();
            intervalLeafDateFrom.setColumnKey("dateFrom");
            intervalLeafDateFrom.setColummValueInf("*");
            intervalLeafDateFrom.setColummValueSup(dateString);

            IntervalLeaf intervalLeafDateTo = new IntervalLeaf();
            intervalLeafDateTo.setColumnKey("dateTo");
            intervalLeafDateTo.setColummValueInf("*");
            intervalLeafDateTo.setColummValueSup(StringUtils.concat(dateString, "-1MINUTE"));

            negation = new Negation();
            negation.addLeaf(intervalLeafDateFrom);
            negation.addLeaf(intervalLeafDateTo);
        }
        return negation;
    }

    private Junction getDateQueryBetween(Long dateFromEpoch, Long dateToEpoch) {
        Junction conjunction = null;
        if (dateFromEpoch != null && dateToEpoch != null) {

            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateParser.setTimeZone(TimeZone.getTimeZone("Z"));

            Date date = new Date(dateFromEpoch);
            String dateFromString = dateParser.format(date);
            date = new Date(dateToEpoch);
            String dateToString = dateParser.format(date);

            IntervalLeaf intervalLeafDateFrom = new IntervalLeaf();
            intervalLeafDateFrom.setColumnKey("dateFrom");
            intervalLeafDateFrom.setColummValueInf(dateFromString);
            intervalLeafDateFrom.setColummValueSup(StringUtils.concat(dateToString, "-1MINUTE"));

            IntervalLeaf intervalLeafDateTo = new IntervalLeaf();
            intervalLeafDateTo.setColumnKey("dateTo");
            intervalLeafDateTo.setColummValueInf(dateFromString);
            intervalLeafDateTo.setColummValueSup(StringUtils.concat(dateToString, "-1MINUTE"));

            conjunction = new Conjunction();
            conjunction.addLeaf(intervalLeafDateFrom);
            conjunction.addLeaf(intervalLeafDateTo);

        }
        return conjunction;

    }

    public void setBenefitIndexSolrTranslator(BenefitIndexSolrTranslator benefitIndexSolrTranslator) {
        this.benefitIndexSolrTranslator = benefitIndexSolrTranslator;
    }

    public void setBenefitIndexDAO(BenefitIndexDAO benefitIndexDAO) {
        this.benefitIndexDAO = benefitIndexDAO;
    }

    public void setGeoAreaMemorySnapshot(GeoAreaMemorySnapshot geoAreaMemorySnapshot) {
        this.geoAreaMemorySnapshot = geoAreaMemorySnapshot;
    }

    public void setMapSortValues(Map<String, String> mapSortValues) {
        this.mapSortValues = mapSortValues;
    }
}
