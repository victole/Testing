package com.despegar.sobek.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.service.ResourceService;

public class ResourceServiceImpl implements ResourceService {

    private static final String EXTENSION_SEPARATOR = ".";

    private static Logger logger = Logger.getLogger(PictureServiceImpl.class);

    private static final String UNIQUE_NAME_SEPARATOR = "_";
    private String virtualPath;
    private String realPath;

    @Override
    public String uploadResource(String resourceFilename, String resourceB64Content) {

        logger.info(StringUtils.concat("Attemping to save resource with original name: ", resourceFilename));

        this.validateParams(resourceFilename, resourceB64Content);

        String resourceUniqueFilename = this.generateUniqueName(resourceFilename);

        File resourceFile = new File(this.realPath, resourceUniqueFilename);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(resourceFile);
            fileOutputStream.write(Base64.decodeBase64(resourceB64Content));
        } catch (IOException ioe) {
            String message = StringUtils.concat("Could not upload resource with name: ", resourceB64Content);
            logger.error(message);
            throw new SobekServiceException(message, ioe);
        } finally {
            if (fileOutputStream != null) {
                IOUtils.closeQuietly(fileOutputStream);
            }
        }
        logger.info("Resource saved successfuly");

        String virtualResourcePath = StringUtils.concat(this.virtualPath, File.separator, resourceUniqueFilename);
        logger.info(StringUtils.concat("Returning virtual resource path: ", virtualResourcePath));
        return virtualResourcePath;
    }

    private String generateUniqueName(String filename) {
        String fileExtension = FilenameUtils.getExtension(filename);
        String fileSimpleName = FilenameUtils.getBaseName(filename);
        return StringUtils.concat(fileSimpleName, UNIQUE_NAME_SEPARATOR, UUID.randomUUID().toString(),
                EXTENSION_SEPARATOR, fileExtension);
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public void setVirtualPath(String virtualPath) {
        this.virtualPath = virtualPath;
    }

    private void validateParams(String resourceFileName, String b64Content) {
        List<ServiceError> errors = new ArrayList<ServiceError>();
        if (resourceFileName == null || resourceFileName.isEmpty()) {
            errors.add(new ServiceError("Resource Filename cannot be null or empty"));
        }

        if (b64Content == null || b64Content.isEmpty()) {
            errors.add(new ServiceError("Resource content cannot be null or empty"));
        }

        if (!errors.isEmpty()) {
            throw new ServiceException(errors);
        }
    }
}
