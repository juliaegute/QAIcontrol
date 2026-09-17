# Projeto IV - QSOFT - Versão Corrigida

Exemplo didático com:

- **Spring Boot / Java 17**: domínio de Empresa e Consumo de IA;
- **PostgreSQL**: persistência;
- **Node.js Alert Service**: microserviço independente para avaliação de consumo;
- **Node.js Front/BFF**: serve a interface, atua como proxy e possui endpoint agregado de dashboard;
- **Docker Compose**: sobe todo o ambiente.

## Arquitetura

```text
Browser :3000
   |
   v
Node.js Front/BFF :3000
   |
   v
Spring Boot :8080 ------> Node Alert Service :3001
   |
   v
PostgreSQL :5432
```

## Requisito Empresa

A Empresa é requisito explícito do domínio. Cada empresa possui um UUID e um consumo de IA só pode ser registrado para uma empresa existente.

Fluxo:

1. `POST /api/empresa` cria a empresa;
2. o Java gera um `UUID` para a empresa;
3. `POST /api/usages` recebe `empresaId`;
4. o Spring valida se a empresa existe;
5. persiste o consumo;
6. chama o Node Alert Service;
7. devolve consumo + alerta ao BFF/front.

## Subir pelo Docker

Na pasta raiz:

```bash
docker compose down -v
docker compose build --no-cache
docker compose up
```

Acessos:

- Front/BFF: http://localhost:3000
- Spring Boot: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- Alert Service health: http://localhost:3001/health
- pgAdmin: http://localhost:5050

pgAdmin:

- e-mail: `admin@admin.com`
- senha: `admin`
- host do PostgreSQL dentro do Docker: `postgres`
- porta: `5432`
- database: `iadb`
- usuário: `ia`
- senha: `ia`

## Teste manual da API

Criar empresa:

```bash
curl -X POST http://localhost:8080/api/empresa \
  -H "Content-Type: application/json" \
  -d '{"name":"ACCamargo","area":"Saude"}'
```

Copie o `id` retornado e use como `empresaId`:

```bash
curl -X POST http://localhost:8080/api/usages \
  -H "Content-Type: application/json" \
  -d '{"empresaId":"COLE-O-UUID-AQUI","tokens":6000,"model":"gpt-demo"}'
```

Com 6000 tokens, o serviço Node deverá responder com alerta `WARNING`.

## BFF

O BFF possui dois papéis:

1. **Proxy** em `/api/*`, evitando que o browser conheça diretamente o endereço do Spring;
2. **Agregação específica da tela** em `GET /bff/dashboard`, combinando empresas e consumos.

Assim, o exemplo não é apenas um reverse proxy: há uma operação genuinamente orientada ao frontend.
