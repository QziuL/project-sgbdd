# API SGBDD - Estudo de Replicação e Load Balancing

Este projeto é um estudo acadêmico desenvolvido para a disciplina de Optativa em Banco de Dados II. O objetivo é demonstrar na prática o funcionamento de uma arquitetura de banco de dados distribuída com **Replicação Master-Slave** e balanceamento de carga com **HAProxy**, consumidos por uma API **Spring Boot**.

## 🏗 Arquitetura

O sistema é composto pelos seguintes componentes:

1.  **Spring Boot API**: Aplicação que gerencia a lógica de negócio (Entidade `Product`). Ela utiliza um roteamento dinâmico de DataSource (`RoutingDataSource`) para direcionar operações de escrita para o Master e operações de leitura para o Load Balancer.
2.  **HAProxy (Load Balancer)**: Atua como intermediário, expondo duas portas:
    *   `:5000` (Write): Redireciona exclusivamente para o nó **Master**.
    *   `:5001` (Read): Distribui as requisições (Round Robin) entre **Master** e **Slave**.
    *   `:8404` (Stats): Painel visual de monitoramento da saúde dos nós.
3.  **PostgreSQL Master**: Nó primário que aceita escritas e replica dados para o Slave.
4.  **PostgreSQL Slave**: Nó secundário (Replica), "read-only", sincronizado com o Master.

## 🚀 Tecnologias Utilizadas

*   **Java 17** + **Spring Boot 3**
*   **Spring Data JPA** (Hibernate)
*   **PostgreSQL** (Imagem Bitnami com suporte a replicação)
*   **HAProxy**
*   **Docker** & **Docker Compose**

## ⚙️ Pré-requisitos

*   Docker e Docker Compose instalados.
*   JDK 21 instalado (ou utilize o `mvnw` embutido).

## 🛠 Como Executar

### 1. Subir a Infraestrutura (Banco de Dados + HAProxy)

A infraestrutura está definida na pasta `config/`. Execute o comando abaixo na raiz do projeto:

```bash
docker compose up --build
```

Aguarde alguns instantes para que:
1.  O Master inicie.
2.  O Slave conecte e sincronize.
3.  O HAProxy realize os *Health Checks*.

Você pode verificar o status dos bancos acessando o painel do HAProxy:
🔗 [http://localhost:8404](http://localhost:8404)

### 2. Executar a Aplicação Spring Boot

Com a infraestrutura rodando, inicie a API:

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

A aplicação iniciará na porta `8080` (padrão).

## 🧪 Testando a Replicação

A API possui uma entidade `Product`.

### Criar um Produto (Escrita -> Master)
```bash
curl -X POST http://localhost:8080/products \
-H "Content-Type: application/json" \
-d '{"name": "Notebook", "price": 5000.00, "quantity": 10}'
```

### Listar Produtos (Leitura -> Balanceado entre Master/Slave)
Faça várias requisições consecutivas. Internamente, a aplicação conectará na porta `5001` do HAProxy, que alternará a entrega entre o Master e o Slave.

```bash
curl http://localhost:8080/products
```

## 📂 Estrutura de Pastas Relevante

```
Api_SGBDD/
├── config/
│   ├── docker-compose.yml  # Definição dos containers (Master, Slave, HAProxy)
│   └── haproxy.cfg         # Regras de balanceamento e Health Checks
├── src/main/java/.../config/
│   ├── RoutingDataSource.java  # Lógica para trocar entre Master/Slave dinamicamente
│   └── DataSourceConfig.java   # Configuração dos Beans do HikariCP
└── src/main/resources/
    └── application.properties  # Conexão JDBC apontando para as portas do HAProxy
```

## 📝 Notas de Configuração

*   **DDL Auto**: A aplicação está configurada com `spring.jpa.hibernate.ddl-auto=update`, ou seja, a tabela `product` será criada automaticamente no Master e replicada para o Slave.
*   **Health Checks**: O HAProxy utiliza `pgsql-check` para garantir que só envia tráfego para bancos que estão prontos para aceitar queries.
