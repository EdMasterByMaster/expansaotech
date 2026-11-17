package br.com.expansaotech.web.api;

import java.time.OffsetDateTime;

public record ApiStatusResponse(String status, String aplicacao, OffsetDateTime verificadoEm) {
}
