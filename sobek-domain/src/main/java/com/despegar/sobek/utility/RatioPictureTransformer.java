package com.despegar.sobek.utility;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.despegar.framework.picture.utils.transformer.PictureTransformer;

public class RatioPictureTransformer extends PictureTransformer {

	@Override
	public BufferedImage transform(BufferedImage image, Dimension dimension) {
		int thumbWidth = dimension.width;
		int thumbHeight = dimension.height;

		// Make sure the aspect ratio is maintained, so the image is not skewed
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;
		double imageRatio = (double) imageWidth / (double) imageHeight;

		if (thumbRatio < imageRatio) {
			thumbHeight = (int) (thumbWidth / imageRatio);
		} else {
			thumbWidth = (int) (thumbHeight * imageRatio);
		}

		int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;

		Image scaledImage = image.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH);

		return this.createBufferedImage(scaledImage, BufferedImage.SCALE_DEFAULT | type);

	}

	private BufferedImage createBufferedImage(Image imageIn, int imageType) {
		BufferedImage bufferedImageOut = new BufferedImage(imageIn.getWidth(null), imageIn.getHeight(null), imageType);
		Graphics2D graphics = bufferedImageOut.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(imageIn, 0, 0, null);

		return bufferedImageOut;
	}
}
