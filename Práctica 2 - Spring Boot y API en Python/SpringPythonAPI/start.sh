#!/usr/bin/env bash
# Se mueve a la carpeta del script
cd "$(dirname "$0")"

# Arranca Spring Boot con Maven en segundo plano
mvn clean spring-boot:run &

# Espera para que el servidor arranque
sleep 10

# Abre el navegador en localhost:8080
if command -v xdg-open >/dev/null; then
  xdg-open http://localhost:8080
elif command -v open >/dev/null; then
  open http://localhost:8080
else
  echo -e "\e[33m[*]\e[0m \e[36mPor favor abre manualmente \e[32mhttp://localhost:8080\e[36m en tu navegador.\e[0m"
fi

# Espera al proceso de Maven
wait