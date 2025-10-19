📋 API Project Manager
=======================
API RESTfull para gerenciamento de projetos, tarefas e usuários, construída com Spring Boot.

🚀 Tecnologias
-----------------

- Java 17+
- Spring Boot 3.x
- Spring Security - Autenticação e autorização
- Spring Data JPA - Persistência de dados
- JWT - Token de autenticação stateless
- PostgreSQL - Banco de dados relacional
- MapStruct - Mapeamento entre entidades e DTOs
- Testcontainers - Testes de integração isolados
- SpringDoc OpenAPI - Documentação automática da API
- Docker Compose - Orquestração de containers
- Maven - Gerenciamento de dependências

⚙️ Configuração
-----------------------

### Pré-requisitos

- JDK 17 ou superior
- Maven 3.8+
- Docker e Docker Compose instalados
- PostgreSQL (se não usar Docker)

### Variáveis de Ambiente

- As variáveis já estão configuradas para rodar o backend e o postgre com docker compose.
- Basta entrar na raiz do projeto e rodar o comando `docker compose up -d`.
    - digite o comando `docker logs -f backend` para ver os logs do backend rodando em docker
- Lembrando que o Docker precisa estar instalado e rodando na sua máquina.

### Executando com Docker Compose

```
# Clone o repositório
git clone git@github.com:rafamguedes/api-project-manager.git
cd api-project-manager

# Inicie os serviços
docker compose up -d

# A API estará disponível em http://localhost:8080
```

📚 Documentação da API
-----------------------

### Após iniciar a aplicação, acesse:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
