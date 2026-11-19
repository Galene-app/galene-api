package br.com.galene.api.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToNumberConverter implements AttributeConverter<Boolean, Integer> {

    public static final int TRUE_VALUE = 1;
    public static final int FALSE_VALUE = 0;
    private static final String UNEXPECTED_VALUE_MESSAGE = "Valor inesperado de NUMBER(1) : ";

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return null;

        return attribute ? TRUE_VALUE : FALSE_VALUE;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;

        return switch (dbData) {
            case TRUE_VALUE -> Boolean.TRUE;
            case FALSE_VALUE -> Boolean.FALSE;
            default -> throw new IllegalArgumentException(UNEXPECTED_VALUE_MESSAGE + dbData);
        };
    }
}
