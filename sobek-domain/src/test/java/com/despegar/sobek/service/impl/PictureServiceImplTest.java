package com.despegar.sobek.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.despegar.framework.persistence.hibernate.dao.generic.GenericReadWriteObjectWLDDAO;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.helper.PictureHelper;
import com.despegar.sobek.model.PictureSize;
import com.despegar.sobek.utility.RatioPictureTransformer;
import com.google.common.collect.Lists;

public class PictureServiceImplTest {

	private static final String TMP_PATH = System.getProperty("java.io.tmpdir");
	private static final String VIRTUAL_PATH = TMP_PATH + File.separator + "virtual";

	@Mock
	private RatioPictureTransformer pictureTransformerMock;
	@Mock
	private GenericReadWriteObjectWLDDAO<PictureSize> pictureSizeDAOMock;

	private PictureHelper pictureHelper = new PictureHelper();

	private PictureServiceImpl instance = new PictureServiceImpl();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.pictureHelper.setEncoding("ISO-8859-1");
		this.instance.setPictureSizeDAO(this.pictureSizeDAOMock);
		this.instance.setPictureHelper(this.pictureHelper);
		this.instance.setMinRequiredHeight(60);
		this.instance.setMinRequiredWidth(60);
		this.instance.setRealPath(TMP_PATH);
		this.instance.setVirtualPath(VIRTUAL_PATH);
		this.instance.setPictureTransformer(this.pictureTransformerMock);
	}

	@Test
	public void uploadAndProcessImage_realPicture_returnsPictureDTO() throws Exception {

		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/service/impl/pictureService.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		PictureDTO pictureDTO = this.instance.uploadAndProcessPicture("pictureService.jpg", testingImageAsB64);

		verify(this.pictureSizeDAOMock).readAll();

		assertNotNull(pictureDTO);
		assertNotNull(pictureDTO.getFileName());
	}

	@Test(expected = ServiceException.class)
	public void uploadAndProcessPicture_nullPictureName_throwsPictureServiceException() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/service/impl/pictureService.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		String pictureName = null;
		this.instance.uploadAndProcessPicture(pictureName, testingImageAsB64);
	}

	@Test(expected = ServiceException.class)
	public void uploadAndProcessPicture_emptyPictureName_throwsPictureServiceException() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/service/impl/pictureService.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		String pictureName = "";
		this.instance.uploadAndProcessPicture(pictureName, testingImageAsB64);
	}

	@Test(expected = ServiceException.class)
	public void uploadAndProcessPicture_nullPictureContent_throwsPictureServiceException() throws Exception {
		String testingImageAsB64 = null;
		this.instance.uploadAndProcessPicture("pictureService.jpg", testingImageAsB64);
	}

	@Test(expected = ServiceException.class)
	public void uploadAndProcessPicture_emptyPictureContent_throwsPictureServiceException() throws Exception {
		String testingImageAsB64 = "";
		this.instance.uploadAndProcessPicture("pictureService.jpg", testingImageAsB64);
	}

	@Test
	public void savePicture_jpgExtension_returnsPictureFile() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorker.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		File savedPicture = this.instance.savePicture("pictureProcessingWorker.jpg", testingImageAsB64);

		assertNotNull(savedPicture);

		String parent = savedPicture.getParent();
		String temp = System.getProperty("java.io.tmpdir");

		if (!(parent.endsWith("/") || parent.endsWith("\\")))
			parent = parent + System.getProperty("file.separator");

		if (!(temp.endsWith("/") || temp.endsWith("\\")))
			temp = temp + System.getProperty("file.separator");

		assertEquals(temp, parent);
		assertTrue(savedPicture.getName().contains("pictureProcessingWorker"));
		assertEquals(FilenameUtils.getExtension(savedPicture.getName()), "jpg");
	}

	@Test
	public void savePicture_pngExtension_returnsPictureFile() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorker.png");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		File savedPicture = this.instance.savePicture("pictureProcessingWorker.png", testingImageAsB64);

		assertNotNull(savedPicture);

		String parent = savedPicture.getParent();
		String temp = System.getProperty("java.io.tmpdir");

		if (!(parent.endsWith("/") || parent.endsWith("\\")))
			parent = parent + System.getProperty("file.separator");

		if (!(temp.endsWith("/") || temp.endsWith("\\")))
			temp = temp + System.getProperty("file.separator");

		assertEquals(temp, parent);
		assertTrue(savedPicture.getName().contains("pictureProcessingWorker"));
		assertEquals(FilenameUtils.getExtension(savedPicture.getName()), "png");
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePicture_nullPictureName_returnsPictureFile() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorker.png");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		String pictureFile = null;
		this.instance.savePicture(pictureFile, testingImageAsB64);
	}

	@Test(expected = SobekServiceException.class)
	public void savePicture_invalidPictureSize_returnsPictureFile() throws Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorkerSmall.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		this.instance.savePicture("pictureProcessingWorker.jpg", testingImageAsB64);
	}

	@Test
	public void genereateMultiplePictureSize_validPictureSizes_returnsPictureDTO() throws URISyntaxException,
			IOException {

		PictureSize pictureSize10x10 = this.createPictureSize(10, 10);
		PictureSize pictureSize15x15 = this.createPictureSize(15, 15);
		List<PictureSize> pictureSizes = Lists.newArrayList(pictureSize10x10, pictureSize15x15);
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorker.png");
		File picture = new File(pictureURL.toURI());
		File destFile = File.createTempFile("pictureProcessingWorker", ".png");
		FileUtils.copyFile(picture, destFile);

		when(this.pictureTransformerMock.transform(any(BufferedImage.class), any(Dimension.class)))
				.thenCallRealMethod();

		PictureDTO pictureDTO = this.instance.generateMultiplePictureSizes(destFile, pictureSizes);

		verify(this.pictureTransformerMock, times(2)).transform(any(BufferedImage.class), any(Dimension.class));

		assertNotNull(pictureDTO);
		assertTrue(pictureDTO.getFileName().contains("pictureProcessingWorker"));
		assertEquals(pictureSizes.size(), pictureDTO.getURLs().size());
		this.assertPictureUrls(pictureSizes, pictureDTO, destFile.getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void genereateMultiplePictureSize_nullPictureFile_throwsIllegalArgumentException()
			throws URISyntaxException, IOException {
		PictureSize pictureSize15x15 = this.createPictureSize(15, 15);
		List<PictureSize> pictureSizes = Lists.newArrayList(pictureSize15x15);
		PictureDTO pictureDTO = null;
		try {
			pictureDTO = this.instance.generateMultiplePictureSizes(null, pictureSizes);

		} catch (IllegalArgumentException pse) {
			verify(this.pictureTransformerMock, never()).transform(any(BufferedImage.class), any(Dimension.class));
			assertNull(pictureDTO);
			throw pse;
		}
	}

	@Test
	public void genereateMultiplePictureSize_nullPictureFile_returnEmptyUrlList() throws URISyntaxException,
			IOException {

		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/worker/pictureProcessingWorker.jpg");
		File picture = new File(pictureURL.toURI());
		File destFile = File.createTempFile("pictureProcessingWorker.jpg", Long.toString(System.nanoTime()));
		FileUtils.copyFile(picture, destFile);

		PictureHelper pictureHelperMock = Mockito.mock(PictureHelper.class);
		this.instance.setPictureHelper(pictureHelperMock);

		when(pictureHelperMock.readPicture(any(File.class))).thenCallRealMethod();
		when(pictureHelperMock.changeExtensionToJPG(any(String.class))).thenCallRealMethod();
		doThrow(new SobekServiceException("Could not write picture")).when(pictureHelperMock).writePicture(
				any(BufferedImage.class), any(String.class), any(String.class), any(String.class));

		PictureSize pictureSize15x15 = this.createPictureSize(15, 15);
		List<PictureSize> pictureSizes = Lists.newArrayList(pictureSize15x15);
		PictureDTO pictureDTO = this.instance.generateMultiplePictureSizes(destFile, pictureSizes);

		assertNotNull(pictureDTO);
		assertTrue(pictureDTO.getURLs().isEmpty());

	}

	private void assertPictureUrls(List<PictureSize> pictureSizes, PictureDTO pictureDTO, String fileName) {
		String virtualPicturePath;
		Map<String, String> url = pictureDTO.getURLs();
		assertNotNull(url);
		assertEquals(pictureSizes.size(), url.size());
		for (int i = 0; i < url.size(); i++) {
			virtualPicturePath = StringUtils.concat(VIRTUAL_PATH, File.separator, pictureSizes.get(i).getName(),
					File.separator, fileName);
			assertEquals(virtualPicturePath, url.get(pictureSizes.get(i).getName()));
		}
	}

	private PictureSize createPictureSize(int width, int height) {
		PictureSize pictureSize = new PictureSize();
		pictureSize.setName(width + "x" + height);
		pictureSize.setWidth(width);
		pictureSize.setHeight(height);
		return pictureSize;
	}
}
