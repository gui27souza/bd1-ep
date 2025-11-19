# Exercício Programado - Banco de Dados 1 - 2025

UNIVERSIDADE DE SÃO PAULO - ESCOLA DE ARTES, CIÊNCIAS E HUMANIDADES
<br>
<br>
Guilherme Oliveira de Souza NºUSP: 1467138
<br>
Júlio César Cordeiro Batista NºUSP: 12684333
<br>
Kennedy Rohab Menezes da Silva NºUSP: 12683395

## Como executar?

Para executar a aplicação principal do cliente:
```bash
# Linux/Mac
./run_ep.sh

# Windows
run_ep.bat
```

**Observações:**
- Garanta que o PostgreSQL está em execução e devidamente configurado (Ver seção de Setup Inicial abaixo)
- O driver JDBC PostgreSQL já está incluso no repositório (`lib/postgresql-42.7.4.jar`)

## Setup inicial para execução do programa

1. **Criar o Banco de Dados:**
   - Crie um banco no PostgreSQL
   - Guarde as informações: endereço (host:porta), nome do BD, usuário e senha

2. **Executar o Script DDL/DML:**
   - Execute o script localizado em `src/main/resources/DDL_DML.sql`
   - Este script cria todas as tabelas e insere dados de teste realistas

3. **Configurar Variáveis de Ambiente:**
   - Edite o arquivo `run_ep.sh` (Linux/Mac) ou `.run_ep.bat` (Windows)
   - Substitua os valores das variáveis de ambiente:
     - linux/mac
       ```bash
       export EP_DB="127.0.0.1:5432"          # Host:Porta do PostgreSQL
       export EP_NAME_DB="ep_bd"               # Nome do banco de dados
       export EP_DB_USER="postgres"            # Usuário do banco
       export EP_DB_PASS="sua_senha"           # Senha do banco
       ```

     - Windows
       ```powershell
       set EP_DB=127.0.0.1:5432
       set EP_NAME_DB=ep_bd
       set EP_DB_USER=postgres
       set EP_DB_PASS=1234
       ```

4. **Executar:**
   - Com o PostgreSQL em execução, execute um dos scripts acima
   - A interface gráfica será iniciada automaticamente

##  Interface do Cliente (ClientApp)

- **Login/Cadastro:** Sistema de autenticação completo
- **Gerenciar Grupos:** Criar grupos, visualizar membros e transações
- **Transações:** Visualizar transações por grupo ou todas
- **Convites:** Enviar e receber convites para grupos
- **Cadastro:** Editar dados pessoais e trocar plano
- **Relatórios:** 6 relatórios SQL complexos com visualização em tabelas

## Tecnologias Utilizadas

- **Java 11+** com Swing para interface gráfica
- **PostgreSQL** como banco de dados
- **JDBC** para conexão com o banco
- **Metal Look and Feel** para consistência cross-platform