@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================

set SOURCEPATH=src\main\java
set CLASSPATH=lib\postgresql-42.7.8.jar

echo.
echo === 1. Compilando projeto... ===
echo.

mkdir bin 2>NUL
echo.

set SOURCE_FILES=
for /R "%SOURCEPATH%" %%f in (*.java) do (
    set SOURCE_FILES=!SOURCE_FILES! "%%f"
)

setlocal enabledelayedexpansion
javac -encoding UTF-8 -d bin -cp %CLASSPATH% -sourcepath %SOURCEPATH% %SOURCE_FILES%

if errorlevel 1 (
    echo.
    echo ERRO: Falha na compilação. Verifique as mensagens acima.
    pause
    endlocal
    exit /b 1
)

endlocal
echo.
echo Compilação concluída com sucesso!

echo.
echo === 2. Executando Aplicação... ===
echo.

java -cp bin;%CLASSPATH% clientapp.Main

if errorlevel 1 (
    echo.
    echo ❌ ERRO: Falha na execução da aplicação.
)

echo.
echo === 3. Limpando arquivos compilados... ===
echo.

rmdir /s /q bin

echo.
echo Operação finalizada.
pause