[LICENSE__BADGE]: https://img.shields.io/github/license/Fernanda-Kipper/Readme-Templates?style=for-the-badge
[JAVA_BADGE]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[POSTGRESQL_BADGE]: https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[MAVEN_BADGE]: https://img.shields.io/badge/maven-%23C71A36.svg?style=for-the-badge&logo=apache-maven&logoColor=white
[PRS_BADGE]: https://img.shields.io/badge/PRs-welcome-green?style=for-the-badge

<h1 align="center" style="font-weight: bold;">Agendador de Barbearia 💈</h1>

![license][LICENSE__BADGE]
![java][JAVA_BADGE]
![spring][SPRING_BADGE]
![postgresql][POSTGRESQL_BADGE]
![maven][MAVEN_BADGE]
![prs][PRS_BADGE]

<details open="open">
<summary>Tabela de Conteúdos</summary>
  
- [🚀 Começando](#começando)
  - [Pré-requisitos](#pré-requisitos)
  - [Clonando](#clonando)
  - [Variáveis de Ambiente](#variáveis-de-ambiente)
  - [Iniciando](#iniciando)
- [📍 Endpoints da API](#endpoints)
  - [Usuários](#usuários)
  - [Agendamentos](#agendamentos)
  - [Bloqueios](#bloqueios)
  
</details>

<p align="center">
  <b>API RESTful para gerenciamento de agendamentos em barbearias, com autenticação segura via OAuth2 e controle de acesso baseado em roles.</b>
</p>

---

<h2 id="começando">🚀 Começando</h2>

Aqui você encontra tudo o que precisa para executar o projeto localmente.

<h3>Pré-requisitos</h3>

Antes de começar, certifique-se de ter instalado:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [PostgreSQL 12+](https://www.postgresql.org/download/)
- [Git](https://git-scm.com/)

<h3>Clonando</h3>

Clone o repositório do projeto:

```bash
git clone https://github.com/EmanuelTaborda/barbershop-appointment-api.git
cd barbershop-appointment-api
```

<h3>Variáveis de Ambiente</h3>

Crie um arquivo `application.properties` na pasta `src/main/resources/` com as seguintes configurações:

```properties
# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/agendador_barbearia
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Aplicação
spring.application.name=agendador-barbearia
spring.profiles.active=dev
spring.jpa.open-in-view=false

# Segurança OAuth2
server.servlet.context-path=/api
server.port=8080
```

<h3>Iniciando</h3>

Execute os seguintes comandos:

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080/api`

---

<h2 id="endpoints">📍 Endpoints da API</h2>

<h3 id="usuários">👥 Usuários</h3>

| Método | Rota | Descrição |
|--------|------|-----------|
| <kbd>POST</kbd> | `/users/cliente` | Registrar novo cliente |
| <kbd>POST</kbd> | `/users/barbeiro` | Registrar novo barbeiro (apenas admin) |
| <kbd>GET</kbd> | `/users/me` | Obter dados do usuário autenticado |

#### POST /users/cliente

Registra um novo cliente na plataforma.

**REQUEST**
```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "userType": "ROLE_CLIENT"
}
```

#### POST /users/barbeiro

Registra um novo barbeiro na plataforma (requer permissão de admin).

**REQUEST**
```json
{
  "name": "Carlos Barbeiro",
  "email": "carlos@barbearia.com",
  "password": "senha123"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": 2,
  "name": "Carlos Barbeiro",
  "email": "carlos@barbearia.com",
  "userType": "ROLE_BARBER"
}
```

#### GET /users/me

Retorna os dados do usuário atualmente autenticado.

**RESPONSE** (200 OK)
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "userType": "ROLE_CLIENT"
}
```

---

<h3 id="agendamentos">📅 Agendamentos</h3>

| Método | Rota | Descrição |
|--------|------|-----------|
| <kbd>POST</kbd> | `/agendamento` | Agendar novo serviço |
| <kbd>GET</kbd> | `/agendamento/cliente/{id}` | Listar agendamentos do cliente |
| <kbd>GET</kbd> | `/agendamento/barbeiro/{id}` | Listar agendamentos do barbeiro por data |
| <kbd>PUT</kbd> | `/agendamento/{id}` | Atualizar agendamento |
| <kbd>DELETE</kbd> | `/agendamento/{id}` | Cancelar agendamento |

#### POST /agendamento

Criar um novo agendamento.

**REQUEST**
```json
{
  "clientId": 1,
  "barberId": 2,
  "appointmentDate": "2026-12-25",
  "appointmentTime": "10:30",
  "service": "Corte de Cabelo"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": 1,
  "clientId": 1,
  "barberId": 2,
  "appointmentDate": "2026-12-25",
  "appointmentTime": "10:30",
  "service": "Corte de Cabelo",
  "status": "CONFIRMADO"
}
```

#### GET /agendamento/cliente/{id}

Obter todos os agendamentos de um cliente.

**RESPONSE** (200 OK)
```json
[
  {
    "id": 1,
    "clientName": "João Silva",
    "barberName": "Carlos Barbeiro",
    "appointmentDate": "2026-12-25",
    "appointmentTime": "10:30",
    "service": "Corte de Cabelo"
  },
  {
    "id": 2,
    "clientName": "João Silva",
    "barberName": "Carlos Barbeiro",
    "appointmentDate": "2026-12-26",
    "appointmentTime": "14:00",
    "service": "Barba"
  }
]
```

#### GET /agendamento/barbeiro/{id}?date=2026-12-25

Obter agendamentos de um barbeiro em uma data específica (requer role ADMIN ou BARBER).

**RESPONSE** (200 OK)
```json
[
  {
    "id": 1,
    "clientName": "João Silva",
    "appointmentTime": "10:30",
    "service": "Corte de Cabelo"
  }
]
```

#### PUT /agendamento/{id}

Atualizar um agendamento existente.

**REQUEST**
```json
{
  "clientId": 1,
  "barberId": 2,
  "appointmentDate": "2026-12-26",
  "appointmentTime": "11:00",
  "service": "Corte + Barba"
}
```

**RESPONSE** (200 OK)
```json
{
  "id": 1,
  "clientId": 1,
  "barberId": 2,
  "appointmentDate": "2026-12-26",
  "appointmentTime": "11:00",
  "service": "Corte + Barba",
  "status": "CONFIRMADO"
}
```

#### DELETE /agendamento/{id}

Cancelar um agendamento.

**RESPONSE** (204 No Content)

---

<h3 id="bloqueios">🚫 Bloqueios</h3>

| Método | Rota | Descrição |
|--------|------|-----------|
| <kbd>POST</kbd> | `/bloqueio` | Bloquear horário (apenas admin/barbeiro) |
| <kbd>GET</kbd> | `/bloqueio/barbeiro/{id}` | Listar bloqueios do barbeiro |
| <kbd>DELETE</kbd> | `/bloqueio/{id}` | Remover bloqueio |

#### POST /bloqueio

Criar um bloqueio de horário (ex: pausa, folga, manutenção).

**REQUEST**
```json
{
  "barberId": 2,
  "blockDate": "2026-12-25",
  "blockStartTime": "12:00",
  "blockEndTime": "13:00",
  "reason": "Almoço"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": 1,
  "barberId": 2,
  "blockDate": "2026-12-25",
  "blockStartTime": "12:00",
  "blockEndTime": "13:00",
  "reason": "Almoço"
}
```

---

