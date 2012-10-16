package com.despegar.sobek.service;

import com.despegar.sobek.dto.PictureDTO;
import com.despegar.sobek.exception.SobekServiceException;

public interface PictureService {

	/**
	 * Uploads given image in base64 and creates different size thumbs.
	 * 
	 * @param imagefileName
	 * @param imageb64Content
	 * @return {@link PictureDTO} on success
	 * @throws sobekServiceException
	 *             if image could not be uploaded or resized
	 */
	public PictureDTO uploadAndProcessPicture(String imagefileName, String imageB64Content) throws SobekServiceException;
}
