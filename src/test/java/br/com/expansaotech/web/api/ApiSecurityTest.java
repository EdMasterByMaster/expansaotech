package br.com.expansaotech.web.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void catalogosRestSaoPublicos() throws Exception {
        mockMvc.perform(get("/api/cursos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/eventos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/dados/cursos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/dados/eventos")).andExpect(status().isOk());
    }

    @Test
    void statusDaApiEOpenApiEstaoDisponiveisSemLogin() throws Exception {
        mockMvc.perform(get("/api/dados"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"Rodando perfeitamente!\"")));
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Expansão Tech API")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/dados/cursos")));
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void operacoesRestAdministrativasExigemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/dados/usuarios")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/eventos").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void portalPublicoELoginRenderizam() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/cursos")).andExpect(status().isOk());
        mockMvc.perform(get("/eventos")).andExpect(status().isOk());
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crudRestProtegidoRetornaDadosSemSenha() throws Exception {
        String ong = mockMvc.perform(post("/api/dados/ongs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cnpj":"12.345.678/0001-90","nome":"ONG São Luís","email":"contato@ong.org"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/dados/ongs/")))
                .andReturn().getResponse().getContentAsString();
        int ongId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(ong).get("id").asInt();

        String usuario = mockMvc.perform(post("/api/dados/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"123.456.789-00","nome":"Ana Professora","email":"ana@ong.org",
                                 "senha":"uma-senha-forte-123","tipo":"Professor","especialidade":"Capoeira"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/dados/usuarios/")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"tipo\":\"Professor\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("senha"))))
                .andReturn().getResponse().getContentAsString();
        int professorId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(usuario).get("id").asInt();

        mockMvc.perform(get("/api/dados/professores"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ana Professora")));

        String curso = mockMvc.perform(post("/api/dados/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ongId":%d,"professorId":%d,"titulo":"Capoeira","descricao":"Aula comunitária",
                                 "horario":"Segundas, 18h","localCurso":"Jardim São Luís"}
                                """.formatted(ongId, professorId)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ana Professora")))
                .andReturn().getResponse().getContentAsString();
        int cursoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(curso).get("id").asInt();
        mockMvc.perform(get("/api/dados/cursos/{id}", cursoId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"titulo\":\"Capoeira\"")));
        mockMvc.perform(get("/cursos"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ana Professora")));

        String evento = mockMvc.perform(post("/api/dados/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ongId":%d,"titulo":"Festa da comunidade","descricao":"Entrada livre",
                                 "dataHora":"2026-12-10T14:00:00","localEvento":"Praça central"}
                                """.formatted(ongId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/dados/eventos/")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"ongNome\":\"ONG São Luís\"")))
                .andReturn().getResponse().getContentAsString();
        int eventoId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(evento).get("id").asInt();
        mockMvc.perform(get("/api/dados/eventos"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Festa da comunidade")));
        mockMvc.perform(get("/api/dados/eventos/{id}", eventoId))
                .andExpect(status().isOk());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/dados/eventos/{id}", eventoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ongId":%d,"titulo":"Festa atualizada","descricao":"Entrada livre",
                                 "dataHora":"2026-12-11T14:00:00","localEvento":"Praça central"}
                                """.formatted(ongId)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Festa atualizada")));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/dados/eventos/{id}", eventoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/dados/cursos/{id}", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ongId":%d,"professorId":%d,"titulo":"Capoeira atualizada","descricao":"Aula comunitária",
                                 "horario":"Segundas, 18h","localCurso":"Jardim São Luís"}
                                """.formatted(ongId, professorId)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Capoeira atualizada")));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/dados/cursos/{id}", cursoId))
                .andExpect(status().isNoContent());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/dados/usuarios/{id}", professorId))
                .andExpect(status().isNoContent());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/dados/ongs/{id}", ongId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void painelAdministrativoExigeERenderizaAcessoPrivilegiado() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/usuarios")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/ongs/nova")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/usuarios/novo")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/cursos/novo")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/eventos/novo")).andExpect(status().isOk());
        mockMvc.perform(get("/admin/ongs/999/editar"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ONG não encontrada")));
    }
}
