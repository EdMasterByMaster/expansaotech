# Expansão Tech

Aplicação web da Associação Expansão Cultural: portal público, painel administrativo e API REST em Java/Spring Boot, com Microsoft SQL Server. As páginas são renderizadas no servidor com Thymeleaf e usam HTML/CSS sem JavaScript.

## Funcionalidades

- Portal público com cursos e eventos publicados.
- Login administrativo; senhas armazenadas com BCrypt e sem credenciais padrão.
- Recuperação de senha por link de uso único enviado ao e-mail cadastrado.
- Painel para cadastrar, consultar, editar e excluir ONGs, usuários, professores, cursos e eventos.
- API REST JSON para as mesmas entidades e regras, com validação e autenticação HTTP Basic para operações administrativas.
- Schema SQL Server baseado nas entidades e relações do modelo fornecido: `Ong`, `Usuario`, `Professor`, `Curso` e `Evento`.
- Interface responsiva que reaproveita a identidade visual e as imagens existentes.

O modelo fornecido não inclui inscrições, capacidade de eventos, pagamentos ou e-commerce; esses recursos não fazem parte deste escopo. A landing page da raiz continua disponível para GitHub Pages como versão estática; o backend Spring precisa ser executado em um host Java/Docker e não pode ser servido pelo GitHub Pages.

## Executar a aplicação completa com Docker

Requisitos: Docker Desktop com Docker Compose.

1. Copie `.env.example` para `.env`.
2. Gere e configure uma senha forte para `MSSQL_SA_PASSWORD` (SQL Server exige senha complexa). Para criar o primeiro administrador, configure também `APP_ADMIN_EMAIL`, `APP_ADMIN_PASSWORD` (mínimo de 12 caracteres), `APP_ADMIN_CPF` e, opcionalmente, `APP_ADMIN_NAME`. As três variáveis do administrador são necessárias em conjunto. Sem elas, o app inicia sem usuário administrativo inicial.
3. Para habilitar recuperação de senha, configure as variáveis SMTP descritas na seção **Recuperação de senha**.
4. Execute `docker compose up --build`.
5. Acesse <http://localhost:8080>; o portal público fica em `/`, e a administração em `/login`.

O Compose inicia o SQL Server, aguarda a disponibilidade do banco, aplica `sql/schema.sql` quando as tabelas ainda não existem e inicia a aplicação. O volume `expansaodb-data` preserva os dados entre reinicializações. Para encerrar, use `docker compose down`; para apagar os dados, remova explicitamente o volume `expansaodb-data`.

Não reutilize a senha de exemplo nem compartilhe o `.env`. O arquivo já está no `.gitignore`.

## Executar localmente com Maven

Requisitos: JDK 25 LTS, Maven 3.9+ e SQL Server (local ou pelo serviço `db` do Compose).

```powershell
Copy-Item .env.example .env
docker compose up -d db schema
```

Configure as variáveis de ambiente no terminal e inicie a aplicação:

```powershell
$env:DB_PASSWORD = "sua-senha-do-sql-server"
$env:APP_ADMIN_EMAIL = "admin@exemplo.org"
$env:APP_ADMIN_PASSWORD = "defina-uma-senha-forte-com-12-caracteres"
$env:APP_ADMIN_CPF = "informe-um-cpf-cadastrado"
$env:MAIL_HOST = "smtp.seuprovedor.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "usuario-smtp"
$env:MAIL_PASSWORD = "senha-de-aplicativo"
$env:MAIL_FROM = "nao-responda@exemplo.org"
$env:MAIL_AUTH = "true"
$env:MAIL_STARTTLS_ENABLED = "true"
$env:APP_PUBLIC_BASE_URL = "http://localhost:8080"
mvn spring-boot:run
```

