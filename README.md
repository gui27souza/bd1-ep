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

- Garanta que o PostgreSQL está em execução e devidamente configurado (Ver seção de Setup Inicial abaixo)
- Na raíz do diretório foram criados 2 scripts para compilação e execução do programa de forma mais prática, um para Windows e outro para Linux, basta criar suas variáveis de ambiente de acordo com o seu sistema operacional.
```
# Windows
run_ep.bat

# Linux
./run_ep
```
<br>
- Além disso, o driver do JDBC, necessário para realizar a conexão do código Java com o Banco de Dados PostgreSQL, já está incluso no repositório.

## Setup inicial para execução do programa

- Primeiro, é necessário criar um banco de dados seguindo o modelo definido pelo grupo:
  - Crie um Banco no PostgreSQL, é importante guardar o endereço de execução, nome, usuário e senha do BD
  - Execute o script de DDL e DML para criar as tabelas e fazer alguns inserts, o .sql está localizado em src/resources/DDL_DML.sql
- Agora, substitua no script .sh (Linux) ou .bat (Windows) os dados guardados do PostgreSQL (endereço de execução, nome, usuário e senha do BD) nos locais indicados
- O Setup está pronto! Basta executar o script de execução com o PostgreSQL em execução!