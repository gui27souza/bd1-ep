@echo off
REM =======================================================
REM SCRIPT DE EXECUÇÃO SIMPLES PARA WINDOWS (.BAT)
REM Compila, Executa e Limpa.
REM =======================================================


set SOURCEPATH=src

set CLASSPATH=lib\postgresql-42.7.8.jar

echo.
echo === 1. Compilando projeto... ===
echo.

mkdir bin 2>NUL
echo.

for /R "%SOURCEPATH%" %%f in (*.java) do (
    javac -encoding UTF-8 -d bin -cp %CLASSPATH% -sourcepath %SOURCEPATH% "%%f"
    if errorlevel 1 goto compilation_error
)

echo.
echo Compilação concluída com sucesso!

---
echo.
echo === 2. Executando Aplicação... ===
echo.

java -cp bin;%CLASSPATH% main.java.clientapp.Main

if errorlevel 1 (
    echo.
    echo ERRO: Falha na execução da aplicação.
)

---
echo.
echo === 3. Limpando arquivos compilados... ===
echo.

rmdir /s /q bin

echo.
echo Operação finalizada.
pause
goto :eof

:compilation_error
echo.
echo ERRO: Falha na compilação. Verifique as mensagens acima.
pause
exit /b 1