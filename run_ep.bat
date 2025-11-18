@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================

REM Define o delimitador de caminho de classe para ponto e vírgula (Windows)
set CLASSPATH=lib\postgresql-42.7.8.jar

echo.
echo === 1. Compilando projeto... ===
echo.

mkdir bin 2>NUL
for /R "src\main\java" %%f in (*.java) do (
    javac -encoding UTF-8 -d bin -cp %CLASSPATH% "%%f"
)

REM Verifica se a compilação falhou (código de erro > 0)
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

REM Limpa o diretório 'bin' onde os arquivos .class foram salvos
rmdir /s /q bin

echo.
echo Operação finalizada.
pause