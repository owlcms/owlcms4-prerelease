/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.apputils;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JpaJsonConverter implements AttributeConverter<Object, String> {
	private static final ObjectMapper om = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Object attribute) {
		try {
			return om.writeValueAsString(attribute);
		} catch (JsonProcessingException ex) {
			// log.error("Error while transforming Object to a text datatable column as json string", ex);
			return null;
		}
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		try {
			return om.readValue(dbData, Object.class);
		} catch (IOException ex) {
			// log.error("IO exception while transforming json text column in Object property", ex);
			return null;
		}
	}
}