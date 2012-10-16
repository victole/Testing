package com.despegar.sobek.service.impl;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.despegar.framework.persistence.hibernate.dao.generic.GenericReadWriteObjectWLDDAO;
import com.despegar.framework.picture.utils.transformer.PictureTransformer;
import com.despegar.library.rest.connector.exceptions.ServiceError;
import com.despegar.library.rest.connector.exceptions.ServiceException;
import com.despegar.framework.utils.string.StringUtils;
import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.exception.SobekServiceException;
import com.despegar.sobek.helper.PictureHelper;
import com.despegar.sobek.model.PictureSize;
import com.despegar.sobek.service.PictureService;

public class PictureServiceImpl implements PictureService {

	private static Logger logger = Logger.getLogger(PictureServiceImpl.class);

	private GenericReadWriteObjectWLDDAO<PictureSize> pictureSizeDAO;
	private PictureTransformer pictureTransformer;
	private PictureHelper pictureHelper;

	private String virtualPath;
	private String realPath;
	private Integer minRequiredWidth;
	private Integer minRequiredHeight;

	@Override
	public PictureDTO uploadAndProcessPicture(String pictureFileName, String b64Content) throws SobekServiceException {
		logger.info("Starting to upload image: " + pictureFileName);

		this.validateParams(pictureFileName, b64Content);

		File picture = this.savePicture(pictureFileName, b64Content);
		Collection<PictureSize> pictureSizes = this.pictureSizeDAO.readAll();
		PictureDTO pictureDTO = this.generateMultiplePictureSizes(picture, pictureSizes);
		logger.info(pictureFileName + " uploaded successfuly");

		return pictureDTO;
	}

	private void validateParams(String pictureFileName, String b64Content) {
		List<ServiceError> errors = new ArrayList<ServiceError>();
		if (pictureFileName == null || pictureFileName.isEmpty()) {
			errors.add(new ServiceError("Picture Filename cannot be null or empty"));
		}

		if (b64Content == null || b64Content.isEmpty()) {
			errors.add(new ServiceError("Picture content cannot be null or empty"));
		}

		if (!errors.isEmpty()) {
			throw new ServiceException(errors);
		}
	}

	public File savePicture(String pictureFileName, String b64Content) {

		String pictureFileExtension = FilenameUtils.getExtension(pictureFileName);
		logger.debug("Drawing image [fileExtension]: " + pictureFileExtension);

		BufferedImage bufferedImage = this.pictureHelper.drawImage(pictureFileExtension, b64Content);

		this.validatePictureSize(bufferedImage);
		String uniquePictureName = this.pictureHelper.generateUniqueName(pictureFileName);
		this.pictureHelper.writePicture(bufferedImage, this.realPath, uniquePictureName, pictureFileExtension);

		return new File(this.realPath, uniquePictureName);
	}

	public PictureDTO generateMultiplePictureSizes(File picture, Collection<PictureSize> pictureSizes) {

		BufferedImage pictureContent = this.pictureHelper.readPicture(picture);

		PictureDTO pictureDTO = new PictureDTO();
		pictureDTO.setFileName(picture.getName());

		this.createMultiplePictureSizes(picture, pictureContent, pictureDTO, pictureSizes);

		return pictureDTO;
	}

	private void createMultiplePictureSizes(File originalPicture, BufferedImage pictureContent, PictureDTO pictureDTO,
			Collection<PictureSize> pictureSizes) {
		String virtualPicturePath = null;
		String pictureBySizePath = null;
		String fileName = originalPicture.getName();
		String filePath = originalPicture.getParent();
		Map<String, String> urls = new HashMap<String, String>();
		String pictureFileExtension = FilenameUtils.getExtension(fileName);
		for (PictureSize pictureSize : pictureSizes) {

			pictureBySizePath = StringUtils.concat(filePath, File.separator, pictureSize.getName(), File.separator);
			Dimension dimension = new Dimension(pictureSize.getWidth(), pictureSize.getHeight());
			BufferedImage transformedPicture = this.pictureTransformer.transform(pictureContent, dimension);
			try {
				this.pictureHelper.writePicture(transformedPicture, pictureBySizePath, fileName, pictureFileExtension);
				virtualPicturePath = StringUtils.concat(this.virtualPath, File.separator, pictureSize.getName(),
						File.separator, fileName);

				urls.put(pictureSize.getName(), virtualPicturePath);
			} catch (Exception e) {
				logger.warn(StringUtils
						.concat("Could not save picture with name:", fileName, " in ", pictureBySizePath));
			}

		}
		pictureDTO.setURLs(urls);
	}

	private void validatePictureSize(BufferedImage image) {

		boolean hasRequiredSize = this.pictureHelper.isBiggerThan(image, this.minRequiredWidth, this.minRequiredHeight);

		if (!hasRequiredSize) {
			String message = StringUtils.concat("Image with width:", image.getWidth(), ",height:", image.getHeight(),
					" below requiered size width:", this.minRequiredWidth, ",height:", this.minRequiredHeight);
			logger.warn(message);
			throw new SobekServiceException(message);
		}
	}

	public void setPictureSizeDAO(GenericReadWriteObjectWLDDAO<PictureSize> pictureSizeDAO) {
		this.pictureSizeDAO = pictureSizeDAO;
	}

	public void setMinRequiredWidth(Integer minRequiredWidth) {
		this.minRequiredWidth = minRequiredWidth;
	}

	public void setMinRequiredHeight(Integer minRequiredHeight) {
		this.minRequiredHeight = minRequiredHeight;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public void setPictureTransformer(PictureTransformer pictureTransformer) {
		this.pictureTransformer = pictureTransformer;
	}

	public void setPictureHelper(PictureHelper pictureHelper) {
		this.pictureHelper = pictureHelper;
	}
}
