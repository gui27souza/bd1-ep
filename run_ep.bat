@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================

REM 1. Cria a pasta bin
if not exist bin mkdir bin

REM 2. Compila - Coloca o .class em 'bin' (-d bin) e usa o driver como classpath (-cp)
javac -encoding UTF-8 -d bin -cp lib\postgresql-42.7.8.jar src\main\java\EpApplication.java

REM Verifica se a compilação falhou (código de erro > 0)
if errorlevel 1 goto :EOF

REM 3. Executa - O classpath usa a pasta 'bin' e o driver
java -cp bin;lib\postgresql-42.7.8.jar main.java.EpApplication

REM 4. Remove a pasta bin e seu conteúdo
rmdir /s /q bin