@echo off
:: Se mueve a la carpeta del script.
cd /d %~dp0

:: Inicia Spring Boot.
start "" /B mvn clean spring-boot:run

:: Espera a que el servidor arranque.
timeout /t 10 /nobreak >nul

:: Abre localhost:8080 en el navegador predeterminado.
start "" "http://localhost:8080"
