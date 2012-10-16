package com.despegar.sobek.service.impl;

import static org.mockito.Matchers.any;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.despegar.sobek.exception.SobekServiceException;
import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;


public class PdfGeneratorServiceImplTest {
    private PDFGeneratorServiceImpl pdfConverterServiceImpl = new PDFGeneratorServiceImpl();

    @Mock
    ITextRenderer rendererMock;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.pdfConverterServiceImpl.setRealPath(System.getProperty("java.io.tmpdir"));
        this.pdfConverterServiceImpl.setMergeRealPath(System.getProperty("java.io.tmpdir"));
        this.pdfConverterServiceImpl.setTemplateName("testTemplatePDF.ftl");
        this.pdfConverterServiceImpl.setRenderer(this.rendererMock);
        this.pdfConverterServiceImpl.setTemplatePath("src/test/resources/com/despegar/sobek/service/impl/");
        this.pdfConverterServiceImpl.setVirtualPath("/search/pdf");
        this.pdfConverterServiceImpl.init();
    }

    @Test(expected = SobekServiceException.class)
    public void savePdf_error_throwSobekException() throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> example = new HashMap<String, Object>();
        root.put("example", example);
        example.put("string", "Hello World");

        Mockito.doThrow(new DocumentException()).when(this.rendererMock).createPDF(any(ByteArrayOutputStream.class));
        this.pdfConverterServiceImpl.generatePDF(root);

    }

    @Test
    public void savePdf_correct_returnsFilePath() throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        Map<String, Object> example = new HashMap<String, Object>();
        root.put("example", example);
        example.put("string", "Hello World");

        String path = this.pdfConverterServiceImpl.generatePDF(root);

        Mockito.verify(this.rendererMock).setDocumentFromString(any(String.class));
        Mockito.verify(this.rendererMock).layout();
        Mockito.verify(this.rendererMock).createPDF(any(ByteArrayOutputStream.class));

        String realPath = path.replace("/search/pdf", System.getProperty("java.io.tmpdir"));
        TestCase.assertTrue(new File(realPath).exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergePDFs_paramNull_throwsIllegalArgumentException() {
        List<String> urlList = null;
        this.pdfConverterServiceImpl.mergePDFs(urlList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergePDFs_emptyList_throwsIllegalArgumentException() {
        List<String> urlList = Lists.newArrayList();
        this.pdfConverterServiceImpl.mergePDFs(urlList);
    }

    @Test
    public void mergePDFs_paramOk_returnFileName() throws IOException, DocumentException, URISyntaxException {
        File fileA = new File(System.getProperty("java.io.tmpdir"), "fileA.pdf");
        FileOutputStream fileOutputStreamA = new FileOutputStream(fileA);
        Document documentA = new Document();
        PdfWriter.getInstance(documentA, fileOutputStreamA);
        documentA.open();
        documentA.add(new Paragraph("Some string A"));
        documentA.close();
        fileOutputStreamA.close();

        File fileB = new File(System.getProperty("java.io.tmpdir"), "fileB.pdf");
        FileOutputStream fileOutputStreamB = new FileOutputStream(fileB);
        Document documentB = new Document();
        PdfWriter.getInstance(documentB, fileOutputStreamB);
        documentB.open();
        documentB.add(new Paragraph("Some string B"));
        documentB.close();
        fileOutputStreamB.close();

        List<String> fileInputList = Lists.newArrayList(fileA.getPath(), fileB.getPath());
        List<String> urlList = Lists.newArrayList();
        urlList.add(fileInputList.get(0));
        urlList.add(fileInputList.get(1));

        String mergePDFsFileName = this.pdfConverterServiceImpl.mergePDFs(urlList);

        // Assert file
        Assert.assertNotNull(mergePDFsFileName);

        File mergedFile = new File(mergePDFsFileName);
        FileInputStream mergedFileInputStream = new FileInputStream(mergedFile);

        PdfReader reader = new PdfReader(mergedFileInputStream);

        // Assert number of pages
        Assert.assertTrue(reader.getNumberOfPages() == 2);


        reader.close();
        mergedFileInputStream.close();

        // Deletes temporal files
        Assert.assertTrue(fileA.delete());
        Assert.assertTrue(fileB.delete());
        Assert.assertTrue(mergedFile.delete());

    }
}
