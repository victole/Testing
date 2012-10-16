package com.despegar.sobek.service.impl;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import com.despegar.library.rest.connector.exceptions.ServiceException;

public class ResourceServiceImplTest {

    private ResourceServiceImpl instance;

    @Before
    public void setUp() {
        this.instance = new ResourceServiceImpl();
        this.instance.setRealPath(System.getProperty("java.io.tmpdir"));
        this.instance.setVirtualPath("virtual" + File.separator + "path");
    }


    @Test
    public void uploadResource_correct_returnsVirtualPath() throws IOException, URISyntaxException {
        String resourceFilename = "resourceService.pdf";
        String resourceB64Content = this.getB64Content(resourceFilename);
        String path = this.instance.uploadResource(resourceFilename, resourceB64Content);

        assertNotNull(path);
        assertTrue(path.contains("virtual" + File.separator + "path" + File.separator + "resourceService"));
        String baseName = FilenameUtils.getName(path);
        File file = new File(System.getProperty("java.io.tmpdir"), baseName);
        assertTrue(file.exists());

    }


    @Test(expected = ServiceException.class)
    public void uploadResource_nullPictureName_throwsServiceException() throws Exception {
        String testingImageAsB64 = this.getB64Content("resourceService.pdf");
        String pictureName = null;
        this.instance.uploadResource(pictureName, testingImageAsB64);
    }

    @Test(expected = ServiceException.class)
    public void uploadResource_emptyPictureName_throwsServiceException() throws Exception {
        String testingImageAsB64 = this.getB64Content("resourceService.pdf");
        String pictureName = "";
        this.instance.uploadResource(pictureName, testingImageAsB64);
    }

    @Test(expected = ServiceException.class)
    public void uploadResource_nullPictureContent_throwsServiceException() throws Exception {
        String testingImageAsB64 = null;
        this.instance.uploadResource("resourceService.pdf", testingImageAsB64);
    }

    @Test(expected = ServiceException.class)
    public void uploadResource_emptyPictureContent_throwsServiceException() throws Exception {
        String testingImageAsB64 = "";
        this.instance.uploadResource("resourceService.pdf", testingImageAsB64);
    }

    private String getB64Content(String resourceFilename) throws IOException, URISyntaxException {
        URL resource = this.getClass().getResource(resourceFilename);
        byte[] bytes = FileUtils.readFileToByteArray(new File(resource.toURI()));

        String resourceB64Content = Base64.encodeBase64String(bytes);
        return resourceB64Content;
    }
}
