package com.despegar.sobek.strategy;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.despegar.sobek.dto.BenefitDTO;

public class DefaultURLGenerationStrategy
    implements URLGenerationStrategy {

    private static Logger logger = Logger.getLogger(DefaultURLGenerationStrategy.class);

    @Override
    public String generateURL(BenefitDTO benefit, Set<Long> destinationIds, String languageCode, String fullName,
        Date checkin, Date checkout, String countryCode) {
        logger.info("Generating url with default strategy");
        return benefit.getExternalResources().get(languageCode);
    }

}
