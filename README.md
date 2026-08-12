[JAVA_BADGE]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[H2_BADGE]: https://img.shields.io/badge/H2%20Database-blue?style=for-the-badge&logo=database&logoColor=white
[POSTGRES_BADGE]: https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[MAVEN_BADGE]: https://img.shields.io/badge/maven-%23C71A36.svg?style=for-the-badge&logo=apache-maven&logoColor=white

<h1 align="center" style="font-weight: bold;">Agendador de Barbearia ✂️</h1>

![java][JAVA_BADGE]
![spring][SPRING_BADGE]
![h2][H2_BADGE]
![postgres][POSTGRES_BADGE]
![maven][MAVEN_BADGE]

<details open="open">
<summary>Tabela de Conteúdos</summary>
  
- [🚀 Começando](#começando)
  - [Pré-requisitos](#pré-requisitos)
  - [Clonando](#clonando)
  - [Iniciando](#iniciando)
- [📍 Endpoints da API](#endpoints)
-  - [Autenticação](#autenticação)
  - [Usuários](#usuários)
  - [Agendamentos](#agendamentos)
  - [Bloqueios](#bloqueios)
- [💾 Banco de Dados](#banco-de-dados)
  
</details>

<p align="center">
  <b>API RESTful para gerenciamento de agendamentos em barbearias, com autenticação segura via OAuth2 e controle de acesso baseado em roles. Pronta para testes com H2.</b>
</p>
<p align="center">
  <b>No momento a aplicação ainda não possui um front end desenvolvido, porém já está em desenvolvimento.</b>
</p>

---

<h2 id="começando">🚀 Começando</h2>

Aqui você encontra tudo o que precisa para executar o projeto localmente na sua IDE.

<h3>Pré-requisitos</h3>

Antes de começar, certifique-se de ter instalado:

- [Java 21+](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/)

<h3>Clonando</h3>

Clone o repositório do projeto:

```bash
git clone https://github.com/EmanuelTaborda/barbershop-appointment-api.git
cd barbershop-appointment-api
```

<h3>Iniciando</h3>

A aplicação já está pré-configurada com banco de dados **H2** para testes. Basta executar:

**Via Maven:**
```bash
mvn clean install
mvn spring-boot:run
```

**Via IDE:**
1. Abra o projeto em sua IDE
2. Navegue até a classe `AgendadorBarbeariaApplication.java`
3. Clique em **Run** ou pressione `Shift + F10` (IntelliJ) / `Ctrl + F11` (Eclipse)

A API estará disponível em: `http://localhost:8080`

**Console H2:**
Acesse o console do banco de dados em: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (deixar em branco)

---

<h2 id="banco-de-dados">💾 Banco de Dados</h2>

O projeto utiliza **H2 Database** em memória para testes e desenvolvimento local. Os dados são carregados automaticamente via script SQL.

**Características:**
- ✅ Banco em memória (sem instalação necessária)
- ✅ Dados de teste pré-carregados via `import.sql`
- ✅ Console web para visualizar tabelas
- ✅ Reinicia automaticamente a cada execução

Para acessar os dados, use o console H2 em: `http://localhost:8080/h2-console`

O projeto em produção usará Banco de Dados PostgreSQL, o H2 é somente para testes.

---

<h2 id="endpoints">📍 Endpoints da API</h2>

Para testar os endpoints, você pode usar o Postman ou outra ferramenta de sua preferência.

Para facilitar os testes, este repositório inclui os arquivos de **coleção** e **environment** do Postman prontos para 
importar: [`barbershop-collection.json`](./postman/barbershop-collection.json) e [`barbershop-environment.json`](./postman/barbershop-environment.json). 
Basta importá-los no Postman (**Import** → selecionar os dois arquivos) e escolher o environment importado no canto superior direito.

Além disso, o projeto já conta com um seed no banco de dados com alguns usuários pré-cadastrados, facilitando os testes sem precisar criar uma conta do zero.

A maioria dos endpoints exige autenticação. Por isso, faça primeiro a requisição de login (`POST /oauth2/token`) para obter o token — a coleção já possui um script que salva o token automaticamente na variável do environment, então as demais requisições já são autenticadas sem trabalho extra. Você pode usar um dos usuários do seed ou cadastrar um novo através do endpoint `/users/cliente`.

<h3 id="autenticação">🔐 Autenticação</h3>

| Método | Rota | Descrição |
|--------|------|-----------|
| <kbd>POST</kbd> | `/oauth2/token` | Autenticar usuário e obter token JWT |

#### POST /oauth2/token

Autentica um usuário e retorna um token JWT para acesso aos demais endpoints protegidos.

> ⚠️ Esta requisição usa `Content-Type: application/x-www-form-urlencoded`, **não** `application/json`.

Para testar, faça login com um usuário cadastrado por você, passando as credenciais no environment do Postman, ou use
o usuário cadastrado via seed e já configurado no environment. Após o login, a variável `token` já fica salva automaticamente para as próximas requisições.

**RESPONSE** (200 OK)
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "token_type": "Bearer",
  "expires_in": 86399
}
```


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
  "id": null,
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "(11) 99999-9999",
  "password": "abcdef"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": "Id gerado pelo Hibernate",
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "(11) 99999-9999",
  "password": "$2a$10$6voKIRT8ZimMnbJ2YlBFvuA0RwsBkC2IWqzvbo9GdMYW55/sVG5gK",
  "roles": [
    {
      "id": 1,
      "authority": "ROLE_CLIENT"
    }
  ]
}
```

#### POST /users/barbeiro

Registra um novo barbeiro na plataforma (requer permissão de admin).

**REQUEST**
```json
{
  "id": null,
  "name": "Carlos Barbeiro",
  "email": "carlos@example.com",
  "phone": "(11) 99999-9999",
  "password": "SenhaSegura123!"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": "id gerado pelo Hibernate",
  "name": "Carlos Barbeiro",
  "email": "carlos@example.com",
  "phone": "(11) 99999-9999",
  "password": "$2a$10$zR81RcSrnM08r.A8rkaBx.kTKqZ3IXGV7tbnCt2ZQmAWg2RqFuBR.",
  "roles": [
    {
      "id": 2,
      "authority": "ROLE_BARBER"
    }
  ]
}
```

#### GET /users/me

Retorna os dados do usuário atualmente autenticado.

**RESPONSE** (200 OK)
```json
{
  "id": "Id gerado pelo hibernate",
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "(11) 99999-9999",
  "roles": [
    "ROLE_CLIENT"
  ]
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
  "startTime": "2026-08-12T11:00:00",
  "services": ["CABELO", "BARBA"],
  "clientEmail": "emanuel@gmail.com",
  "barberEmail": "flavio@gmail.com"
}
```

**RESPONSE** (201 Created)
```json
{
  "startTime": "2026-08-12T11:00:00",
  "services": [
    "BARBA",
    "CABELO"
  ],
  "barberName": "Flavio",
  "status": "AGENDADO"
}
```

#### GET /agendamento/cliente/{id}

Obter todos os agendamentos de um cliente.

**RESPONSE** (200 OK)
```json
[
  {
    "id": 1,
    "services": [
      "BARBA",
      "CABELO"
    ],
    "status": "AGENDADO",
    "startTime": "2026-08-12T11:00:00",
    "barberName": "Flavio"
  }
]
```

#### GET /agendamento/barbeiro/{id}?date=2026-12-25

Obter agendamentos de um barbeiro em uma data específica (requer role ADMIN ou BARBER). O parâmetro 'date' é obrigatório.

**RESPONSE** (200 OK)
```json
[
  {
    "id": 1,
    "services": [
      "BARBA",
      "CABELO"
    ],
    "client": "Emanuel",
    "endTime": "2026-08-12T12:00:00",
    "startTime": "2026-08-12T11:00:00"
  }
]
```

#### PUT /agendamento/{id}

Atualizar um agendamento existente.

**REQUEST**
```json
{
  "startTime": "2026-08-13T16:20:00",
  "services": ["CABELO", "BARBA"],
  "clientEmail": "emanuel@gmail.com",
  "barberEmail": "higor@gmail.com"
}
```

**RESPONSE** (200 OK)
```json
{
  "startTime": "2026-08-13T16:20:00",
  "services": [
    "BARBA",
    "CABELO"
  ],
  "barberName": "Higor",
  "status": "AGENDADO"
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
  "id": null,
  "idBarber": 3,
  "startTime": "2026-08-14T10:00:00",
  "endTime": "2026-08-14T14:00:00"
}
```

**RESPONSE** (201 Created)
```json
{
  "id": "gerado pelo Hibernate",
  "idBarber": 3,
  "startTime": "2026-08-14T10:00:00",
  "endTime": "2026-08-14T14:00:00",
  "timeValid": true
}
```

---

