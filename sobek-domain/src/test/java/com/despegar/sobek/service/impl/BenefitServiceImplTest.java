package com.despegar.sobek.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.caching.CacheTemplate;
import com.despegar.framework.caching.repository.impl.MockedCacheRepositoryManager;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.sobek.dao.BenefitDAO;
import com.despegar.sobek.dto.BenefitApplianceDTO;
import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.dto.BenefitFilterResultDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.dto.BenefitItemResultDTO;
import com.despegar.sobek.dto.ClientDTO;
import com.despegar.sobek.dto.MergePDFsDTO;
import com.despegar.sobek.dto.OrderDTO;
import com.despegar.sobek.dto.OrderDTO.OrderByType;
import com.despegar.sobek.dto.OrderDTO.OrderDirectionType;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.exception.ServiceErrorCode;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.BenefitStatus;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.VoucherI18N;
import com.despegar.sobek.service.GeoAreaService;
import com.despegar.sobek.service.PDFGeneratorService;
import com.despegar.sobek.solr.index.manager.BenefitIndexManager;
import com.despegar.sobek.translator.BenefitBuilder;
import com.despegar.sobek.translator.BenefitFilterResultTranslator;
import com.despegar.sobek.translator.BenefitTranslator;
import com.despegar.sobek.translator.PictureTranslator;
import com.despegar.sobek.translator.SolrBenefitMergeBuilder;
import com.despegar.sobek.utility.ModelContentCreator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class BenefitServiceImplTest {

    private BenefitServiceImpl instance;
    @Mock
    private BenefitTranslator benefitTranslatorMock;
    @Mock
    private BenefitDAO benefitDAOMock;
    @Mock
    private PDFGeneratorService pdfGeneratorServiceMock;
    @Mock
    private BenefitIndexManager benefitIndexManager;
    @Mock
    private BenefitFilterResultTranslator benefitFilterResultTranslator;
    @Mock
    private GeoAreaService geoAreaSerivceMock;
    @Mock
    private BenefitBuilder benefitBuilderMock;
    @Mock
    private PictureTranslator pictureTranslator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.instance = new BenefitServiceImpl();
        this.instance.setSolrBenefitMergeBuilder(new SolrBenefitMergeBuilder());
        this.instance.setBenefitTranslator(this.benefitTranslatorMock);
        this.instance.setBenefitDAO(this.benefitDAOMock);
        this.instance.setPdfGeneratorService(this.pdfGeneratorServiceMock);
        this.instance.setBenefitIndexManager(this.benefitIndexManager);
        this.instance.setBenefitFilterResultTranslator(this.benefitFilterResultTranslator);
        this.instance.setGeoAreaService(this.geoAreaSerivceMock);
        this.instance.setBenefitBuilder(this.benefitBuilderMock);
        this.instance.setPictureTranslator(this.pictureTranslator);
        CacheTemplate cacheTemplate = new CacheTemplate();
        cacheTemplate.setCacheRepositoryManager(new MockedCacheRepositoryManager());
        this.instance.setCacheTemplate(cacheTemplate);

        Mockito.doNothing().when(this.benefitIndexManager).update(Mockito.anyListOf(Long.class));
        Mockito.doNothing().when(this.benefitIndexManager).delete(Mockito.anyListOf(Long.class));
    }

    @Test
    public void save_nullOID_returnsLongOID() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(null);

        Benefit benefit = this.createPublishedBenefit();

        when(this.benefitTranslatorMock.getPersistentObject(benefitDTO)).thenReturn(benefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        verify(this.benefitTranslatorMock, never()).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock).getPersistentObject(benefitDTO);

    }

    @Test
    public void save_updateBenefitState_returnsLongOID() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.UNPUBLISHED.getCode());

        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(status);

        Benefit updatedBenefit = this.createPublishedBenefit();

        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.fillPersistentObject(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        verify(this.benefitTranslatorMock).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);

    }

    @Test
    public void save_updateBenefit_returnsLongOID() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.UNPUBLISHED.getCode());

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.UNPUBLISHED.getCode());

        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(status);

        Benefit updatedBenefit = this.createPublishedBenefit();

        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.fillPersistentObject(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        verify(this.benefitTranslatorMock).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);

    }

    @SuppressWarnings("unchecked")
    public void save_cancelBenefit_returnsLongOID() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.CANCELLED.getCode());

        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(ModelContentCreator.createBenefitStatus(BenefitStatusCode.PUBLISHED, null));

        Benefit updatedBenefit = ModelContentCreator.createBenefit();
        updatedBenefit.setBenefitStatus(ModelContentCreator.createBenefitStatus(BenefitStatusCode.CANCELLED, null));
        updatedBenefit.setOID(1L);

        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.fillBenefitStatus(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);
        verify(this.benefitTranslatorMock, never()).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitTranslatorMock).fillBenefitStatus(any(BenefitDTO.class), any(Benefit.class));
        verify(this.pdfGeneratorServiceMock, never()).generatePDF(any(Map.class));
    }

    private Benefit createPublishedBenefit() {
        BenefitStatus publishedStatus = new BenefitStatus();
        publishedStatus.setCode(BenefitStatusCode.PUBLISHED.getCode());

        Benefit updatedBenefit = new Benefit();
        updatedBenefit.setOID(1L);
        updatedBenefit.setBenefitStatus(publishedStatus);
        return updatedBenefit;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void save_publishBenefit_generateVoucher() {

        String expectedLanguageIsoCode = "ES";
        String expectedResourceName = "myFile.pdf";

        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.UNPUBLISHED.getCode());

        Language language = new Language();
        language.setIsoCode(expectedLanguageIsoCode);

        BenefitDescriptionI18N description = new BenefitDescriptionI18N();
        description.setLanguage(language);

        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(status);

        Benefit updatedBenefit = this.createPublishedBenefit();
        updatedBenefit.setBenefitDescriptionI18N(Sets.newHashSet(description));
        updatedBenefit.setCompany(ModelContentCreator.createCompany());

        when(this.pdfGeneratorServiceMock.generatePDF(any(Map.class))).thenReturn(expectedResourceName);
        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.fillPersistentObject(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);
        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        assertEquals(1, updatedBenefit.getExternalResource().size());
        VoucherI18N voucherI18N = updatedBenefit.getExternalResource().iterator().next();
        assertEquals(expectedResourceName, voucherI18N.getResourceName());
        assertTrue(voucherI18N.getIsGenerated());
        assertEquals(expectedLanguageIsoCode, voucherI18N.getLanguage().getIsoCode());

        verify(this.benefitTranslatorMock).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void save_publishBenefit_doNotGenerateVoucher() {

        String expectedLanguageIsoCode = "ES";
        String expectedResourceName = "myFile.pdf";

        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.UNPUBLISHED.getCode());

        Language language = new Language();
        language.setIsoCode(expectedLanguageIsoCode);

        BenefitDescriptionI18N description = new BenefitDescriptionI18N();
        description.setLanguage(language);

        VoucherI18N voucher = new VoucherI18N();
        voucher.setIsGenerated(false);
        voucher.setLanguage(language);

        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(status);

        Benefit updatedBenefit = this.createPublishedBenefit();
        updatedBenefit.setBenefitDescriptionI18N(Sets.newHashSet(description));
        updatedBenefit.setExternalResource(Sets.newHashSet(voucher));

        when(this.benefitTranslatorMock.fillPersistentObject(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);
        when(this.pdfGeneratorServiceMock.generatePDF(any(Map.class))).thenReturn(expectedResourceName);
        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        assertEquals(1, updatedBenefit.getExternalResource().size());
        VoucherI18N voucherI18N = updatedBenefit.getExternalResource().iterator().next();
        assertFalse(voucherI18N.getIsGenerated());
        assertEquals(expectedLanguageIsoCode, voucherI18N.getLanguage().getIsoCode());

        verify(this.benefitTranslatorMock).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);

    }

    @Test
    public void save_nullBenefitInformationLink_returnsLongOID() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);

        BenefitInformationDTO benefitInformationDTO = new BenefitInformationDTO();
        benefitInformationDTO.setLink(null);
        benefitInformationDTO.setTitle("title");

        HashMap<String, BenefitInformationDTO> benefitInformations = new HashMap<String, BenefitInformationDTO>();
        benefitInformations.put("ES", benefitInformationDTO);

        benefitDTO.setBenefitInformation(benefitInformations);

        BenefitStatus status = new BenefitStatus();
        status.setCode(BenefitStatusCode.UNPUBLISHED.getCode());
        // BENEFIT
        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        benefit.setBenefitStatus(status);

        Benefit updatedBenefit = this.createPublishedBenefit();

        when(this.benefitTranslatorMock.fillPersistentObject(any(BenefitDTO.class), any(Benefit.class))).thenReturn(
            updatedBenefit);
        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);

        Long benefitOID = this.instance.save(benefitDTO);

        assertNotNull(benefitOID);
        assertEquals(Long.valueOf(1L), benefitOID);

        verify(this.benefitTranslatorMock).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
        verify(this.benefitDAOMock).saveWithFlush(any(Benefit.class));
        verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_updatePublishedBenefit_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        Benefit benefit = this.createPublishedBenefit();
        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);

        this.savenAndExpectServiceException(benefitDTO);

    }

    @Test(expected = ServiceException.class)
    public void save_updateCancelledBenefit_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());

        Benefit benefit = ModelContentCreator.createBenefit();
        benefit.setBenefitStatus(ModelContentCreator.createBenefitStatus(BenefitStatusCode.CANCELLED, null));

        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(benefit);

        this.savenAndExpectServiceException(benefitDTO);

    }

    @Test(expected = ServiceException.class)
    public void save_nullDateFrom_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setDateFrom(null);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_nullDateTo_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setDateTo(null);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_nullCategoryCode_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setCategoryCode(null);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_nullCompanyOID_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setCompanyOID(null);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_nullBenefitStatusCode_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitStatusCode(null);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_emptyBenefitInformations_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);
        benefitDTO.setBenefitInformation(new HashMap<String, BenefitInformationDTO>());

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test(expected = ServiceException.class)
    public void save_nullBenefitInformationTitle_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);

        BenefitInformationDTO benefitInformationDTO = new BenefitInformationDTO();
        benefitInformationDTO.setTitle(null);

        HashMap<String, BenefitInformationDTO> benefitInformations = new HashMap<String, BenefitInformationDTO>();
        benefitInformations.put("ES", benefitInformationDTO);

        benefitDTO.setBenefitInformation(benefitInformations);

        this.savenAndExpectServiceException(benefitDTO);
    }

    private void savenAndExpectServiceException(BenefitDTO benefitDTO) {
        try {
            this.instance.save(benefitDTO);
        } catch (ServiceException se) {
            verify(this.benefitDAOMock, never()).save(any(Benefit.class));
            verify(this.benefitTranslatorMock, never()).getPersistentObject(benefitDTO);
            verify(this.benefitTranslatorMock, never()).fillPersistentObject(any(BenefitDTO.class), any(Benefit.class));
            verify(this.benefitTranslatorMock, never()).fillBenefitStatus(any(BenefitDTO.class), any(Benefit.class));
            throw se;
        }
    }

    @Test(expected = ServiceException.class)
    public void save_nullBenefitInformationLinkTitle_throwsServiceException() {
        BenefitDTO benefitDTO = this.createBenefitDTO();
        benefitDTO.setOID(1L);

        BenefitInformationDTO benefitInformationDTO = new BenefitInformationDTO();
        benefitInformationDTO.setTitle("title");
        benefitInformationDTO.setLink("www.test.com");

        HashMap<String, BenefitInformationDTO> benefitInformations = new HashMap<String, BenefitInformationDTO>();
        benefitInformations.put("ES", benefitInformationDTO);

        benefitDTO.setBenefitInformation(benefitInformations);

        this.savenAndExpectServiceException(benefitDTO);
    }

    @Test
    public void delete_correct() {

        doNothing().when(this.benefitDAOMock).delete(any(Long.class));
        Benefit benefit = new Benefit();
        benefit.setOID(1L);
        when(this.benefitDAOMock.getBenefit(any(Long.class))).thenReturn(benefit);

        this.instance.delete(12L);

        verify(this.benefitDAOMock).deleteWithFlush(any(Benefit.class));
    }

    @Test
    public void getBenefit_correct_returnsBenefit() {

        Benefit benefit = new Benefit();
        BenefitDTO dto = new BenefitDTO();

        when(this.benefitDAOMock.getBenefit(any(Long.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.getDTO(any(Benefit.class), any())).thenReturn(dto);

        long OID = 12L;
        this.instance.getBenefit(OID);

        verify(this.benefitDAOMock).getBenefit(OID);
        verify(this.benefitTranslatorMock).getDTO(any(Benefit.class), any());

    }

    @Test(expected = ServiceException.class)
    public void getBenefit_nullOID_throwServiceException() {

        try {
            this.instance.getBenefit(null);
        } catch (ServiceException se) {
            assertEquals(1, se.getErrors().size());
            assertEquals(ServiceErrorCode.BENEFIT_OID_NULL.getCode(), se.getErrors().get(0).getCode());
            throw se;
        }
    }

    @Test(expected = SobekServiceException.class)
    public void getBenefit_nullBenefit_returnsBenefit() {

        Benefit benefit = new Benefit();

        when(this.benefitDAOMock.read(any(Serializable.class))).thenReturn(null);

        long OID = 12L;

        try {
            this.instance.getBenefit(OID);
        } catch (SobekServiceException sse) {
            verify(this.benefitDAOMock).getBenefit(OID);
            verify(this.benefitTranslatorMock, never()).getDTO(benefit);
            throw sse;
        }

    }

    @Test
    @SuppressWarnings("unchecked")
    public void getBenefit_nullApplianceGeoArea_returnsBenefit() {

        Benefit benefit = ModelContentCreator.createBenefit();
        BenefitDTO dto = new BenefitDTO();

        when(this.benefitDAOMock.getBenefit(any(Long.class))).thenReturn(benefit);
        when(this.benefitTranslatorMock.getDTO(any(Benefit.class), any())).thenReturn(dto);

        ListMultimap<Long, GeoAreaDescriptionDTO> create = ArrayListMultimap.create();
        when(this.geoAreaSerivceMock.getGeoAreasDescriptions(anyList())).thenReturn(create);
        long OID = 12L;
        this.instance.getBenefit(OID);

        verify(this.benefitDAOMock).getBenefit(OID);
        verify(this.benefitTranslatorMock).getDTO(any(Benefit.class), any());

    }

    @Test
    public void searchBenefits_completeBenefitFilterContainberDTO_returnsListBenefitDTO() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        BenefitFilterResultDTO benefitFilterResultDTO = this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator).translate(solrBenefitResults);

        assertEquals(1, benefitFilterResultDTO.getNumberOfResults().intValue());
        assertEquals(1, benefitFilterResultDTO.getBenefits().size());
        assertEquals(Boolean.TRUE, benefitFilterResultDTO.getBenefits().get(0).getIsOutstanding());
        assertEquals("companyName", benefitFilterResultDTO.getBenefits().get(0).getCompanyName());
        assertEquals(now.getTime(), benefitFilterResultDTO.getBenefits().get(0).getPublicationDate());
        assertEquals("PUB", benefitFilterResultDTO.getBenefits().get(0).getBenefitStatusCode());
        assertEquals(now.getTime(), benefitFilterResultDTO.getBenefits().get(0).getDateFrom());
        assertEquals(now.getTime(), benefitFilterResultDTO.getBenefits().get(0).getDateTo());
        assertEquals(2, benefitFilterResultDTO.getBenefits().get(0).getTitles().size());
        assertEquals("titleES", benefitFilterResultDTO.getBenefits().get(0).getTitles().get("ES"));
        assertEquals("titlePT", benefitFilterResultDTO.getBenefits().get(0).getTitles().get("PT"));
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noPageNumber_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.setPageNumber(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noPageSize_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.setPageSize(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noBrand_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.getBenefitFilterDTOs().iterator().next().setBrandCode("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noCategoryCode_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.getBenefitFilterDTOs().iterator().next().setCategoryCode("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noDestinationType_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.getBenefitFilterDTOs().iterator().next().setDestinationType("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test(expected = ServiceException.class)
    public void searchBenefits_noOriginType_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterDTO = this.createBenefitFilterContainerDTO();
        benefitFilterDTO.getBenefitFilterDTOs().iterator().next().setOriginType("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitFilterResultDTO value = this.createBenefitFilterResultDTO(now);

        when(this.benefitIndexManager.search(benefitFilterDTO)).thenReturn(solrBenefitResults);
        when(this.benefitFilterResultTranslator.translate(solrBenefitResults)).thenReturn(value);

        this.instance.searchBenefits(benefitFilterDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterDTO);
        verify(this.benefitFilterResultTranslator, never()).translate(solrBenefitResults);
    }

    @Test
    public void getBenefitsByCustomSearch_completeBenefitFilterContainberDTO_returnsListBenefitDTO() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        BenefitContainerDTO benefitsByCustomSearch = this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock).build(solrBenefitResults, benefitFilterContainerDTO);

        assertEquals(1, benefitsByCustomSearch.getNumberOfResults().intValue());
        assertEquals(1, benefitsByCustomSearch.getBenefitDTOs().size());
        assertEquals("PUB", benefitsByCustomSearch.getBenefitDTOs().get(0).getBenefitStatusCode());
        assertEquals("GST", benefitsByCustomSearch.getBenefitDTOs().get(0).getCategoryCode());
        assertEquals(1L, benefitsByCustomSearch.getBenefitDTOs().get(0).getCompanyOID().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateFrom().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateTo().longValue());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsFree());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsOutstanding());
        assertEquals("HASHCODE_RENDERING", benefitsByCustomSearch.getBenefitDTOs().get(0).getLinkTemplateType());
        assertEquals("picture.jpg", benefitsByCustomSearch.getBenefitDTOs().get(0).getPicture().getFileName());
        assertEquals(new BigDecimal(1), benefitsByCustomSearch.getBenefitDTOs().get(0).getRelevance());
    }

    @Test
    public void getBenefitByCustomSearch_noBenefitFilters_returnsAllBenefitDTO() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.setBenefitFilterDTOs(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        BenefitContainerDTO benefitsByCustomSearch = this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock).build(solrBenefitResults, benefitFilterContainerDTO);

        assertEquals(1, benefitsByCustomSearch.getNumberOfResults().intValue());
        assertEquals(1, benefitsByCustomSearch.getBenefitDTOs().size());
        assertEquals("PUB", benefitsByCustomSearch.getBenefitDTOs().get(0).getBenefitStatusCode());
        assertEquals("GST", benefitsByCustomSearch.getBenefitDTOs().get(0).getCategoryCode());
        assertEquals(1L, benefitsByCustomSearch.getBenefitDTOs().get(0).getCompanyOID().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateFrom().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateTo().longValue());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsFree());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsOutstanding());
        assertEquals("HASHCODE_RENDERING", benefitsByCustomSearch.getBenefitDTOs().get(0).getLinkTemplateType());
        assertEquals("picture.jpg", benefitsByCustomSearch.getBenefitDTOs().get(0).getPicture().getFileName());
        assertEquals(new BigDecimal(1), benefitsByCustomSearch.getBenefitDTOs().get(0).getRelevance());
    }

    @Test
    public void getBenefitByCustomSearch_emptyBenefitStatusList_returnsAllBenefitDTO() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().setBenefitStatusCodes(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        BenefitContainerDTO benefitsByCustomSearch = this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock).build(solrBenefitResults, benefitFilterContainerDTO);

        assertEquals(1, benefitsByCustomSearch.getNumberOfResults().intValue());
        assertEquals(1, benefitsByCustomSearch.getBenefitDTOs().size());
        assertEquals("PUB", benefitsByCustomSearch.getBenefitDTOs().get(0).getBenefitStatusCode());
        assertEquals("GST", benefitsByCustomSearch.getBenefitDTOs().get(0).getCategoryCode());
        assertEquals(1L, benefitsByCustomSearch.getBenefitDTOs().get(0).getCompanyOID().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateFrom().longValue());
        assertEquals(now.getTime(), benefitsByCustomSearch.getBenefitDTOs().get(0).getDateTo().longValue());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsFree());
        assertEquals(Boolean.TRUE, benefitsByCustomSearch.getBenefitDTOs().get(0).getIsOutstanding());
        assertEquals("HASHCODE_RENDERING", benefitsByCustomSearch.getBenefitDTOs().get(0).getLinkTemplateType());
        assertEquals("picture.jpg", benefitsByCustomSearch.getBenefitDTOs().get(0).getPicture().getFileName());
        assertEquals(new BigDecimal(1), benefitsByCustomSearch.getBenefitDTOs().get(0).getRelevance());
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noBrand_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().setBrandCode("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noCategoryCode_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().setCategoryCode("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noDestinationType_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().setDestinationType("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noOriginType_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.getBenefitFilterDTOs().iterator().next().setOriginType("");
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noPageSize_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.setPageSize(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = ServiceException.class)
    public void getBenefitByCustomSearch_noPageNumber_throwClientResponseException() {
        Date now = new Date();
        BenefitFilterContainerDTO benefitFilterContainerDTO = this.createBenefitFilterContainerDTO();
        benefitFilterContainerDTO.setPageNumber(null);
        QueryResponse solrBenefitResults = this.createQueryResponse(now);
        BenefitContainerDTO benefitContainerDTO = this.createBenefitContainer(now);

        when(this.benefitIndexManager.search(benefitFilterContainerDTO)).thenReturn(solrBenefitResults);
        when(this.benefitBuilderMock.build(solrBenefitResults, benefitFilterContainerDTO)).thenReturn(benefitContainerDTO);

        this.instance.getBenefitsByCustomSearch(benefitFilterContainerDTO);

        verify(this.benefitIndexManager, never()).search(benefitFilterContainerDTO);
        verify(this.benefitBuilderMock, never()).build(solrBenefitResults, benefitFilterContainerDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergePDFs_mergePDFsDTONull_throwIllegalArgumentException() {
        MergePDFsDTO mergePDFsDTO = null;
        this.instance.mergePDFs(mergePDFsDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergePDFs_languageNull_throwIllegalArgumentException() {
        MergePDFsDTO mergePDFsDTO = new MergePDFsDTO();
        mergePDFsDTO.setLanguage(null);
        this.instance.mergePDFs(mergePDFsDTO);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergePDFs_paramOk_returnOk() throws DocumentException, IOException {
        String language = "ES";
        String fileName = StringUtils.concat(System.getProperty("java.io.tmpdir"), "somePdf.pdf");
        File somePdfFile = this.createSomePdf(fileName);
        FileInputStream mergedFileInputStream = new FileInputStream(somePdfFile);
        mergedFileInputStream.close();
        URL resource = somePdfFile.toURI().toURL();
        Long benefitOid = 1L;

        MergePDFsDTO mergePDFsDTO = new MergePDFsDTO();
        mergePDFsDTO.setLanguage(language);
        mergePDFsDTO.setBenefitOidList(Lists.newArrayList(benefitOid));

        QueryResponse queryResponse = this.createQueryResponse(benefitOid, resource.toString());

        when(this.benefitIndexManager.getBenefitsByOIDs(Lists.newArrayList(benefitOid))).thenReturn(queryResponse);
        when(this.pdfGeneratorServiceMock.mergePDFs(Mockito.anyList())).thenReturn(somePdfFile.getPath());

        byte[] mergedPDFsFileByetArray = this.instance.mergePDFs(mergePDFsDTO);

        Mockito.verify(this.pdfGeneratorServiceMock).mergePDFs(Mockito.anyList());
        Mockito.verify(this.benefitIndexManager).getBenefitsByOIDs(Lists.newArrayList(benefitOid));

        assertTrue(mergedPDFsFileByetArray.length > 800);
    }

    private File createSomePdf(String fileName) throws DocumentException, IOException {
        File fileA = new File(fileName);
        FileOutputStream fileOutputStreamA = new FileOutputStream(fileA);
        Document documentA = new Document();
        PdfWriter.getInstance(documentA, fileOutputStreamA);
        documentA.open();
        documentA.add(new Paragraph("Some string"));
        documentA.close();
        fileOutputStreamA.close();
        return fileA;
    }

    public BenefitDTO createBenefitDTO() {
        BenefitDTO dto = new BenefitDTO();
        dto.setAppliances(new HashSet<BenefitApplianceDTO>());
        dto.setBenefitInformation(new HashMap<String, BenefitInformationDTO>());
        dto.setBenefitStatusCode(BenefitStatusCode.PUBLISHED.getCode());
        dto.setCategoryCode("CAT");
        dto.setCompanyOID(123L);
        dto.setDateFrom(System.currentTimeMillis());
        dto.setDateTo(System.currentTimeMillis());
        dto.setIsFree(true);
        dto.setIsOutstanding(true);
        dto.setExternalResources(new HashMap<String, String>());

        BenefitInformationDTO informationDTO = new BenefitInformationDTO();
        informationDTO.setBranches("branch");
        informationDTO.setDescription("description");
        informationDTO.setLink("link");
        informationDTO.setLinkTitle("linkTitle");
        informationDTO.setTermsAndConditions("termsAndConditions");
        informationDTO.setTitle("title");

        Map<String, BenefitInformationDTO> benefitInformationDTO = Maps.newHashMap();
        benefitInformationDTO.put("ES", informationDTO);

        dto.getBenefitInformation().putAll(benefitInformationDTO);

        return dto;
    }

    private BenefitFilterContainerDTO createBenefitFilterContainerDTO() {
        BenefitFilterContainerDTO benefitFilterContainerDTO = new BenefitFilterContainerDTO();
        BenefitFilterDTO benefitFilterDTO = new BenefitFilterDTO();
        List<String> appliesTo = new LinkedList<String>();
        appliesTo.add("H");
        appliesTo.add("F");
        appliesTo.add("P");
        benefitFilterDTO.setAppliesTo(appliesTo);
        List<String> benefitStatusCodes = new LinkedList<String>();
        benefitStatusCodes.add("PUB");
        benefitStatusCodes.add("CAN");
        benefitStatusCodes.add("UNP");
        benefitFilterDTO.setBenefitStatusCodes(benefitStatusCodes);
        benefitFilterDTO.setBrandCode("DESAR");
        benefitFilterDTO.setCategoryCode("GST");
        benefitFilterDTO.setCompanyOID(1L);
        benefitFilterDTO.setDateFrom(1320186031L);
        benefitFilterDTO.setDateTo(1318544431L);
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationOID(982L);
        benefitFilterDTO.setDestinationType("C");
        benefitFilterDTO.setOriginOID(5693L);
        benefitFilterDTO.setOriginOID(4544L);
        benefitFilterDTO.setOriginType("C");

        Set<BenefitFilterDTO> benefitFilterDTOs = new HashSet<BenefitFilterDTO>();
        benefitFilterDTOs.add(benefitFilterDTO);
        benefitFilterContainerDTO.setBenefitFilterDTOs(benefitFilterDTOs);

        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);

        OrderDTO order = new OrderDTO();
        order.setOrderBy(OrderByType.DATE_FROM);
        order.setOrderDirection(OrderDirectionType.ASC);
        benefitFilterContainerDTO.getOrders().add(order);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Juan");
        clientDTO.setLastName("Perez");
        clientDTO.setEmail("juan.perez@gmail.com");
        benefitFilterContainerDTO.setClientDTO(clientDTO);
        benefitFilterContainerDTO.setPageNumber(1);
        benefitFilterContainerDTO.setPageSize(10);

        return benefitFilterContainerDTO;
    }

    private QueryResponse createQueryResponse(Date now) {
        QueryResponse queryResponse = new QueryResponse();
        GroupCommand command = new GroupCommand("name", 0);
        SolrDocumentList result = new SolrDocumentList();
        SolrDocument element = new SolrDocument();
        element.setField("state", "state");
        element.setField("titleES", "titleES");
        element.setField("titlePT", "titlePT");
        element.setField("branchesES", "branchesES");
        element.setField("branchesPT", "branchesPT");
        element.setField("linkES", "linkES");
        element.setField("linkPT", "linkPT");
        element.setField("linkTitleES", "linkTitleES");
        element.setField("linkTitlePT", "linkTitlePT");
        element.setField("termsAndConditionsES", "termsAndConditionsES");
        element.setField("termsAndConditionsPT", "termsAndConditionsPT");
        element.setField("descriptionES", "descriptionES");
        element.setField("descriptionPT", "descriptionPT");
        element.setField("companyName", "companyName");
        element.setField("company", 1L);
        element.setField("dateFrom", now);
        element.setField("dateTo", now);
        element.setField("publicationDate", now);
        element.setField("category", "SHP");
        element.setField("isOutstanding", true);
        element.setField("isFree", true);
        element.setField("companyPicture", "foto");
        element.setField("oidBenefit", "1");
        element.setField("name", "value");
        element.setField("relevance", 1);
        result.add(element);
        Group group = new Group("groupValue", result);
        command.add(group);
        SimpleOrderedMap<Object> g = new SimpleOrderedMap<Object>();
        g.add("matches", 0);
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(command);
        g.add("doclist", result);
        NamedList<Object> res = new NamedList<Object>();
        NamedList<Object> a = new NamedList<Object>();
        a.add("groups", g);
        res.add("grouped", a);
        queryResponse.setResponse(res);
        return queryResponse;
    }

    private BenefitFilterResultDTO createBenefitFilterResultDTO(Date date) {
        BenefitFilterResultDTO benefitFilterResultDTO = new BenefitFilterResultDTO();
        benefitFilterResultDTO.setNumberOfResults(1);
        List<BenefitItemResultDTO> benefits = Lists.newArrayList();
        BenefitItemResultDTO benefitItemResultDTO = new BenefitItemResultDTO();
        benefitItemResultDTO.setBenefitStatusCode("PUB");
        benefitItemResultDTO.setCompanyName("companyName");
        benefitItemResultDTO.setIsOutstanding(Boolean.TRUE);
        Map<String, String> titles = new HashMap<String, String>();
        titles.put("ES", "titleES");
        titles.put("PT", "titlePT");
        benefitItemResultDTO.setTitles(titles);
        benefitItemResultDTO.setDateFrom(date.getTime());
        benefitItemResultDTO.setDateTo(date.getTime());
        benefitItemResultDTO.setPublicationDate(date.getTime());
        benefits.add(benefitItemResultDTO);
        benefitFilterResultDTO.setBenefits(benefits);
        return benefitFilterResultDTO;
    }

    private BenefitContainerDTO createBenefitContainer(Date date) {
        BenefitContainerDTO benefitContainerDTO = new BenefitContainerDTO();
        benefitContainerDTO.setNumberOfResults(1);
        List<BenefitDTO> benefitDTOs = Lists.newArrayList();
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setBenefitStatusCode("PUB");
        benefitDTO.setCategoryCode("GST");
        benefitDTO.setCompanyOID(1L);
        benefitDTO.setDateFrom(date.getTime());
        benefitDTO.setDateTo(date.getTime());
        benefitDTO.setIsOutstanding(Boolean.TRUE);
        benefitDTO.setIsFree(Boolean.TRUE);
        benefitDTO.setLinkTemplateType("HASHCODE_RENDERING");
        benefitDTO.setRelevance(new BigDecimal(1));
        PictureDTO picture = new PictureDTO();
        picture.setFileName("picture.jpg");
        benefitDTO.setPicture(picture);
        benefitDTOs.add(benefitDTO);
        benefitContainerDTO.setBenefitDTOs(benefitDTOs);
        return benefitContainerDTO;
    }

    private QueryResponse createQueryResponse(Long oid, String link) {
        QueryResponse queryResponse = new QueryResponse();
        GroupCommand command = new GroupCommand("name", 0);
        SolrDocumentList result = new SolrDocumentList();
        SolrDocument element = new SolrDocument();
        element.setField("oidBenefit", oid);
        element.setField("linkVoucherES", link);
        element.setField("linkVoucherPT", link);

        result.add(element);
        Group group = new Group("groupValue", result);
        command.add(group);
        SimpleOrderedMap<Object> g = new SimpleOrderedMap<Object>();
        g.add("matches", 0);
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(command);
        g.add("doclist", result);
        NamedList<Object> res = new NamedList<Object>();
        NamedList<Object> a = new NamedList<Object>();
        a.add("groups", g);
        res.add("grouped", a);
        queryResponse.setResponse(res);
        return queryResponse;
    }
}
