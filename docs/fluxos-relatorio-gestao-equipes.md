# Fluxos mínimos do Relatório - Gestão de Equipes

Este roteiro relaciona os dez fluxos funcionais do relatório às telas, endpoints e dados persistidos no projeto. A aplicação mantém a arquitetura já definida: Spring Boot, SQL Server, API REST e páginas HTML/CSS renderizadas pelo servidor, sem JavaScript.

| # | Processo demonstrável | Caminho/tela | Regras e persistência |
|---|---|---|---|
| 1 | Login e bloqueio | `/login` | Autenticação por e-mail; cinco falhas bloqueiam a conta por 15 minutos. |
| 2 | Recuperação de senha | `/esqueci-senha` e `/redefinir-senha` | Token de uso único, armazenado como hash, válido por 30 minutos. |
| 3 | Cadastro e ativação | `/cadastro`, `/ativar-conta?token=...`; API `POST /api/contas` e `POST /api/contas/ativacao?token=...` | Cadastro de aluno, voluntário ou doador; conta fica desativada até validar o link enviado por e-mail. |
| 4 | Completar perfil | `/perfil`; API `PUT /api/contas/perfil` | CPF validado, CEP e endereço, número, complemento, nascimento, telefone, interesses e dias de disponibilidade. A tela permite consultar o ViaCEP sem JavaScript e também preencher o endereço manualmente se o serviço estiver indisponível. |
| 5 | Cadastro da ONG | `/admin/ongs` ou `POST /api/dados/ongs` | Razão social, nome fantasia, CNPJ com dígitos verificadores, CEP/endereço e contato. |
| 6 | Cadastro e ativação de colaborador | `/admin/usuarios` | Administração cria o usuário `Colaborador`; o acesso fica desabilitado até ativação pelo link de convite. A listagem permite reenviar o convite. |
| 7 | Cadastro de voluntário | `/cadastro` como `Voluntario`, depois `/perfil` | Registra contato, áreas de interesse e dias disponíveis nas tabelas de perfil do usuário. |
| 8 | Turmas de informática | `/admin/cursos` e catálogo `/cursos` | Período, carga horária, vagas, instrutor, dias/horários e local; inscrições respeitam publicação e capacidade. |
| 9 | Aulas de dança | `/admin/cursos` com modalidade `DANCA` | Regras de turma acrescidas da faixa etária; conflito do professor ou espaço é validado no período, dias e horário. |
| 10 | Ações/eventos pontuais | `/admin/eventos` e catálogo `/eventos` | Início/término, descrição, local, publicação e quantidade de voluntários; inscrições respeitam a capacidade definida. |

As telas administrativas exigem a função `ADMIN`. Inscrição em turma ou evento exige uma conta ativada, login e perfil completo. A mesma regra de participação está disponível na API por `POST /api/dados/cursos/{id}/inscricao` e `POST /api/dados/eventos/{id}/inscricao`; as demais operações administrativas da API continuam restritas a `Administracao`.

## Atualização do SQL Server

Para instalações novas, aplique `sql/schema.sql`. Em banco já existente, faça backup e aplique, nesta ordem, a migração de tokens de redefinição que corresponda à versão instalada e `sql/migration-report-requirements.sql`. O último script é idempotente para as colunas e tabelas adicionadas nesta fase; ele preserva os registros antigos e mantém contas/ONGs existentes habilitadas.

Ativação de contas e redefinição de senha precisam de SMTP configurado. Em ambiente local, use `APP_PUBLIC_BASE_URL=http://localhost:8080`; sem acesso SMTP funcional, o sistema informa explicitamente a falha ao administrador e não ativa uma conta pública.
