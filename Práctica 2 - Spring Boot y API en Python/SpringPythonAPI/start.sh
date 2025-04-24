#!/usr/bin/env bash
# ----------------------------------------------------------------
# start.sh  —  Levanta Postgres en Docker, arranca Spring Boot y abre el navegador
# ----------------------------------------------------------------

echo "\e[33m[!] ADVERTENCIA: El proyecto solo se ha probado en Windows, por lo que puede no funcionar en otros sistemas operativos.\e[0m"

# 1) Sitúate en la carpeta del script
cd "$(dirname "$0")"

# 2) Levanta la base de datos con docker-compose
echo "[*] Levantando PostgreSQL en Docker..."
docker-compose up -d db

# 3) Espera a que PostgreSQL esté listo
echo "[*] Esperando a que Postgres responda..."
until docker exec "$(docker-compose ps -q db)" pg_isready -U miusuario > /dev/null 2>&1; do
  sleep 1
done
echo "[*] PostgreSQL lista."

# 4) Arranca Spring Boot en segundo plano
echo "[*] Iniciando Spring Boot..."
mvn clean spring-boot:run &
APP_PID=$!

# 5) Espera unos segundos para que la aplicación se levante
sleep 10

# 6) Abre el navegador en localhost:8080
if command -v xdg-open >/dev/null; then
  xdg-open http://localhost:8080
elif command -v open >/dev/null; then
  open http://localhost:8080
else
  echo -e "\e[33m[*]\e[0m \e[36mPor favor abre manualmente \e[32mhttp://localhost:8080\e[36m en tu navegador.\e[0m"
fi

# 7) Mantén el proceso de Maven en primer plano
wait $APP_PID