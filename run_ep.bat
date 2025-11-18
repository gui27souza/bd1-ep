@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================


set CLASSPATH=lib\postgresql-42.7.8.jar

echo.
echo === 1. Compilando projeto... ===
echo.

set SOURCEPATH=src\main\java
set CLASSPATH=lib\postgresql-42.7.8.jar

mkdir bin 2>NUL
echo.

for /R "%SOURCEPATH%" %%f in (*.java) do (
    javac -encoding UTF-8 -d bin -cp %CLASSPATH% -sourcepath %SOURCEPATH% "%%f"
)

if errorlevel 1 (
    echo.
    echo ❌ ERRO: Falha na compilação.
    pause
    exit /b 1
)

echo.
echo === 2. Executando Aplicação... ===
echo.

java -cp bin;%CLASSPATH% main.java.clientapp.Main

echo.
echo === 3. Limpando arquivos compilados... ===
echo.

rmdir /s /q bin

echo.
echo Operação finalizada.
pause