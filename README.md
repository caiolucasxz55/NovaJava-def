# Nova - Sistema de Gerenciamento de Metas e Habilidades

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![Oracle](https://img.shields.io/badge/Database-Oracle-red)
![License](https://img.shields.io/badge/License-MIT-blue)

## 📋 Sobre o Projeto

**Nova** é uma aplicação completa de gerenciamento de metas e desenvolvimento de habilidades, desenvolvida com Spring Boot. O sistema permite que usuários criem e acompanhem suas metas pessoais e profissionais, registrem habilidades, e interajam com um chatbot inteligente alimentado por IA (Perplexity AI) para obter orientações personalizadas.

### 🎯 Funcionalidades Principais

- **Gerenciamento de Usuários**: Cadastro, autenticação JWT e perfil de usuário
- **Sistema de Metas**: Criação, acompanhamento e categorização de metas com status
- **Desenvolvimento de Habilidades**: Registro e organização de habilidades por tipo
- **Chatbot com IA**: Assistente inteligente com contexto do usuário usando Perplexity AI
- **API RESTful**: Endpoints bem documentados com HATEOAS
- **Segurança**: Autenticação JWT com chaves RSA
- **Validação**: Validação completa de dados com Bean Validation
- **Paginação e Filtros**: Busca avançada com filtros dinâmicos e paginação

## 🌟 Diferenciais do Projeto

### 1. **Chatbot Inteligente com Contexto**
O chatbot utiliza a API Perplexity AI e possui contexto completo do usuário, incluindo:
- Histórico de conversas anteriores
- Metas cadastradas
- Habilidades registradas

Isso permite respostas personalizadas e orientações relevantes.

### 2. **HATEOAS Completo**
Todas as respostas da API incluem links HATEOAS, facilitando a navegação e descoberta da API:

```json
{
  "id": 1,
  "name": "João",
  "_links": {
    "self": {"href": "/api/users/1"},
    "goals": {"href": "/api/users/1/goals"},
    "skills": {"href": "/api/users/1/skills"}
  }
}
```

### 3. **Busca Avançada com Specifications**
Sistema de filtros dinâmicos usando JPA Specifications, permitindo queries complexas sem código adicional.

### 4. **Validação Robusta**
Validação completa em todas as camadas:
- Bean Validation nas entidades e DTOs
- Validação customizada nos services
- Tratamento de exceções centralizado

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas (Layered Architecture):

```
├── Controller Layer    → Recebe requisições HTTP
├── Service Layer       → Lógica de negócio
├── Repository Layer    → Acesso a dados (JPA)
└── Model Layer         → Entidades e DTOs
```

### 📦 Tecnologias Utilizadas

#### Core
- **Java 17**: Linguagem de programação
- **Spring Boot 3.3.5**: Framework principal
- **Maven**: Gerenciamento de dependências

#### Spring Ecosystem
- **Spring Data JPA**: Persistência de dados
- **Spring Security**: Autenticação e autorização
- **Spring HATEOAS**: Hypermedia REST API
- **Spring Validation**: Validação de dados
- **Spring OAuth2 Resource Server**: JWT com RSA

#### Database
- **Oracle Database**: Banco de dados principal
- **Hikari**: Pool de conexões

#### Documentação
- **SpringDoc OpenAPI 3**: Documentação Swagger/OpenAPI

#### Ferramentas
- **Lombok**: Redução de boilerplate
- **DotEnv**: Gerenciamento de variáveis de ambiente
- **Docker**: Containerização (Windows)

#### Integrações Externas
- **Perplexity AI API**: Chatbot inteligente

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+

### Passo 1: Clone o Repositório

```bash
git clone https://github.com/joaoGFG/NovaJava.git
cd NovaJava/nova
```

### Passo 2: Configure as Variáveis de Ambiente

Crie um arquivo `launch.json` na raiz do projeto `nova/` com as seguintes variáveis:

```{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Rodar Nova API",
            "request": "launch",
            "mainClass": "com.fiap.nova.NovaApplication",
            "projectName": "nova",
            "env": {
                "PERPLEXITY_API_KEY": "insira_a_key"
            }
        }
    ]
}
```

### Passo 3: Compile o Projeto

```bash
./mvnw clean install
```

Ou no Windows:
```powershell
.\mvnw.cmd clean install
```

### Passo 4: Execute a Aplicação

```bash
./mvnw spring-boot:run
```

Ou no Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### 🐳 Executar com Docker (Windows)

```bash
docker build -f Dockerfile.windows -t nova-app .
docker run -p 8080:8080 --env-file .env nova-app
```

## 📚 Documentação da API

### Swagger UI

Acesse a documentação interativa da API em:
```
http://localhost:8080/swagger-ui.html
```

### Endpoints Principais

#### Autenticação
- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/login` - Login e obtenção de token JWT

#### Usuários
- `GET /api/users` - Listar usuários (paginado e filtrado)
- `GET /api/users/{id}` - Buscar usuário por ID
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário

#### Metas
- `POST /api/goals` - Criar nova meta
- `GET /api/goals` - Listar metas (paginado e filtrado)
- `GET /api/goals/{id}` - Buscar meta por ID
- `PUT /api/goals/{id}` - Atualizar meta
- `DELETE /api/goals/{id}` - Deletar meta

#### Habilidades
- `POST /api/skills` - Criar nova habilidade
- `GET /api/skills` - Listar habilidades (paginado e filtrado)
- `GET /api/skills/{id}` - Buscar habilidade por ID
- `PUT /api/skills/{id}` - Atualizar habilidade
- `DELETE /api/skills/{id}` - Deletar habilidade

#### Chatbot
- `POST /api/chatbot` - Enviar mensagem para o chatbot

### Autenticação JWT

A API usa autenticação JWT com chaves RSA. Para acessar endpoints protegidos:

1. Faça login via `/api/auth/login`
2. Copie o token retornado
3. Adicione o header em todas as requisições:
```
Authorization: Bearer seu_token_jwt_aqui
```

### Exemplo de Requisição

#### Registro de Usuário
```json
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "Senha@123"
}
```

#### Criar Meta
```json
POST /api/goals
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Aprender Spring Boot",
  "description": "Dominar o framework Spring Boot",
  "targetDate": "2024-12-31",
  "category": "PROFESSIONAL",
  "status": "IN_PROGRESS"
}
```

### Filtros e Paginação

Todos os endpoints de listagem suportam:

**Paginação**:
- `page` - Número da página (default: 0)
- `size` - Tamanho da página (default: 10)
- `sort` - Campo de ordenação (ex: `name,asc`)

**Filtros**:
- Metas: `title`, `categoryId`, `statusId`
- Habilidades: `name`, `type`
- Usuários: `name`, `email`

Exemplo:
```
GET /api/goals?page=0&size=10&sort=title,asc&title=Spring&statusId=1
```

## 🗃️ Modelo de Dados

### Principais Entidades

#### User (Usuário)
```java
- id: Long
- name: String
- email: String (único)
- password: String (criptografado)
- createdAt: LocalDateTime
- goals: List<Goal> (many-to-many)
- skills: List<Skill> (many-to-many)
```

#### Goal (Meta)
```java
- id: Long
- title: String
- description: String
- targetDate: LocalDate
- category: GoalCategory
- status: GoalStatus
- createdAt: LocalDateTime
```

#### Skill (Habilidade)
```java
- id: Long
- name: String
- description: String
- type: SkillType (TECHNICAL, SOFT, LANGUAGE, OTHER)
- proficiency: String
- yearsOfExperience: Integer
```

#### AIInteraction (Interação com IA)
```java
- id: Long
- user: User
- userMessage: String
- aiResponse: String
- createdAt: LocalDateTime
```

### Relacionamentos

- **User ↔ Goal**: Many-to-Many (um usuário pode ter várias metas, uma meta pode ser compartilhada)
- **User ↔ Skill**: Many-to-Many (um usuário pode ter várias habilidades)
- **User → AIInteraction**: One-to-Many (um usuário tem várias interações)
- **Goal → GoalCategory**: Many-to-One
- **Goal → GoalStatus**: Many-to-One

## 🔒 Segurança

### Autenticação

- Sistema JWT com chaves RSA (2048 bits)
- Tokens com validade de 24 horas
- Senhas criptografadas com BCrypt
- Chaves armazenadas em `src/main/resources/certs/`

## 📁 Estrutura do Projeto

```
nova/
├── src/
│   ├── main/
│   │   ├── java/com/fiap/nova/
│   │   │   ├── config/           # Configurações (Security, Swagger, etc)
│   │   │   ├── controller/       # Controllers REST
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Tratamento de exceções
│   │   │   ├── filters/          # Filtros de busca
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositórios JPA
│   │   │   ├── service/          # Lógica de negócio
│   │   │   ├── specification/    # Specifications JPA
│   │   │   └── NovaApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── certs/            # Chaves RSA
│   └── test/                     
├── Dockerfile.windows            # Docker (no momento windows)
├── docker-compose.windows.yml
├── pom.xml
└── README.md
```

## 🛠️ Solução de Problemas

### Erro de Conexão com Banco de Dados

Verifique se:
1. As credenciais no properties estão corretas
2. O Oracle Database está acessível
3. A URL de conexão está correta

### Erro no Chatbot

Verifique se:
1. A `PERPLEXITY_API_KEY` está configurada no `launch.json`
2. A URL da API está correta

### Erro de Autenticação JWT

Verifique se:
1. As chaves RSA existem em `src/main/resources/certs/`
2. O token não expirou (24 horas)
3. O header Authorization está correto

## 👥 Equipe

- **joaoGFG**, **caiolucasxz55** e **madjerfin**

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos na FIAP.

## 📞 Contato

Para dúvidas ou sugestões, entre em contato através do GitHub.

---

⭐ Se este projeto foi útil para você, considere dar uma estrela no repositório!
