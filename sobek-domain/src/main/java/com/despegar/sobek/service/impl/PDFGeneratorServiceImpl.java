package com.despegar.sobek.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.despegar.framework.utils.file.FileUtil;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.service.PDFGeneratorService;
import com.google.common.base.Preconditions;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class PDFGeneratorServiceImpl
    implements PDFGeneratorService {

    private static Logger logger = Logger.getLogger(PDFGeneratorServiceImpl.class);

    private static final String PDFEXTENSION = ".pdf";
    private String templateName;
    private String templatePath;
    private String realPath;
    private String resourceRealPath;
    private String mergeRealPath;
    private Template template;
    private ITextRenderer renderer;
    private String virtualPath;
    private String siteHost;

    private final static String BASE_RESOURCE_URL = "/sobek-resources/";

    public void init() throws IOException {
        String fullPath = FilenameUtils.getFullPath(this.templatePath);

        Configuration cfg = new Configuration();
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDirectoryForTemplateLoading(new File(fullPath));

        this.template = cfg.getTemplate(this.templateName);
    }

    private byte[] translateToPDF(String xhtmlDocumentString) {

        logger.debug(xhtmlDocumentString);
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            this.renderer.setDocumentFromString(xhtmlDocumentString);
            this.renderer.layout();
            this.renderer.createPDF(baos);
            bytes = baos.toByteArray();
        } catch (DocumentException e) {
            throw new SobekServiceException(e);
        } finally {
            FileUtil.tryClose(baos);
        }

        return bytes;
    }

    public String generatePDF(Map<String, Object> model) {

        StringWriter writer = new StringWriter();
        String fileName = UUID.randomUUID().toString();
        String outputFileName = StringUtils.concat(this.realPath, File.separator, fileName, PDFEXTENSION);
        logger.info("Generating PDF with name: " + outputFileName);
        try {
            model.put("siteHost", this.siteHost);
            this.template.process(model, writer);
            writer.flush();

            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
            fileOutputStream.write(this.translateToPDF(writer.toString()));

            String pdfVirtualPath = StringUtils.concat(this.virtualPath, File.separator, fileName, PDFEXTENSION);
            logger.info("Returning PDF with virtual path: " + pdfVirtualPath);
            return pdfVirtualPath;

        } catch (Exception e) {
            String message = "Error generating pdf in service generatePDF";
            logger.error(message, e);
            throw new SobekServiceException(message, e);
        }

    }

    @Override
    public String mergePDFs(List<String> pdfPaths) {
        Preconditions.checkArgument(pdfPaths != null, "Param streamOfPDFFiles must not be null");
        Preconditions.checkArgument(!pdfPaths.isEmpty(), "There is no pdf files to merge");

        Document document = null;
        int addedPages = 0;
        FileOutputStream fileOutputStream = null;
        String fileName = UUID.randomUUID().toString();
        String mergedPDFsFileName = StringUtils.concat(this.mergeRealPath, File.separator, fileName, PDFEXTENSION);

        try {
            document = new Document();
            fileOutputStream = new FileOutputStream(mergedPDFsFileName);

            PdfCopy pdfCopy = new PdfCopy(document, fileOutputStream);
            document.open();

            int pageOfCurrentReaderPDF = 1;

            for (String pdfPath : pdfPaths) {
                String fullRealPath = this.getFullRealPath(pdfPath);

                if (new File(fullRealPath).exists()) {
                    PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(fullRealPath), null);
                    while (pageOfCurrentReaderPDF <= pdfReader.getNumberOfPages()) {
                        pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader, pageOfCurrentReaderPDF));
                        pageOfCurrentReaderPDF++;
                        addedPages++;
                    }
                    pdfReader.close();
                    pageOfCurrentReaderPDF = 1;
                } else {
                    logger.warn(StringUtils.concat("The file ", fullRealPath, " does not exist - URL: ", pdfPath));
                }
            }

            fileOutputStream.flush();
            return mergedPDFsFileName;

        } catch (Exception e) {
            String message = "Error merging PDFs in service concatPDFs";
            logger.error(message, e);
            throw new SobekServiceException(message, e);
        } finally {

            if (document != null && document.isOpen() && addedPages > 0) {
                document.close();
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    String message = "Error closing mergedPDFs file in service concatPDFs";
                    logger.error(message, e);
                    throw new SobekServiceException(message, e);
                }
            }
        }
    }

    private String getFullRealPath(String pdfPath) {
        String pdfURLFileName = FilenameUtils.getName(pdfPath);
        String fullRealPath = StringUtils.EMTPY_STRING;

        Pattern directoryPattern = Pattern.compile(StringUtils.concat(".*", BASE_RESOURCE_URL, ".+"));
        if (directoryPattern.matcher(pdfPath).matches()) {
            fullRealPath = StringUtils.concat(this.resourceRealPath, File.separator, pdfURLFileName);
            logger.info(StringUtils.concat("Generated Full real path: ", fullRealPath));
            return fullRealPath;
        }

        fullRealPath = StringUtils.concat(this.realPath, File.separator, pdfURLFileName);
        logger.info(StringUtils.concat("Generated Full real path: ", fullRealPath));
        return fullRealPath;
    }

    // Setters for dependency injection
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setRenderer(ITextRenderer renderer) {
        this.renderer = renderer;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    public void setMergeRealPath(String mergeRealPath) {
        this.mergeRealPath = mergeRealPath;
    }

    public void setSiteHost(String siteHost) {
        this.siteHost = siteHost;
    }

    public void setResourceRealPath(String resourceRealPath) {
        this.resourceRealPath = resourceRealPath;
    }

}