Por padrão, a aplicação conecta a `localhost:1433/Expansaodb`. É possível substituir a conexão com `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. `spring.jpa.hibernate.ddl-auto=validate` faz a aplicação falhar explicitamente se o schema não corresponder; a aplicação não altera o banco implicitamente.

## SQL Server e atualização do modelo

- Instalação nova: o Compose executa `sql/schema.sql`; também é possível aplicá-lo manualmente com `sqlcmd`.
- Banco já criado com o DDL original informado: faça backup e aplique uma única vez `sql/migration-original-model-to-unicode.sql` antes de iniciar a nova versão. O script amplia campos textuais para Unicode e converte datas para `DATETIME2`, preservando os registros e relacionamentos existentes. No Windows, com o serviço `db` iniciado, execute `./sql/migrate-original-model.ps1`.
- Banco já atualizado para a versão anterior da aplicação: aplique `sql/migration-add-password-reset-tokens.sql` antes de iniciar esta versão, para criar a tabela de tokens de redefinição. No PowerShell, com `DB_PASSWORD` configurada, execute `sqlcmd -S localhost,1433 -U sa -P "$env:DB_PASSWORD" -C -i .\sql\migration-add-password-reset-tokens.sql`.
- O schema preserva as chaves e exclusões em cascata do modelo para ONG/eventos/cursos e usuário/professor; a exclusão de professor referenciado por curso é bloqueada.
- As datas de cadastro/criação usam UTC. Configure datas de eventos no horário local pretendido pela ONG.

## Recuperação de senha

Na tela `/login`, o usuário pode acessar **Esqueci minha senha**. A solicitação sempre mostra a mesma confirmação, exista ou não uma conta para o e-mail informado. O link expira em 30 minutos, só pode ser usado uma vez e o banco armazena apenas o hash do token. A nova senha deve ter ao menos 12 caracteres, respeitar o limite de 72 bytes do BCrypt e é gravada com BCrypt.

O envio real depende de um servidor SMTP. Configure `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_AUTH` e `MAIL_STARTTLS_ENABLED` no `.env`; configure `APP_PUBLIC_BASE_URL` com a URL pública usada nos links enviados. Os valores de exemplo apontam para `localhost:1025` e não configuram um servidor de e-mail. Sem SMTP disponível, o sistema mantém a resposta genérica e registra a falha no log do servidor; nenhum token é exposto na página ou no log.

Em produção, use SMTP autenticado e HTTPS em `APP_PUBLIC_BASE_URL`. Ao executar em Docker, `MAIL_HOST` deve ser acessível de dentro do container; `localhost` aponta para o próprio container, não para o computador host.

## API REST

Base recomendada: `/api/dados`. O status da API responde em `GET /api/dados`. Para seguir o padrão de apresentação do projeto de referência, a API inclui Swagger UI em <http://localhost:8080/swagger-ui/index.html> e a especificação OpenAPI em <http://localhost:8080/v3/api-docs>. Nas operações protegidas, use **Authorize** na interface Swagger e informe a conta administrativa.

Os catálogos `GET /api/dados/cursos` e `GET /api/dados/eventos` são públicos. Demais operações exigem um usuário `Administracao` via HTTP Basic; em produção, use somente HTTPS. As rotas anteriores sob `/api` continuam como aliases de compatibilidade.

| Recurso | Operações |
|---|---|
| `/api/dados/ongs` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/api/dados/usuarios` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/api/dados/professores` | `GET` (professores são mantidos pelo cadastro de usuário do tipo `Professor`) |
| `/api/dados/cursos` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/api/dados/eventos` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |

Exemplo de criação de evento:

```json
{
  "ongId": 1,
  "titulo": "Encontro comunitário",
  "descricao": "Atividade aberta à comunidade",
  "dataHora": "2026-12-10T14:00:00",
  "localEvento": "Jardim São Luís",
  "fotoUrl": ""
}
```

`UsuarioInput` recebe `cpf`, `nome`, `email`, `senha`, `tipo`, `fotoUrl` e `especialidade` (para professor). A resposta nunca inclui senha/hash. Tipos aceitos correspondem aos valores do schema: `Funcionario`, `Administracao`, `Colaborador`, `Voluntario`, `Professor`, `Doador` e `Aluno`. Erros de validação retornam HTTP 400, recurso inexistente 404 e conflito de integridade 409.

## Testes e build

```powershell
mvn test
mvn package
```

Os testes usam H2 isolado e cobrem renderização das páginas públicas, acesso anônimo à API, nomes dos tipos e a ausência de JavaScript nos templates. O workflow `.github/workflows/ci.yml` executa `mvn verify` com Java 25 LTS.

O roteiro com **oito processos da API para demonstração**, exemplos de requisição e a divisão sugerida dos cinco slides do PDF está em [`docs/apresentacao-api.md`](docs/apresentacao-api.md). O PDF é um guia de gestão de equipe, não uma especificação de oito processos do sistema; os oito fluxos foram organizados para apresentar os cadastros e relacionamentos implementados.

## Organização do projeto

- `src/main/java`: domínio, persistência, serviços, segurança, MVC e API.
- `src/main/resources/templates`: páginas Thymeleaf.
- `src/main/resources/static/css/app.css`: estilos da aplicação.
- `docs/apresentacao-api.md`: roteiro da demonstração da API e conexão com o relatório de gestão.
- `sql/schema.sql`: schema SQL Server para instalação nova.
- `sql/migration-original-model-to-unicode.sql`: atualização preservando dados do DDL original.
- `compose.yaml` e `Dockerfile`: execução local containerizada.
- [GitHub Project — Expansaotech - Plataforma Web](https://github.com/users/EdMasterByMaster/projects/2): cards de acompanhamento.
