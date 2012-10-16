package com.despegar.sobek.translator;

import java.util.HashMap;
import java.util.Map;

import com.despegar.framework.mapper.translator.AbstractIdentifiableDTOTranslator;
import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadObjectDAO;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.model.PictureSize;

public class PictureTranslator
    extends AbstractIdentifiableDTOTranslator<PictureDTO, Picture> {

    private static final String URL_PATH_SEPARATOR = "/";

    private AbstractReadObjectDAO<PictureSize> pictureSizeDAO;
    private String pictureBaseURL;

    public void setPictureSizeDAO(AbstractReadObjectDAO<PictureSize> pictureSizeDAO) {
        this.pictureSizeDAO = pictureSizeDAO;
    }

    public void setPictureBaseURL(String pictureBaseURL) {
        this.pictureBaseURL = pictureBaseURL;
    }

    public PictureDTO fillPictureDTO(String fileName) {
        if (fileName == null) {
            return null;
        }
        PictureDTO pictureDTO = new PictureDTO();
        Map<String, String> pictureURLs = new HashMap<String, String>();
        for (PictureSize size : this.pictureSizeDAO.readAll()) {
            pictureURLs.put(size.getName(),
                StringUtils.concat(this.pictureBaseURL, URL_PATH_SEPARATOR, size.getName(), URL_PATH_SEPARATOR, fileName));
        }

        pictureDTO.setFileName(fileName);
        pictureDTO.setURLs(pictureURLs);
        return pictureDTO;
    }

    @Override
    protected Picture fillPersistentObject(PictureDTO dto, Picture entity) {
        entity.setOID(dto.getOID());
        entity.setResourceName(dto.getFileName());
        return entity;
    }

    @Override
    protected PictureDTO fillDTO(PictureDTO dto, Picture persistentObject) {
        if (persistentObject != null && persistentObject.getResourceName() != null) {
            Map<String, String> pictureURLs = new HashMap<String, String>();
            for (PictureSize size : this.pictureSizeDAO.readAll()) {
                pictureURLs.put(size.getName(), StringUtils.concat(this.pictureBaseURL, URL_PATH_SEPARATOR, size.getName(),
                    URL_PATH_SEPARATOR, persistentObject.getResourceName()));
            }
            dto.setOID(persistentObject.getOID());
            dto.setFileName(persistentObject.getResourceName());
            dto.setURLs(pictureURLs);
        }
        return dto;
    }

}
