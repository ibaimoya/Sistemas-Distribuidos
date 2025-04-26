# Práctica 2 – Spring Boot + API Flask

### Autor  
- **Ibai Moya Aroz** ([ima1013@alu.ubu.es](mailto:ima1013@alu.ubu.es))

---

## Requisitos

- Tener instalados:
  - [Docker](https://docs.docker.com/get-docker/)  
  - [Docker Compose](https://docs.docker.com/compose/install/)  

---

## Instrucciones

1. Clona o sitúate en la carpeta raíz de la práctica 2:
```
cd Práctica\ 2\ -\ Spring\ Boot\ y\ API\ en\ Python/SpringPythonAPI/
```
2. Arranca todos los contenedores con Docker Compose:
```
docker-compose up --build
```
> [!IMPORTANT]  
> Docker Compose tiene que estar ya instalado en la máquina host.

Esto desplegará una web en el puerto **8080** y la API en el **5050**.

> [!IMPORTANT]  
> Asegúrate de que esos puertos estén libres en tu máquina.

3. Por último introduce la dirección de la web en el navegador:
```
http://localhost:8080
```

