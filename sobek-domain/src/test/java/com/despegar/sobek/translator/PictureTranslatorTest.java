package com.despegar.sobek.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.persistence.hibernate.dao.generic.AbstractReadObjectDAO;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.model.Picture;
import com.despegar.sobek.model.PictureSize;
import com.despegar.sobek.utility.ModelContentCreator;
import com.google.common.collect.Lists;

public class PictureTranslatorTest {

    private static final String BASEURL = "http://localhost";

    @Mock
    private AbstractReadObjectDAO<PictureSize> pictureSizeDAOMock;
    private PictureTranslator instance = new PictureTranslator();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.instance.setPictureBaseURL(BASEURL);
        this.instance.setPictureSizeDAO(this.pictureSizeDAOMock);
    }

    @Test
    public void getDTO_correct_returnPictureDTO() {

        String resourceName = "picturename.jpg";
        Picture picture = new Picture();
        picture.setResourceName(resourceName);

        PictureSize pictureSize60x60 = ModelContentCreator.createPictureSize(60, 60);
        PictureSize pictureSize40x40 = ModelContentCreator.createPictureSize(40, 40);
        Collection<PictureSize> pictureSizes = Lists.newArrayList(pictureSize60x60, pictureSize40x40);

        when(this.pictureSizeDAOMock.readAll()).thenReturn(pictureSizes);
        PictureDTO pictureDTO = this.instance.getDTO(picture);

        assertNotNull(pictureDTO);
        assertEquals(resourceName, pictureDTO.getFileName());
        Map<String, String> URLs = pictureDTO.getURLs();
        assertNotNull(URLs);

        assertEquals(pictureSizes.size(), URLs.size());

        for (PictureSize key : pictureSizes) {
            assertEquals(BASEURL + "/" + key.getName() + "/" + resourceName, URLs.get(key.getName()));
        }
    }

    @Test
    public void fillPictureDTO_correct_returnPictureDTO() {

        String resourceName = "picturename.jpg";

        PictureSize pictureSize60x60 = ModelContentCreator.createPictureSize(60, 60);
        PictureSize pictureSize40x40 = ModelContentCreator.createPictureSize(40, 40);
        Collection<PictureSize> pictureSizes = Lists.newArrayList(pictureSize60x60, pictureSize40x40);

        when(this.pictureSizeDAOMock.readAll()).thenReturn(pictureSizes);
        PictureDTO pictureDTO = this.instance.fillPictureDTO(resourceName);

        assertNotNull(pictureDTO);
        assertEquals(resourceName, pictureDTO.getFileName());
        Map<String, String> URLs = pictureDTO.getURLs();
        assertNotNull(URLs);

        assertEquals(pictureSizes.size(), URLs.size());

        for (PictureSize key : pictureSizes) {
            assertEquals(BASEURL + "/" + key.getName() + "/" + resourceName, URLs.get(key.getName()));
        }
    }

    @Test
    public void fillPictureDTO_nullPicture_returnNull() {
        PictureDTO pictureDTO = this.instance.fillPictureDTO(null);
        assertNull(pictureDTO);
    }

    @Test
    public void fillPersistentObject_correct_returnsPicture() {
        PictureDTO pictureDTO = new PictureDTO();
        pictureDTO.setFileName("pictureName.jpg");
        Picture picture = this.instance.getPersistentObject(pictureDTO);
        assertEquals(pictureDTO.getFileName(), picture.getResourceName());
        assertEquals(pictureDTO.getOID(), picture.getOID());
    }
}
