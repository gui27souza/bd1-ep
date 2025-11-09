@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================

REM 1. Compila
javac -encoding UTF-8 -d . -cp lib\postgresql-42.7.8.jar src\main\java\**\*.java

REM Verifica se a compilação falhou (código de erro > 0)
if errorlevel 1 goto :EOF

REM 2. Executa
java -cp .:lib/postgresql-42.7.8.jar main.java.clientapp.Main

REM 3. limpa
rmdir /s /q main