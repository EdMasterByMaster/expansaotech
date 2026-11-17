package br.com.expansaotech.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UsuarioTipoConverter implements AttributeConverter<UsuarioTipo, String> {

    @Override
    public String convertToDatabaseColumn(UsuarioTipo tipo) {
        return tipo == null ? null : tipo.getValorBanco();
    }

    @Override
    public UsuarioTipo convertToEntityAttribute(String valor) {
        return valor == null ? null : UsuarioTipo.fromValorBanco(valor);
    }
}
