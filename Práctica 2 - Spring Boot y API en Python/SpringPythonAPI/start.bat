@echo off
REM ----------------------------------------------------------------
REM start.bat  —  Levanta Postgres en Docker, arranca Spring Boot y abre el navegador
REM ----------------------------------------------------------------

REM 1) Sitúate en la carpeta del script
cd /d %~dp0

REM 2) Levanta la base de datos con docker-compose
echo Levantando PostgreSQL en Docker...
docker-compose up -d db

echo Levantando Flask en Docker...
docker-compose up -d flask

REM 3) Espera un momento para que Postgres arranque (ajusta si es necesario)
echo Esperando a PostgreSQL...
timeout /t 5 /nobreak >nul

REM 4) Inicia Spring Boot en segundo plano
echo Iniciando Spring Boot...
start "" /B mvn clean spring-boot:run
if %errorlevel% neq 0 (
    echo Fallo al iniciar Spring Boot. Código de error %errorlevel%.
    goto end
)

REM 5) Espera unos segundos para que la app esté arriba
timeout /t 10 /nobreak >nul

REM 6) Abre el navegador en localhost:8080
start "" "http://localhost:8080"

:end