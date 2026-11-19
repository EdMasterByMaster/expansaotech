package br.com.expansaotech.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CepService {
    private final RestClient client;

    public CepService() {
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(java.net.http.HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build());
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder()
                .baseUrl("https://viacep.com.br/ws")
                .requestFactory(requestFactory)
                .build();
    }

    public CepEndereco buscar(String cepInformado) {
        String cep = cepInformado == null ? "" : cepInformado.replaceAll("\\D", "");
        if (!cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("Informe um CEP com oito dígitos.");
        }
        JsonNode resposta = client.get()
                .uri("/{cep}/json", cep)
                .retrieve()
                .body(JsonNode.class);
        if (resposta == null || resposta.path("erro").asBoolean()) {
            throw new IllegalArgumentException("CEP não encontrado. Confira o número ou preencha o endereço manualmente.");
        }
        return new CepEndereco(
                resposta.path("cep").asText(),
                resposta.path("logradouro").asText(),
                resposta.path("complemento").asText());
    }
}
