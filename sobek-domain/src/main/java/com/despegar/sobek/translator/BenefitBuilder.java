package com.despegar.sobek.translator;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.strategy.URLGenerationStrategy;

public class BenefitBuilder {

    private Map<String, URLGenerationStrategy> urlGenerationStrategies;
    private SolrBenefitTranslator solrBenefitTranslator;
    private Map<String, String> brandLanguageMap;

    private static Logger logger = Logger.getLogger(BenefitBuilder.class);

    public BenefitContainerDTO build(QueryResponse search, BenefitFilterContainerDTO benefitFilterContainerDTO) {

        String languageCode = this.getLanguageCode(benefitFilterContainerDTO);
        String countryCode = this.getCountryCode(benefitFilterContainerDTO);

        logger.info("Building benefitDTO list from solr query and search parameters");

        Date checkInDate = new Date(Long.MAX_VALUE);
        Date checkOutDate = new Date(0);

        Date dateFrom;
        Date dateTo;
        Set<Long> destinationOIDs = new HashSet<Long>();
        // Se recorren los filtros para obtener el listado de destinos y las fechas de checkin y checkout
        for (BenefitFilterDTO benefitFilterDTO : benefitFilterContainerDTO.getBenefitFilterDTOs()) {

            destinationOIDs.add(benefitFilterDTO.getDestinationOID());

            dateFrom = new Date(benefitFilterDTO.getDateFrom());
            if (dateFrom.before(checkInDate)) {
                checkInDate = dateFrom;
            }

            if (benefitFilterDTO.getDateTo() != null) {
                dateTo = new Date(benefitFilterDTO.getDateTo());

                if (dateTo.after(checkOutDate)) {
                    checkOutDate = dateTo;
                }
            }
        }

        String fullName = benefitFilterContainerDTO.getClientDTO().obtainFullName();

        BenefitContainerDTO benefitContainer = this.solrBenefitTranslator.buildBenefitList(search);

        for (BenefitDTO benefit : benefitContainer.getBenefitDTOs()) {
            logger.info(StringUtils.concat("Generating external resource url for benefitOID ", benefit.getOID()));
            URLGenerationStrategy urlGenerationStrategy = this.getStrategy(benefit.getLinkTemplateType());
            String url = urlGenerationStrategy.generateURL(benefit, destinationOIDs, languageCode, fullName, checkInDate,
                checkOutDate, countryCode);
            if (url != null) {
                benefit.getExternalResources().put(languageCode, url);
            }
        }

        return benefitContainer;
    }

    private String getLanguageCode(BenefitFilterContainerDTO benefitFilterContainerDTO) {
        String languageCode = null;
        if (benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().hasNext()) {
            languageCode = this.brandLanguageMap.get(benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next()
                .getBrandCode());
        }
        if (languageCode == null) {
            languageCode = this.brandLanguageMap.get("DEFAULT");
        }
        return languageCode;
    }

    private String getCountryCode(BenefitFilterContainerDTO benefitFilterContainerDTO) {
        String languageCode = null;
        if (benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().hasNext()) {
            return benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().getBrandCode();
        }
        return languageCode;
    }


    private URLGenerationStrategy getStrategy(String code) {
        if (this.urlGenerationStrategies.containsKey(code)) {
            return this.urlGenerationStrategies.get(code);
        }
        return this.urlGenerationStrategies.get("DEFAULT");
    }

    public void setUrlGenerationStrategies(Map<String, URLGenerationStrategy> urlGenerationStrategies) {
        this.urlGenerationStrategies = urlGenerationStrategies;
    }

    public void setSolrBenefitTranslator(SolrBenefitTranslator solrBenefitTranslator) {
        this.solrBenefitTranslator = solrBenefitTranslator;
    }

    public void setBrandLanguageMap(Map<String, String> brandLanguageMap) {
        this.brandLanguageMap = brandLanguageMap;
    }

}
