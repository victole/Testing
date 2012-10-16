package com.despegar.sobek.strategy;

import java.util.Date;
import java.util.Set;

import com.despegar.sobek.dto.BenefitDTO;

public interface URLGenerationStrategy {

    public String generateURL(BenefitDTO benefit, Set<Long> destinationIds, String languageCode, String fullName,
        Date checkin, Date checkout, String countryCode);

}
