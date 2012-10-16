package com.despegar.sobek.translator;

import com.despegar.framework.mapper.translator.AbstractIdentifiableDTOTranslator;
import com.despegar.sobek.dto.ContactDTO;
import com.despegar.sobek.model.Contact;

public class ContactTranslator extends AbstractIdentifiableDTOTranslator<ContactDTO, Contact> {

	@Override
	protected Contact fillPersistentObject(ContactDTO dto, Contact entity) {
		entity.setCellphone(dto.getCellphone());
		entity.setEmail(dto.getEmail());
		entity.setName(dto.getName());
		entity.setNotes(dto.getNotes());
		entity.setOID(dto.getOID());
		entity.setPhone(dto.getPhone());
		entity.setPosition(dto.getPosition());
		entity.setSkype(dto.getSkype());
		return entity;
	}

	@Override
	protected ContactDTO fillDTO(ContactDTO dto, Contact persistentObject) {
		dto.setCellphone(persistentObject.getCellphone());
		dto.setEmail(persistentObject.getEmail());
		dto.setName(persistentObject.getName());
		dto.setOID(persistentObject.getOID());
		dto.setNotes(persistentObject.getNotes());
		dto.setPhone(persistentObject.getPhone());
		dto.setPosition(persistentObject.getPosition());
		dto.setSkype(persistentObject.getSkype());
		return dto;
	}

}
