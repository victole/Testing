package com.despegar.sobek.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.despegar.sobek.helper.PictureHelper;

public class RatioPictureTransformerTest {

	private RatioPictureTransformer instance;

	private PictureHelper pictureHelper;

	@Before
	public void setUp() {
		this.instance = new RatioPictureTransformer();
		this.pictureHelper = new PictureHelper();
	}

	@Test
	public void transform_correct_returnBufferedImage() throws URISyntaxException, Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/utility/opaque_horizontal_ratio.jpg");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		BufferedImage image = this.pictureHelper.drawImage("jpg", testingImageAsB64);
		Dimension dimension = new Dimension(10, 10);

		BufferedImage buffered = this.instance.transform(image, dimension);

		assertNotNull(buffered);
		assertEquals(10, buffered.getWidth());
		assertTrue(buffered.getHeight() < 10);

	}

	@Test
	public void transform_transparency_returnBufferedImage() throws URISyntaxException, Exception {
		URL pictureURL = this.getClass().getResource("/com/despegar/sobek/utility/transparency_vertical_ratio.png");
		String testingImageAsB64 = this.pictureHelper.getBase64Picture(pictureURL.toURI());
		BufferedImage image = this.pictureHelper.drawImage("png", testingImageAsB64);
		Dimension dimension = new Dimension(10, 10);

		BufferedImage buffered = this.instance.transform(image, dimension);

		assertNotNull(buffered);
		assertEquals(10, buffered.getHeight());
		assertTrue(buffered.getWidth() < 10);
	}
}
