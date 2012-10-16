package com.despegar.sobek.dao;

import static junit.framework.Assert.assertEquals;

import org.springframework.test.context.ContextConfiguration;

import com.despegar.framework.persistence.hibernate.dao.ReadWriteObjectWLDDAO;
import com.despegar.framework.persistence.test.support.AbstractReadWriteObjectWLDDAOTest;
import com.despegar.sobek.model.PictureSize;

@ContextConfiguration(locations = { "classpath:com/despegar/test/test-reference-data-context.xml" })
public class PictureSizeDAOTest extends
		AbstractReadWriteObjectWLDDAOTest<ReadWriteObjectWLDDAO<PictureSize>, PictureSize> {

	@Override
	protected PictureSize createObject() {
		PictureSize pictureSize = new PictureSize();
		pictureSize.setHeight(340);
		pictureSize.setName("HotelIMG0125.jpg");
		pictureSize.setWidth(640);
		return pictureSize;
	}

	@Override
	protected String getBeanName() {
		return "pictureSizeDAO";
	}

	@Override
	protected void updateObject(PictureSize object) {
		object.setHeight(600);
		object.setName("HotelIMG0126.jpg");
		object.setWidth(800);
	}

	@Override
	protected void assertUpdated(PictureSize object) {
		assertEquals("HotelIMG0126.jpg", object.getName());
		assertEquals(600, object.getHeight().intValue());
		assertEquals(800, object.getWidth().intValue());
	}
}
