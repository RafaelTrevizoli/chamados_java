# Sistema de Chamados (Entrega 35%)

Backend Spring Boot + MySQL e páginas HTML/CSS/JS simples servidas via `/static`.

## Rodando localmente

1. **Pré-requisitos**:
   - Java 17+
   - Maven 3.9+
   - MySQL rodando com DB `sistema_chamados`
     ```sql
     CREATE DATABASE sistema_chamados CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
     ```
   - Ajuste `spring.datasource.username` e `spring.datasource.password` em `src/main/resources/application.properties`.

2. **Build & run**:
   ```bash
   mvn spring-boot:run
   ```

3. **Testar**:
   - Acesse `http://localhost:8080/` para ver as páginas estáticas.
   - Endpoints:
     - `POST /api/usuarios/cadastro`
     - `POST /api/usuarios/login`
     - `POST /api/chamados/criar`
     - `GET  /api/chamados/cliente/{id}`
     - `PUT  /api/chamados/{id}/status`

> Observação: Segurança simplificada para entrega. Em produção, use hashing de senha e autenticação com JWT/Spring Security.
