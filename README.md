# 💳 Nimble Pagamento – API Gateway de Pagamentos

Este projeto é uma API REST desenvolvida para simular um gateway de pagamentos simplificado, permitindo que usuários realizem operações como cadastro, autenticação, criação e gestão de cobranças, pagamentos entre contas, depósitos e cancelamentos.

## Sumário

- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Execução](#execução)
- [Documentação da API](#documentação-da-api)
- [Postman Collection](#postman-collection)
- [Estrutura das Entidades](#estrutura-das-entidades)
- [Regras de Negócio e Permissões](#regras-de-negócio-e-permissões)
- [Exemplos de Uso](#exemplos-de-uso)
- [Testes e Cobertura de Código](#testes-e-cobertura-de-código)
- [Observações](#observações)
- [Autor](#autor)


### Funcionalidades Principais

- **👤 Gerenciamento de Usuários:** Cadastro, autenticação JWT e controle de saldo
- **💰 Sistema de Cobranças:** Criação, consulta e gestão de cobranças entre usuários
- **💳 Processamento de Pagamentos:** Pagamento via saldo ou cartão de crédito
- **🔄 Cancelamento:** Cancelamento de cobranças com regras específicas
- **💵 Depósitos:** Adição de saldo via integração externa
- **🔐 Segurança:** Rate limiting, autenticação JWT e validações de negócio

## Requisitos

- [Git](https://git-scm.com/downloads)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Lombok](https://projectlombok.org/download)
- [Docker](https://docs.docker.com/engine/install/)
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- [Postman](https://www.postman.com/downloads/) (para testar a API)

## Instalação

### 1. Clone o repositório:

```bash
git clone https://github.com/VictorDevWakanda/Nimble-Pagamentos.git
cd nimble-pagamento
```

### 2. Inicie os containers Docker:

```bash
docker compose up --build -d
```

### 3. Compile:

```bash
mvn clean install
```

### 4. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

## Execução

A aplicação iniciará automaticamente com as seguintes configurações:

- **Porta:** 8080
- **Context Path:** `/nimble-pagamento/api`
- **Database:** PostgreSQL (via Docker)
- **Documentação:** Swagger UI disponível

## Documentação da API

### 📊 Health Check
**http://localhost:8080/nimble-pagamento/api/actuator/health**

### 🌐 Swagger UI
Acesse a documentação interativa da API em:
**http://localhost:8080/nimble-pagamento/api/swagger-ui.html**

### 🔗 API Base URL
**http://localhost:8080/nimble-pagamento/api**

## Postman Collection

Para facilitar os testes, foi criada uma collection completa do Postman com todos os endpoints da API.

### 📥 Como Usar

1. **Importe o arquivo** `nimbleCollection.json` no Postman
2. **Configure as variáveis de ambiente:**
   - `baseUrl`: http://localhost:8080/nimble-pagamento/api
   - `token`: Token JWT obtido após login
   - `userId`: UUID do usuário
   - `cobrancaId`: UUID da cobrança

### 🔐 Fluxo de Uso Recomendado

#### 1. Cadastro e Login
1. Execute "Cadastrar Usuário" para criar uma conta
2. Execute "Login" para obter o token JWT
3. Copie o token para a variável `{{token}}`

#### 2. Operações de Cobrança
1. Execute "Criar Cobrança" (precisa do token)
2. Use o ID da cobrança retornado na variável `{{cobrancaId}}`
3. Execute "Consultar Cobrança por ID"
4. Execute "Listar Cobranças Criadas" ou "Listar Cobranças Recebidas"

#### 3. Pagamentos
1. Execute "Pagar com Saldo" (destinatário)
2. Ou execute "Pagar com Cartão" (destinatário)

#### 4. Cancelamento
1. Execute "Cancelar Cobrança" (apenas originador)

#### 5. Depósito
1. Execute "Realizar Depósito" para adicionar saldo

### 🏷️ Organização da Collection

- 🔐 **Autenticação** - Login e cadastro
- 👤 **Usuário** - Gerenciamento completo de usuários
- 💰 **Cobrança** - CRUD de cobranças
- 💳 **Pagamento** - Processamento de pagamentos
- 🔄 **Cancelamento** - Cancelamento de cobranças
- 💵 **Depósito** - Adição de saldo
- 🏥 **Monitoramento** - Health checks e status

## 🚀 Endpoints Disponíveis

### 🔐 Autenticação
- `POST /usuario/autenticacao/login`

### 👤 Usuário
- `POST /usuario` (cadastro)
- `GET /usuario/{idUsuario}` (consulta por ID)
- `PATCH /usuario/{idUsuario}` (alteração)
- `DELETE /usuario/{idUsuario}` (exclusão)

### 💰 Cobrança
- `POST /cobrancas` (criar)
- `GET /cobrancas/consulta` (consultar por ID)
- `GET /cobrancas/originador` (listar do originador)
- `GET /cobrancas/destinatario` (listar do destinatário)

### 💳 Pagamento
- `POST /cobrancas/pagamento/{idCobranca}` (com saldo)
- `POST /cobrancas/pagamento-cartao/{idCobranca}` (com cartão)

### 🔄 Cancelamento
- `POST /cobrancas/{idCobranca}/cancelamento`

### 💵 Depósito
- `POST /cobrancas/deposito`

### 🏥 Monitoramento
- `GET /actuator/health` (health check)

## Estrutura das Entidades

### 👤 Usuário

| Campo | Tipo | Descrição | Validação |
|-------|------|-----------|-----------|
| `idUsuario` | UUID | Identificador único | Auto-gerado |
| `nome` | String | Nome completo | Obrigatório |
| `cpf` | String | CPF do usuário | Único, validado |
| `email` | String | Email do usuário | Único, validado |
| `senhaHash` | String | Senha criptografada | BCrypt |
| `saldo` | BigDecimal | Saldo da conta | ≥ 0.00 |

### 💰 Cobrança

| Campo | Tipo | Descrição | Validação |
|-------|------|-----------|-----------|
| `idCobranca` | UUID | Identificador único | Auto-gerado |
| `idUsuarioOriginador` | UUID | ID do usuário que criou | Obrigatório |
| `cpfDestinatario` | String | CPF do destinatário | Validado |
| `valorCobranca` | BigDecimal | Valor da cobrança | > 0.00 |
| `descricao` | String | Descrição da cobrança | ≤ 500 chars |
| `status` | Enum | Status atual | PENDENTE/PAGA/CANCELADA |
| `formaPagamento` | Enum | Forma de pagamento | SALDO/CARTAO |
| `dataExpiracao` | LocalDateTime | Data de expiração | Auto-definido |
| `dataPagamento` | LocalDateTime | Data do pagamento | Auto-definido |
| `dataCancelamento` | LocalDateTime | Data do cancelamento | Auto-definido |

## Regras de Negócio e Permissões

### 🔐 Autenticação
- Apenas usuários autenticados podem acessar endpoints protegidos
- Autenticação via CPF ou e-mail + senha
- Senhas armazenadas com BCrypt
- Tokens JWT com expiração configurável

### 💰 Cobranças
- Apenas o **originador** pode cancelar uma cobrança
- Apenas o **destinatário** pode pagar uma cobrança
- Cobranças expiram em 30 dias por padrão
- Valor mínimo: R$ 0,01

### 💳 Pagamentos
- **Saldo:** Débito direto da conta do pagador
- **Cartão:** Requer validação externa via API
- Pagamentos são irreversíveis (exceto cancelamentos autorizados)

### 🔄 Cancelamentos
- Apenas originador pode solicitar
- Cobranças pagas com cartão requerem autorização externa
- Cancelamentos geram estorno quando necessário

### 💵 Depósitos
- Todos os depósitos passam por validação externa
- Saldo atualizado automaticamente
- Histórico de transações mantido

## Exemplos de Uso

### 🔐 Autenticação

#### Login
```http
POST /usuario/autenticacao/login
Content-Type: application/json

{
  "cpfOuEmail": "12345678909",
  "senha": "senhaSegura"
}
```

#### Cadastro de Usuário
```http
POST /usuario
Content-Type: application/json

{
  "nome": "João Silva",
  "cpf": "12345678909",
  "email": "joao@email.com",
  "senha": "senhaSegura"
}
```

### 💰 Operações de Cobrança

#### Criar Cobrança
```http
POST /cobrancas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "cpfDestinatario": "11144477735",
  "valorCobranca": 150.00,
  "descricao": "Serviço de consultoria"
}
```

#### Consultar Cobrança por ID
```http
GET /cobrancas/consulta?idCobranca={{cobrancaId}}&idUsuarioSolicitante={{userId}}
Authorization: Bearer {{token}}
```

#### Listar Cobranças Criadas (Originador)
```http
GET /cobrancas/originador?IdusuarioOriginador={{userId}}&status=PENDENTE
Authorization: Bearer {{token}}
```

#### Listar Cobranças Recebidas (Destinatário)
```http
GET /cobrancas/destinatario?idUsuarioSolicitante={{userId}}&status=PAGA
Authorization: Bearer {{token}}
```

### 💳 Pagamentos

#### Pagamento com Saldo
```http
POST /cobrancas/pagamento/{{cobrancaId}}?idUsuarioPagador={{userId}}
Authorization: Bearer {{token}}
```

#### Pagamento com Cartão
```http
POST /cobrancas/pagamento-cartao/{{cobrancaId}}?idUsuarioPagador={{userId}}
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "numeroCartao": "4111111111111111",
  "validade": "12/28",
  "cvv": "123"
}
```

### 🔄 Cancelamento

#### Cancelar Cobrança
```http
POST /cobrancas/{{cobrancaId}}/cancelamento?idUsuarioSolicitante={{userId}}
Authorization: Bearer {{token}}
```

### 💵 Depósito

#### Realizar Depósito
```http
POST /cobrancas/deposito?idUsuario={{userId}}
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "valor": 500.00
}
```

## Observações

- ✅ **Spring Security:** Autenticação JWT implementada
- ✅ **Rate Limiting:** 5 requisições por minuto por IP
- ✅ **Validações:** CPF (formato correto), email, valores positivos
- ✅ **Integrações:** WebClient para autorizador externo
- ✅ **Docker:** Pronto para containerização
- ✅ **Clean Code:** SOLID, DDD e boas práticas
- ✅ **Documentação:** Swagger UI atualizada
- ✅ **Testes:** Testes unitários e de integração com cobertura JaCoCo
- ❌ **OBS:** Os testes não puderam ser concluidos pois faltou tempo para a conclusão!

## 📊 Testes e Cobertura de Código

O projeto utiliza **JaCoCo** (Java Code Coverage) para análise de cobertura de código nos testes automatizados.

### 🧪 Executando os Testes

Para executar os testes unitários e de integração e gerar o relatório de cobertura:

```bash
mvn clean test
```

### 📈 Relatório de Cobertura

Após a execução dos testes, o relatório de cobertura é gerado automaticamente e pode ser encontrado em:

**📁 Localização:** `target/site/jacoco/index.html`

### 🌐 Visualizando o Relatório

1. Execute o comando `mvn clean test`
2. Abra o arquivo `target/site/jacoco/index.html` no seu navegador
3. O relatório mostra:
   - **Cobertura por classe** (linhas, branches, complexidade)
   - **Cobertura geral do projeto**
   - **Linhas não cobertas** destacadas em vermelho
   - **Métricas detalhadas** por pacote e classe

### 🎯 Configuração do JaCoCo

O plugin JaCoCo está configurado no `pom.xml` com:

- **Versão:** 0.8.10
- **Relatório:** Gerado automaticamente na fase `test`
- **Formato:** HTML (acessível via navegador)
- **Integração:** Maven (executado com `mvn test`)

---

## Autor

**João Victor Mota**

- 💼 [LinkedIn](https://www.linkedin.com/in/jvnmdev/)
- 📧 victormota_03@hotmail.com

Sinta-se à vontade para entrar em contato para dúvidas, sugestões ou oportunidades!

---

## 📚 Links Úteis

- [📖 Swagger UI](#documentação-da-api)
- [📋 Postman Collection](#postman-collection)
- [🔧 Exemplos de Uso](#exemplos-de-uso)
- [🚀 Endpoints Disponíveis](#endpoints-disponíveis)
- [🏗️ Estrutura das Entidades](#estrutura-das-entidades)
- [📊 Testes e Cobertura](#testes-e-cobertura-de-código)

