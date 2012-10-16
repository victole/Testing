package com.despegar.sobek.strategy;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.client.CityClient;
import com.despegar.geodespegar.dto.CityDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.utility.RedirectHashGenerator;
import com.google.common.base.Joiner;

public class HashcodeURLGenerationStrategy
    implements URLGenerationStrategy {

    private static Logger logger = Logger.getLogger(HashcodeURLGenerationStrategy.class);

    private String baseUrl;
    private String singleDestinationQueryString;
    private String multipleDestinationQueryString;
    private RedirectHashGenerator redirectHashGenerator;
    private CityClient cityClient;

    @Override
    public String generateURL(BenefitDTO benefit, Set<Long> destinationIds, String languageCode, String fullName,
        Date checkIn, Date checkOut, String countryCode) {

        logger.info("Generating url with hashcode strategy");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String checkinDate = sdf.format(checkIn);
        String checkoutDate = null;

        if (checkOut != null) {
            checkoutDate = sdf.format(checkOut);
        }

        String hash = this.redirectHashGenerator.getHashRedirection(languageCode.toLowerCase(), destinationIds, fullName,
            checkinDate, checkoutDate);

        String generatedURL;
        String queryString;
        String destination = Joiner.on(",").join(destinationIds);
        if (destinationIds.size() > 1) {
            logger.info("Generating url for multiple destinations");
            benefit
                .getBenefitInformation()
                .get(languageCode)
                .setDescription(
                    MessageFormat.format(benefit.getBenefitInformation().get(languageCode).getDescription(),
                        StringUtils.EMTPY_STRING));
            benefit
                .getBenefitInformation()
                .get(languageCode)
                .setTitle(
                    MessageFormat.format(benefit.getBenefitInformation().get(languageCode).getTitle(),
                        StringUtils.EMTPY_STRING));

            queryString = MessageFormat.format(this.baseUrl, this.multipleDestinationQueryString);
            generatedURL = MessageFormat.format(queryString, languageCode.toLowerCase(), destination,
                fullName.replace(" ", "%20"), hash, countryCode);
        } else {
            logger.info("Generating url for single destination");
            this.getDestinationNameFromGeo(benefit, destinationIds.iterator().next(), languageCode);

            queryString = MessageFormat.format(this.baseUrl, this.singleDestinationQueryString);
            generatedURL = MessageFormat.format(queryString, languageCode.toLowerCase(), destination,
                fullName.replace(" ", "%20"), checkinDate, checkoutDate, hash, countryCode);
        }

        return generatedURL;
    }


    private void getDestinationNameFromGeo(BenefitDTO benefit, Long destinationOID, String languageCode) {
        logger.info(StringUtils.concat("Getting destination's name from GeoDespegar - destinationOID: ", destinationOID,
            " languageCode: ", languageCode));
        CityDTO cityDTO = this.cityClient.getCityByCityOID(destinationOID).getData();
        BenefitInformationDTO benefitInformationDTO = benefit.getBenefitInformation().get(languageCode);
        String description = benefitInformationDTO.getDescription();
        String title = benefitInformationDTO.getTitle();
        logger.info(StringUtils.concat("Setting city name (", cityDTO.getName(languageCode), ") to benefit description"));
        benefit.getBenefitInformation().get(languageCode)
            .setDescription(MessageFormat.format(description, StringUtils.concat("de ", cityDTO.getName(languageCode))));
        benefit.getBenefitInformation().get(languageCode)
            .setTitle(MessageFormat.format(title, StringUtils.concat("de ", cityDTO.getName(languageCode))));
    }


    public void setRedirectHashGenerator(RedirectHashGenerator redirectHashGenerator) {
        this.redirectHashGenerator = redirectHashGenerator;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setSingleDestinationQueryString(String singleDestinationQueryString) {
        this.singleDestinationQueryString = singleDestinationQueryString;
    }

    public void setMultipleDestinationQueryString(String multipleDestinationQueryString) {
        this.multipleDestinationQueryString = multipleDestinationQueryString;
    }

    public void setCityClient(CityClient cityClient) {
        this.cityClient = cityClient;
    }

}
