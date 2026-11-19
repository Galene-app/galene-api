# Galene API

API REST do **Galene**, app que mede o ruído do ambiente, adapta sons de foco e conforto ao perfil de cada pessoa e gera relatórios diários de exposição.

Linguagem de bem-estar: o Galene não faz diagnóstico nem promete efeito terapêutico (Anvisa RDC 657/2022).

## Stack

- Java 25 · Spring Boot 4 · Spring Security (JWT + 2FA por e-mail) · Spring Data JPA (Hibernate 7)
- Oracle Database 19c (FIAP) · Maileroo (envio de e-mail via API HTTP) · springdoc (Swagger)

## Como rodar

1. **JDK 25 ou superior.**
2. Copie `.env.example` para `.env` e preencha:

   | Variável | Uso |
   |---|---|
   | `DB_USER`, `DB_PASSWORD` | Usuário do Oracle da FIAP |
   | `JWT_SECRET_TOKEN` | Segredo do JWT (mínimo de 32 bytes) |
   | `MAILEROO_API_KEY`, `MAILEROO_FROM_ADDRESS`, `MAILEROO_FROM_NAME` | Envio dos e-mails (2FA e senha temporária) |
   | `HAS_MFA` | `true` (padrão). `false` só é aceito nos perfis `dev`/`test` |
   | `SPRING_PROFILES_ACTIVE` | `dev` para avaliação/desenvolvimento, `prod` em produção |

3. Prepare o banco (seção abaixo).
4. Suba a API:

   ```bash
   ./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
   ```

5. Swagger: <http://localhost:7057/swagger-ui.html>

## Deploy (Docker / Railway)

O `Dockerfile` faz tudo: compila com Maven + JDK 25 e roda só com a JRE 25, com usuário sem privilégios.
O banco continua sendo o Oracle da FIAP. O `.env` **não** entra na imagem: os segredos são variáveis do ambiente.

**Railway:** conecte o repositório; o Railway detecta o `Dockerfile` e define a porta pela variável `PORT`.
Em *Variables*, cadastre:

| Variável | Obrigatória | Padrão na imagem |
|---|---|---|
| `DB_USER`, `DB_PASSWORD` | sim | — |
| `JWT_SECRET_TOKEN` | sim | — |
| `MAILEROO_API_KEY`, `MAILEROO_FROM_ADDRESS` | sim | — |
| `MAILEROO_FROM_NAME` | não | `Galene` |
| `SPRING_PROFILES_ACTIVE` | não | `prod` (use `dev` para liberar o `testMode` do relatório) |
| `HAS_MFA` | não | `true` |

**Local:**

```bash
docker build -t galene-api .
docker run --env-file .env -p 7057:7057 galene-api
```

## Banco de dados

Os scripts ficam em `../galene_sql/`, fora deste repositório. Ordem para um banco novo:

| # | Script | Conteúdo |
|---|---|---|
| 1 | `galene_ddl.sql` | Tabelas base |
| 2 | `galene_dml.sql` | Seed de exemplo (opcional) |
| 3 | `galene_ddl_mvp_incremental.sql` | Ajustes do MVP: termos, sessões, 2FA, questionário, sons etc. |
| 4 | `galene_dml_email_template.sql` | Templates de e-mail |
| 5 | `galene_dml_intensity_level.sql` | Faixas de ruído (Silencioso a Crítico) |
| 6 | `galene_dml_questionnaire.sql` | Questionário de perfil (8 perguntas) |
| 7 | `galene_dml_sound.sql` | Catálogo de sons (30 sons, 6 tipos) |

Os arquivos `galene_*migration*.sql` servem apenas para bancos que rodaram versões anteriores do script incremental.
A aplicação usa `ddl-auto=validate`: ela não cria tabelas, só confere se o banco está igual às entidades.

## Perfis

| Perfil | Diferenças |
|---|---|
| `dev` | Log em DEBUG; `HAS_MFA=false` permitido; **modo de teste do relatório ligado** (`?testMode=true`) |
| `prod` | 2FA obrigatório; modo de teste desligado (ligá-lo impede a aplicação de subir) |

## Endpoints (`/api/v1`)

Respostas de sucesso no envelope `{ statusCode, message, valid, content }`; erros no formato `{ code, message, details }`.

| Recurso | Endpoints | Acesso |
|---|---|---|
| Autenticação | `POST /auth/register` · `POST /auth/login` · `POST /auth/2fa/verify` · `POST /auth/refresh` · `POST /auth/password/forgot` | Público |
| | `POST /auth/logout` | Token |
| Perfil | `GET` `PUT` `DELETE /users/me` · `PUT /users/me/password` · `GET /users/me/export` | Token |
| Termos e consentimentos | `GET /terms` | Público |
| | `GET` `POST /users/me/consents` · `DELETE /users/me/consents/{termType}` · `PUT /users/me/permissions` | Token |
| Questionário | `GET /questionnaire` · `GET` `PUT /users/me/questionnaire` · `GET /users/me/profile-sound` | Token + `DATA_USE` |
| Leituras de ruído | `GET /noise-levels` | Público |
| | `POST` `GET /users/me/noise-readings` | Token + `DATA_USE` |
| Sons | `GET /sounds` | Público |
| | `GET /users/me/sounds` | Token + `DATA_USE` |
| Relatórios | `GET /users/me/reports/daily?date=&testMode=` · `/weekly` e `/monthly` (Premium, fora do MVP) | Token + `DATA_USE` |
| Notificações | `GET /users/me/notifications` · `PATCH /users/me/notifications/{id}` · `PATCH /users/me/notifications` | Token |

## Roteiro rápido para avaliação (perfil `dev`)

1. `POST /auth/register` → cria a conta (maior de 18 anos, senha forte, aceite dos termos).
2. `POST /auth/login` → envia o código de 6 dígitos por e-mail (com `HAS_MFA=false`, devolve os tokens direto).
3. `POST /auth/2fa/verify` com o código → `accessToken` (5 min) e `refreshToken` (7 dias).
4. `POST /users/me/consents` com `{ "termTypes": ["DATA_USE", "LOCATION"] }` e `PUT /users/me/permissions`.
5. `GET /questionnaire` → `PUT /users/me/questionnaire` → `GET /users/me/profile-sound` e `GET /users/me/sounds`.
6. `POST /users/me/noise-readings` com algumas leituras de agora (uma de 85 dB ou mais gera alerta) → `GET /users/me/notifications`.
7. `GET /users/me/reports/daily?date=<hoje>&testMode=true` → relatório sem esperar as 24h de uso.
8. `GET /users/me/export` → todos os dados do usuário em JSON (LGPD).

## Regras principais

- **2FA:** código de 6 dígitos por e-mail, válido por 5 minutos e de tentativa única; a flag `HAS_MFA` desliga o 2FA só em `dev`/`test`.
- **Esqueci a senha:** envia uma senha temporária por e-mail; no login seguinte a API devolve `passwordChangeRequired=true`.
- **Leituras:** lote de até 500, classificadas pelo servidor nas faixas de `gln_intensity_level`; o lote inteiro é recusado se houver leitura inválida.
- **Relatório D-1:** tempo em cada nível (teto de 60 s por leitura), média energética (Leq), pico e locais nos níveis Intenso e Crítico; disponível após 24h de uso. Um job às 00:30 registra a emissão e gera a notificação.
- **Sons:** recomendados pelo perfil do questionário; sons Premium não aparecem para o plano gratuito.
- **LGPD:** consentimentos por finalidade com histórico, exportação dos dados e exclusão da conta (anonimiza e apaga leituras, respostas, relatórios e notificações; mantém os consentimentos).
