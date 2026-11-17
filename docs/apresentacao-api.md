# Roteiro da apresentação: Expansão Tech API

## Importante sobre o PDF

O PDF anexado é uma proposta de apresentação de **Gestão de Equipes**. Ele organiza a apresentação em cinco slides, contempla as seções 4.1 a 4.4 e sugere divisão entre duas pessoas; não especifica oito fluxos de negócio para a aplicação. Para deixar a entrega do sistema demonstrável, este roteiro define oito processos da API, aproveitando o padrão do projeto de referência (`AppController`, DTOs, Maven e Swagger) e mantendo persistência real no SQL Server em vez da lista em memória usada no exemplo do café.

## Oito processos da aplicação para demonstrar

Use <http://localhost:8080/swagger-ui/index.html>. No botão **Authorize**, informe o e-mail e a senha da conta `Administracao`. O Swagger permite executar as chamadas e ver parâmetros, exemplos, respostas e erros. As consultas públicas de cursos e eventos não precisam de autenticação.

1. **Verificar se a API está ativa.** Execute `GET /api/dados`. A resposta confirma o status do serviço e inclui aplicação e horário UTC da verificação. A rota anterior `GET /api` continua disponível como compatibilidade.
2. **Cadastrar a organização.** Como administrador, execute `POST /api/dados/ongs` com CNPJ, nome e e-mail. Guarde o campo `id` da resposta; os cursos e eventos precisam estar associados a uma ONG cadastrada.
3. **Consultar a organização cadastrada.** Execute `GET /api/dados/ongs` para listar e `GET /api/dados/ongs/{id}` para consultar o registro específico.
4. **Cadastrar um professor.** Execute `POST /api/dados/usuarios` com CPF, nome, e-mail, senha, `tipo: "Professor"` e `especialidade`. A aplicação cria o usuário e seu registro correspondente em `Professor` em uma única operação lógica.
5. **Consultar professores.** Execute `GET /api/dados/professores`; a resposta contém o ID usado por cursos, nome e especialidade. `GET /api/dados/usuarios` permite conferir os demais usuários, sem retornar senha ou hash.
6. **Cadastrar e consultar um curso.** Execute `POST /api/dados/cursos` usando o ID da ONG e o ID do professor. Depois confira o curso em `GET /api/dados/cursos` e `GET /api/dados/cursos/{id}`; o catálogo público também está em `GET /api/dados/cursos`.
7. **Atualizar e remover um curso.** Execute `PUT /api/dados/cursos/{id}` para alterar seus campos. Para demonstrar a remoção, use `DELETE /api/dados/cursos/{id}`. Cursos de professores que já ministram cursos não podem permanecer órfãos: primeiro exclua ou reassocie os cursos antes de excluir o professor.
8. **Cadastrar, consultar e manter um evento.** Execute `POST /api/dados/eventos` associado à ONG; confira em `GET /api/dados/eventos` e `GET /api/dados/eventos/{id}`. Execute `PUT /api/dados/eventos/{id}` para editar e, se estiver usando um registro descartável, `DELETE /api/dados/eventos/{id}` para excluir. A agenda pública está disponível em `GET /api/dados/eventos` e na página `/eventos`.

Todos os recursos também têm operações de listagem, consulta por ID, cadastro, atualização e exclusão: `ongs`, `usuarios`, `cursos` e `eventos`. Professores são criados e mantidos pelo cadastro de usuário do tipo `Professor`; a API expõe a consulta `GET /api/dados/professores`. O prefixo legado `/api` continua aceito para os mesmos recursos.

### Exemplos de corpos JSON

Cadastro de ONG:

```json
{
  "cnpj": "12.345.678/0001-90",
  "nome": "Associação Expansão Cultural",
  "email": "contato@exemplo.org",
  "telefone": "(11) 5555-0100",
  "fotoUrl": ""
}
```

Cadastro de professor:

```json
{
  "cpf": "123.456.789-00",
  "nome": "Ana Professora",
  "email": "ana.professora@exemplo.org",
  "senha": "TroqueEstaSenha123!",
  "tipo": "Professor",
  "especialidade": "Capoeira",
  "fotoUrl": ""
}
```

Cadastro de curso:

```json
{
  "ongId": 1,
  "professorId": 2,
  "titulo": "Capoeira",
  "descricao": "Atividade cultural aberta à comunidade.",
  "horario": "Segundas, às 18h",
  "localCurso": "Jardim São Luís",
  "fotoUrl": ""
}
```

Cadastro de evento:

```json
{
  "ongId": 1,
  "titulo": "Encontro comunitário",
  "descricao": "Atividade aberta à comunidade.",
  "dataHora": "2026-12-10T14:00:00",
  "localEvento": "Jardim São Luís",
  "fotoUrl": ""
}
```

Os IDs dos exemplos são ilustrativos: use os valores retornados pelos cadastros realizados na demonstração. CPF, CNPJ e e-mail precisam ser únicos. A senha de usuário novo precisa ter pelo menos 12 caracteres e nunca é devolvida pela API.

## Como apresentar o Swagger e as camadas

- **Controller (`ApiController`)**: centraliza as rotas sob `/api/dados`, com o `@RestController`, `@RequestMapping`, `@Tag` e operações identificadas por `@Operation`, seguindo a organização de endpoints do exemplo acadêmico.
- **DTOs (`web/dto`)**: definem os corpos de entrada validados e as respostas sem campos sensíveis.
- **Services e repositories**: aplicam regras de negócio e acessam as entidades persistidas; não há dados de demonstração em uma lista estática que desaparece ao encerrar a aplicação.
- **SQL Server**: armazena os registros e mantém as relações ONG → cursos/eventos e usuário → professor → cursos.
- **Swagger UI**: `/swagger-ui/index.html`; a especificação OpenAPI em JSON fica em `/v3/api-docs`. O esquema HTTP Basic documenta quais operações exigem administrador.

## Conexão com a apresentação de Gestão de Equipes

O PDF propõe cinco slides, não oito: (1) capa e contexto do MVP; (2) governança Scrum/Kanban, com Ednilson como Scrum Master/Tech Lead e Natalia como PMO; (3) matriz de papéis e corresponsabilidade nas frentes de banco de dados, front-end, back-end e documentação; (4) habilidades hard e soft; (5) desafios de requisitos, carga de trabalho e qualidade, com suas soluções.

O banco, API, interface, documentação, testes e code review demonstráveis sustentam os slides 3 e 5. Para o slide 4, preencham a matriz com habilidades reais da equipe; o documento de referência não fornece a relação individual de competências. A demonstração dos oito processos da API pode acompanhar os slides sem confundir entregas técnicas com as seções do relatório de gestão.
