/*
 * 
 */
package com.stayprime.tournament.util;

import com.google.gson.Gson;
import com.stayprime.tournament.model.ScoringFormat;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author benjamin
 */
@Converter(autoApply = true)
public class JpaScoringFormatConverter implements AttributeConverter<ScoringFormat, String> {
    private Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(ScoringFormat entityValue) {
        if( entityValue == null ) {
            return null;
        }
        return gson.toJson(entityValue);
    }

    @Override
    public ScoringFormat convertToEntityAttribute(String databaseValue) {
        if( databaseValue == null ) {
            return null;
        }
        return gson.fromJson(databaseValue, ScoringFormat.class);
    }
}