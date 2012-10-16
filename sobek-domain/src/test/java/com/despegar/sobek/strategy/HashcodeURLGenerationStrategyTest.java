package com.despegar.sobek.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.spring.response.context.client.ResponseContainer;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.client.CityClient;
import com.despegar.geodespegar.dto.CityDTO;
import com.despegar.geodespegar.dto.KeyPairDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.utility.RedirectHashGenerator;
import com.google.common.collect.Lists;

public class HashcodeURLGenerationStrategyTest {

    private HashcodeURLGenerationStrategy instance = new HashcodeURLGenerationStrategy();
    private static final String BASE_URL = "http://www.guiomatic.com/guias?{0}";
    private String SINGLE_DESTINATION_QUERY_STRING = "idioma={0}&destino={1}&nomap={2}&checkin={3}&checkout={4}&hash={5}&origen={6}";
    private String MULTIPLE_DESTINATION_QUERY_STRING = "idioma={0}&destino={1}&nomap={2}&hash={3}&origen={4}";
    @Mock
    private RedirectHashGenerator redirectHashGeneratorMock;
    @Mock
    private CityClient cityClientMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance.setRedirectHashGenerator(this.redirectHashGeneratorMock);
        this.instance.setCityClient(this.cityClientMock);
        this.instance.setBaseUrl(BASE_URL);
        this.instance.setMultipleDestinationQueryString(this.MULTIPLE_DESTINATION_QUERY_STRING);
        this.instance.setSingleDestinationQueryString(this.SINGLE_DESTINATION_QUERY_STRING);
    }

    @Test
    public void generateURL_singleDestination_returnsURL() {
        BenefitDTO benefit = new BenefitDTO();
        Map<String, BenefitInformationDTO> benefitInformationMap = new HashMap<String, BenefitInformationDTO>();
        BenefitInformationDTO descriptionES = new BenefitInformationDTO();
        descriptionES.setDescription("Descripcion del beneficio {0} en español");
        descriptionES.setTitle("Titulo {0} en español");
        benefitInformationMap.put("ES", descriptionES);
        benefit.setBenefitInformation(benefitInformationMap);
        Set<Long> destinationIds = new HashSet<Long>();
        destinationIds.add(982L);
        String brandCode = "DESAR";
        String languageCode = "ES";
        String fullName = "Juan Perez";
        Date checkIn = new Date();
        Date checkOut = new Date();

        CityDTO data = new CityDTO();
        List<KeyPairDTO<String, String>> descriptions = Lists.newArrayList();
        descriptions.add(new KeyPairDTO<String, String>("ES", "Ciudad"));
        descriptions.add(new KeyPairDTO<String, String>("PT", "Cidade"));
        data.setNames(descriptions);
        ResponseContainer<CityDTO> responseContainer = new ResponseContainer<CityDTO>();
        responseContainer.setData(data);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String checkinDate = sdf.format(checkIn);
        String checkoutDate = sdf.format(checkOut);

        when(this.cityClientMock.getCityByCityOID(982L)).thenReturn(responseContainer);
        when(
            this.redirectHashGeneratorMock.getHashRedirection(languageCode.toLowerCase(), destinationIds, fullName,
                checkinDate, checkoutDate)).thenReturn("esteesunhashgeneradoapartirdelosdatosparametro");

        String generatedURL = this.instance.generateURL(benefit, destinationIds, languageCode, fullName, checkIn, checkOut,
            brandCode);
        assertNotNull(generatedURL);
        assertEquals(StringUtils.concat("http://www.guiomatic.com/guias?idioma=es&destino=982&nomap=Juan%20Perez&checkin=",
            checkinDate, "&checkout=", checkoutDate, "&hash=esteesunhashgeneradoapartirdelosdatosparametro", "&origen=",
            brandCode), generatedURL);
        assertEquals("Descripcion del beneficio de Ciudad en español", benefit.getBenefitInformation().get("ES")
            .getDescription());
        assertEquals("Titulo de Ciudad en español", benefit.getBenefitInformation().get("ES").getTitle());
    }

    @Test
    public void generateURL_multipleDestinations_returnsURL() {
        BenefitDTO benefit = new BenefitDTO();
        Map<String, BenefitInformationDTO> benefitInformationMap = new HashMap<String, BenefitInformationDTO>();
        BenefitInformationDTO descriptionES = new BenefitInformationDTO();
        descriptionES.setDescription("Descripcion del beneficio en {0} en español");
        descriptionES.setTitle("Titulo para {0} en español");
        benefitInformationMap.put("ES", descriptionES);
        benefit.setBenefitInformation(benefitInformationMap);
        Set<Long> destinationIds = new HashSet<Long>();
        destinationIds.add(982L);
        destinationIds.add(4545L);
        String brandCode = "";
        String languageCode = "ES";
        String fullName = "Juan Perez";
        Date checkIn = new Date();
        Date checkOut = new Date();

        CityDTO data = new CityDTO();
        List<KeyPairDTO<String, String>> descriptions = Lists.newArrayList();
        descriptions.add(new KeyPairDTO<String, String>("ES", "Ciudad"));
        descriptions.add(new KeyPairDTO<String, String>("PT", "Cidade"));
        data.setNames(descriptions);
        ResponseContainer<CityDTO> responseContainer = new ResponseContainer<CityDTO>();
        responseContainer.setData(data);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String checkinDate = sdf.format(checkIn);
        String checkoutDate = sdf.format(checkOut);

        when(this.cityClientMock.getCityByCityOID(982L)).thenReturn(responseContainer);
        when(this.cityClientMock.getCityByCityOID(4545L)).thenReturn(responseContainer);
        when(
            this.redirectHashGeneratorMock.getHashRedirection(languageCode.toLowerCase(), destinationIds, fullName,
                checkinDate, checkoutDate)).thenReturn("esteesunhashgeneradoapartirdelosdatosparametro");

        String generatedURL = this.instance.generateURL(benefit, destinationIds, languageCode, fullName, checkIn, checkOut,
            brandCode);
        assertNotNull(generatedURL);
        assertEquals(
            "http://www.guiomatic.com/guias?idioma=es&destino=982,4545&nomap=Juan%20Perez&hash=esteesunhashgeneradoapartirdelosdatosparametro&origen=",
            generatedURL);
        assertEquals("Descripcion del beneficio en  en español", benefit.getBenefitInformation().get("ES").getDescription());
        assertEquals("Titulo para  en español", benefit.getBenefitInformation().get("ES").getTitle());
    }
}
