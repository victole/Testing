package com.despegar.sobek.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.despegar.framework.caching.CacheTemplate;
import com.despegar.framework.caching.SingleCacheableExecutionBlock;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.geodespegar.dto.GeoAreaDescriptionDTO;
import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.sobek.dao.BenefitDAO;
import com.despegar.sobek.dto.BenefitContainerDTO;
import com.despegar.sobek.dto.BenefitDTO;
import com.despegar.sobek.dto.BenefitFilterContainerDTO;
import com.despegar.sobek.dto.BenefitFilterDTO;
import com.despegar.sobek.dto.BenefitFilterResultDTO;
import com.despegar.sobek.dto.BenefitInformationDTO;
import com.despegar.sobek.dto.BenefitMergeFilterDTO;
import com.despegar.sobek.dto.MergePDFsDTO;
import com.despegar.sobek.exception.ServiceErrorCode;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.model.Appliance;
import com.despegar.sobek.model.Benefit;
import com.despegar.sobek.model.BenefitDescriptionI18N;
import com.despegar.sobek.model.BenefitLinkRenderType;
import com.despegar.sobek.model.BenefitStatusCode;
import com.despegar.sobek.model.GeoArea;
import com.despegar.sobek.model.Language;
import com.despegar.sobek.model.VoucherI18N;
import com.despegar.sobek.service.BenefitService;
import com.despegar.sobek.service.GeoAreaService;
import com.despegar.sobek.service.PDFGeneratorService;
import com.despegar.sobek.solr.index.manager.BenefitIndexManager;
import com.despegar.sobek.translator.BenefitBuilder;
import com.despegar.sobek.translator.BenefitFilterResultTranslator;
import com.despegar.sobek.translator.BenefitTranslator;
import com.despegar.sobek.translator.PictureTranslator;
import com.despegar.sobek.translator.SolrBenefitMergeBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class BenefitServiceImpl
    implements BenefitService {
    // Agrego un nuevo comentario


    private static Logger logger = Logger.getLogger(BenefitServiceImpl.class);

    private BenefitTranslator benefitTranslator;
    private BenefitDAO benefitDAO;
    private GeoAreaService geoAreaService;
    private PDFGeneratorService pdfGeneratorService;
    private BenefitIndexManager benefitIndexManager;
    private BenefitFilterResultTranslator benefitFilterResultTranslator;
    private BenefitBuilder benefitBuilder;
    private CacheTemplate cacheTemplate;
    private PictureTranslator pictureTranslator;
    private SolrBenefitMergeBuilder solrBenefitMergeBuilder;

    @Override
    @Transactional(readOnly = true)
    public BenefitDTO getBenefit(Long OID) {

        logger.info(StringUtils.concat("Retrieving Benefit with OID: ", OID));

        if (OID == null) {
            ServiceError error = new ServiceError(ServiceErrorCode.BENEFIT_OID_NULL.getCode(), "Benefit OID can not be null");
            throw new ServiceException(Lists.newArrayList(error));
        }

        Benefit benefit = this.benefitDAO.getBenefit(OID);

        if (benefit == null) {
            throw new SobekServiceException(StringUtils.concat("No Benefit with OID: ", OID, " exists"));
        }

        return this.benefitTranslator.getDTO(benefit, this.getGeoAreasDescription(benefit));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long save(BenefitDTO benefitDTO) {

        this.validateBenefit(benefitDTO);

        Benefit benefit = null;
        Long oid = benefitDTO.getOID();

        if (oid == null) {
            logger.info("Creating brand new Benefit");
            benefit = this.benefitTranslator.getPersistentObject(benefitDTO);
            benefit.setRelevance(BigDecimal.valueOf(100));
            benefit.setLinkTemplateType(BenefitLinkRenderType.NONE_RENDENRING);
        } else {
            logger.info("Updating Benefit with OID:" + oid);
            benefit = this.benefitDAO.read(oid);

            boolean isRecentlyCancelled = BenefitStatusCode.CANCELLED.getCode().equals(benefitDTO.getBenefitStatusCode());

            this.validateBenefitStatus(benefit, oid, isRecentlyCancelled);

            if (isRecentlyCancelled) {
                logger.info("Cancelling Benefit");
                benefit = this.benefitTranslator.fillBenefitStatus(benefitDTO, benefit);
            } else {
                benefit = this.benefitTranslator.fillPersistentObject(benefitDTO, benefit);
            }

        }

        if (this.isPublished(benefit)) {
            this.publishBenefit(benefit);
        }

        this.saveBenefit(benefit);

        return benefit.getOID();
    }

    private void saveBenefit(Benefit benefit) {
        this.benefitDAO.saveWithFlush(benefit);
        List<Long> benefitOIDs = new LinkedList<Long>();
        benefitOIDs.add(benefit.getOID());
        this.benefitIndexManager.update(benefitOIDs);
    }

    private void publishBenefit(Benefit benefit) {
        logger.info("Publishing benefit with OID:" + benefit.getOID());
        Date publicationDate = Calendar.getInstance().getTime();
        benefit.setPublicationDate(publicationDate);
        this.generatePDFs(benefit);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long OID) {
        logger.info(StringUtils.concat("Deleting Benefit with OID: ", OID));
        this.benefitDAO.deleteWithFlush(this.benefitDAO.read(OID));

        Benefit benefit = this.benefitDAO.getInactiveBenefit(OID);

        if (benefit != null) {
            List<Long> applianceOIDs = new LinkedList<Long>();
            for (Appliance appliance : benefit.getAppliance()) {
                applianceOIDs.add(appliance.getOID());
            }

            this.benefitIndexManager.delete(applianceOIDs);
        } else {
            logger.info(StringUtils.concat("No se pudo obtener el benefit para realizar el borrado en solr, el OID es: ",
                OID));
        }
    }

    @Override
    public BenefitFilterResultDTO searchBenefits(BenefitFilterContainerDTO benefitFilterContainerDTO) {
        logger.info(StringUtils.concat("Searching benefits with: ", benefitFilterContainerDTO));
        this.validateSearchBenefits(benefitFilterContainerDTO);
        QueryResponse solrBenefitResults = this.benefitIndexManager.search(benefitFilterContainerDTO);
        BenefitFilterResultDTO benefitFilterResultDTO = this.benefitFilterResultTranslator.translate(solrBenefitResults);
        logger.info(StringUtils.concat("Returning ", benefitFilterResultDTO.getBenefits().size(), " benefits"));
        return benefitFilterResultDTO;
    }

    @Override
    public BenefitContainerDTO getBenefitsByCustomSearch(final BenefitFilterContainerDTO benefitFilterContainerDTO) {
        logger.info(StringUtils.concat("Getting benefits by custom search with ", benefitFilterContainerDTO.toString()));

        BenefitContainerDTO benefitContainerDTO = this.cacheTemplate.execute(benefitFilterContainerDTO.toString(),
            "com.despegar.sobek.service.impl.BenefitServiceImpl.getBenefitsByCustomSearch()",
            new SingleCacheableExecutionBlock<String, BenefitContainerDTO>() {
                @Override
                public BenefitContainerDTO execute(String key) {
                    BenefitServiceImpl.this.completeStatusListIfEmpty(benefitFilterContainerDTO);
                    BenefitServiceImpl.this.validateSearchBenefits(benefitFilterContainerDTO);
                    QueryResponse search = BenefitServiceImpl.this.benefitIndexManager.search(benefitFilterContainerDTO);
                    BenefitContainerDTO benefitContainerDTO = BenefitServiceImpl.this.benefitBuilder.build(search,
                        benefitFilterContainerDTO);
                    return benefitContainerDTO;
                }
            });
        logger.info(StringUtils.concat("Returning ", benefitContainerDTO.getBenefitDTOs().size(), " benefits"));

        return benefitContainerDTO;
    }


    private void completeStatusListIfEmpty(BenefitFilterContainerDTO benefitFilterContainerDTO) {
        for (BenefitFilterDTO benefitFilterDTO : benefitFilterContainerDTO.getBenefitFilterDTOs()) {
            if (benefitFilterDTO.getBenefitStatusCodes().isEmpty()) {
                benefitFilterDTO.getBenefitStatusCodes().add("PUB");
            }
        }
    }

    private void generatePDFs(Benefit benefit) {
        Language language = null;
        Map<String, Object> model = null;
        String pdfPath = null;
        Set<VoucherI18N> voucherI18Ns = benefit.getExternalResource();
        logger.info("Generating PDFs for Benefit");
        for (BenefitDescriptionI18N description : benefit.getBenefitDescriptionI18N()) {
            language = description.getLanguage();
            final String isoCode = language.getIsoCode();

            boolean voucherExists = false;
            for (VoucherI18N voucher : voucherI18Ns) {
                voucherExists |= voucher.getLanguage().getIsoCode().equals(isoCode);
            }

            logger.info(StringUtils.concat("Voucher for Benefit OID: ", benefit.getOID(), " exist: ", voucherExists));

            if (!voucherExists) {
                logger.info(StringUtils.concat("Generating PDFs for benefit with OID: ", benefit.getOID(),
                    " for language isoCode: ", description.getLanguage().getIsoCode()));
                model = new HashMap<String, Object>();
                model.put("benefit", benefit);
                model.put("descriptionI18N", description);
                model.put("pictureDTO",
                    this.pictureTranslator.fillPictureDTO(benefit.getCompany().getPicture().getResourceName()));
                pdfPath = this.pdfGeneratorService.generatePDF(model);

                VoucherI18N voucherI18N = new VoucherI18N();
                voucherI18N.setLanguage(language);
                voucherI18N.setResourceName(pdfPath);
                voucherI18N.setIsGenerated(true);
                voucherI18Ns.add(voucherI18N);
            }

        }
    }

    private ListMultimap<Long, GeoAreaDescriptionDTO> getGeoAreasDescription(Benefit benefit) {
        List<Long> geoAreaOIDs = Lists.newArrayList();

        GeoArea destinationGeoArea = null;
        GeoArea originGeoArea = null;
        for (Appliance appliance : benefit.getAppliance()) {
            destinationGeoArea = appliance.getDestinationGeoArea();
            if (destinationGeoArea != null) {
                geoAreaOIDs.add(destinationGeoArea.getDespegarItemOID());
            }
            originGeoArea = appliance.getOriginGeoArea();
            if (originGeoArea != null) {
                geoAreaOIDs.add(originGeoArea.getDespegarItemOID());
            }
        }

        ListMultimap<Long, GeoAreaDescriptionDTO> geoAreasDescriptions = ArrayListMultimap.create();
        if (!geoAreaOIDs.isEmpty()) {
            geoAreasDescriptions.putAll(this.geoAreaService.getGeoAreasDescriptions(geoAreaOIDs));
        }
        return geoAreasDescriptions;
    }

    private void validateBenefit(BenefitDTO benefitDTO) {
        List<ServiceError> errors = new ArrayList<ServiceError>();
        Map<String, BenefitInformationDTO> benefitInformation = benefitDTO.getBenefitInformation();

        if (benefitDTO.getDateFrom() == null) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_DATE_FROM_EMPTY.getCode(), "DateFrom can not be empty"));
        }

        if (benefitDTO.getDateTo() == null) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_DATE_TO_EMPTY.getCode(), "DateTo can not be empty"));
        }

        if (benefitDTO.getCategoryCode() == null) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_CATEGORY_EMPTY.getCode(), "Category can not be empty"));
        }

        if (benefitDTO.getCompanyOID() == null) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_COMPANY_EMPTY.getCode(), "Company can not be empty"));
        }

        if (benefitDTO.getBenefitStatusCode() == null) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_STATUS_EMPTY.getCode(), "Benefit Status can not be empty"));
        }
        if (benefitInformation.isEmpty()) {
            errors.add(new ServiceError(ServiceErrorCode.BENEFIT_INFORMATION_EMPTY.getCode(),
                "Benefit Information can not be empty"));
        } else {
            for (String languageIsoCode : benefitInformation.keySet()) {

                BenefitInformationDTO infoDTO = benefitInformation.get(languageIsoCode);
                if (infoDTO.getTitle() == null) {
                    errors.add(new ServiceError(ServiceErrorCode.BENEFIT_TITLE_EMPTY.getCode(), "Title can not be empty"));
                }
                if (infoDTO.getLink() != null && infoDTO.getLinkTitle() == null) {
                    errors.add(new ServiceError(ServiceErrorCode.BENEFIT_LINK_EMPTY.getCode(),
                        "Link Title must be filled when Link has been entered"));
                }

            }
        }

        if (!errors.isEmpty()) {
            throw new ServiceException(errors);
        }
    }

    private void validateSearchBenefits(BenefitFilterContainerDTO benefitFilterContainerDTO) {
        List<ServiceError> errors = Lists.newArrayList();
        if (benefitFilterContainerDTO.getPageNumber() == null) {
            errors.add(new ServiceError("PageNumber cannot be null"));
        }
        if (benefitFilterContainerDTO.getPageSize() == null) {
            errors.add(new ServiceError("PageSize cannot be null"));
        }
        if (benefitFilterContainerDTO != null) {
            for (BenefitFilterDTO benefit : benefitFilterContainerDTO.getBenefitFilterDTOs()) {
                if (benefit.getBrandCode() == StringUtils.EMTPY_STRING) {
                    errors.add(new ServiceError("BrandCode cannot be empty"));
                }
                if (benefit.getCategoryCode() == StringUtils.EMTPY_STRING) {
                    errors.add(new ServiceError("CategoryCode cannot be empty"));
                }
                if (benefit.getDestinationType() == StringUtils.EMTPY_STRING) {
                    errors.add(new ServiceError("DestinationType cannot be empty"));
                }
                if (benefit.getOriginType() == StringUtils.EMTPY_STRING) {
                    errors.add(new ServiceError("getOriginType cannot be empty"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ServiceException(errors);
        }
    }

    private void validateBenefitStatus(Benefit benefit, Long oid, boolean isRecentlyCancelled) {
        if (this.isPublished(benefit) && !isRecentlyCancelled) {
            String message = StringUtils.concat("Could not update a published benefit [OID]:", oid);
            logger.warn(message);
            throw new ServiceException(Lists.newArrayList(new ServiceError("ERROR", message)));
        }

        if (this.isCanceled(benefit)) {
            String message = StringUtils.concat("Could not update a cancelled benefit [OID]:", oid);
            logger.warn(message);
            throw new ServiceException(Lists.newArrayList(new ServiceError("ERROR", message)));
        }
    }

    @Override
    public byte[] mergePDFs(MergePDFsDTO mergePDFsDTO) {
        Preconditions.checkArgument(mergePDFsDTO != null, "Error in service mergePDFs: mergePDFsDTO must not be null.");
        Preconditions.checkArgument(!mergePDFsDTO.getBenefitOidList().isEmpty(),
            "Error in service mergePDFs: benefitOidList must not be empty.");
        Preconditions.checkArgument(mergePDFsDTO.getLanguage() != null,
            "Error in service mergePDFs: language must not be null.");

        byte[] mergedPDFsFileByetArray = null;
        String mergedPDFsFileName = null;
        try {
            List<String> benefitsPdfPaths = this.getBenefitsPdfPaths(mergePDFsDTO.getBenefitOidList(),
                mergePDFsDTO.getLanguage());

            mergedPDFsFileName = this.pdfGeneratorService.mergePDFs(benefitsPdfPaths);

            mergedPDFsFileByetArray = this.getMergedPDFsFileByetArray(mergedPDFsFileName);

            logger.info(StringUtils.concat("Merged file name: ", mergedPDFsFileName));

        } finally {
            if (mergedPDFsFileName != null) {
                File mergedFile = new File(mergedPDFsFileName);
                if (mergedFile.exists()) {
                    mergedFile.delete();
                }
            }
        }

        return mergedPDFsFileByetArray;
    }

    private byte[] getMergedPDFsFileByetArray(String mergedPDFsFileName) {
        byte[] byteArray = {};
        try {
            File mergedFile = new File(mergedPDFsFileName);
            FileInputStream mergedFileInputStream = new FileInputStream(mergedFile);

            byteArray = IOUtils.toByteArray(mergedFileInputStream);
            mergedFileInputStream.close();
        } catch (IOException e) {
            String message = "Error in service getMergedPDFsFileByetArray coverting mergedPDFsFile to byte array ";
            logger.error(message, e);
            throw new SobekServiceException(message, e);
        }

        return Base64.encodeBase64(byteArray);
    }

    private List<String> getBenefitsPdfPaths(List<Long> benefitOidList, String language) {
        logger.info("Getting vouchers for benefits");
        List<String> pdfsPaths = new ArrayList<String>();

        QueryResponse benefitsQueryResponse = this.benefitIndexManager.getBenefitsByOIDs(benefitOidList);
        List<BenefitMergeFilterDTO> benefitsByOIDs = this.solrBenefitMergeBuilder.buildBenefitList(benefitsQueryResponse);
        for (BenefitMergeFilterDTO benefit : benefitsByOIDs) {
            String link = benefit.getLinks().get(language.toUpperCase());
            if (link != null) {
                pdfsPaths.add(link);
                logger.info(StringUtils.concat("Benefit oid: ", benefit.getOID(), " voucher url: ", link));
            }
        }
        return pdfsPaths;
    }

    private boolean isPublished(Benefit benefit) {
        return BenefitStatusCode.PUBLISHED.getCode().equals(benefit.getBenefitStatus().getCode());
    }

    private boolean isCanceled(Benefit benefit) {
        return BenefitStatusCode.CANCELLED.getCode().equals(benefit.getBenefitStatus().getCode());
    }

    public void setBenefitTranslator(BenefitTranslator benefitTranslator) {
        this.benefitTranslator = benefitTranslator;
    }

    public void setBenefitDAO(BenefitDAO benefitDAO) {
        this.benefitDAO = benefitDAO;
    }

    public void setPdfGeneratorService(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public void setBenefitFilterResultTranslator(BenefitFilterResultTranslator benefitFilterResultTranslator) {
        this.benefitFilterResultTranslator = benefitFilterResultTranslator;
    }

    public void setBenefitIndexManager(BenefitIndexManager benefitIndexManager) {
        this.benefitIndexManager = benefitIndexManager;
    }

    public void setBenefitBuilder(BenefitBuilder benefitBuilder) {
        this.benefitBuilder = benefitBuilder;
    }

    public void setGeoAreaService(GeoAreaService geoAreaService) {
        this.geoAreaService = geoAreaService;
    }

    public void setCacheTemplate(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    public void setPictureTranslator(PictureTranslator pictureTranslator) {
        this.pictureTranslator = pictureTranslator;
    }

    public void setSolrBenefitMergeBuilder(SolrBenefitMergeBuilder solrBenefitMergeBuilder) {
        this.solrBenefitMergeBuilder = solrBenefitMergeBuilder;
    }


}
