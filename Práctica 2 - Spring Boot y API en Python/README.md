# Práctica 2 – Spring Boot + API Flask

### Autor  
- **Ibai Moya Aroz** ([ima1013@alu.ubu.es](mailto:ima1013@alu.ubu.es))

---

## Requisitos

| **Producto** | **Versión** | **Descarga** |
|:------:|:--------:|:------------:|
| Docker Desktop | 4.40.0+ | [link](https://www.docker.com/products/docker-desktop/) |
| Docker Compose | 2.27.0+ | [link](https://docs.docker.com/compose/install/#:~:text=Mac-,Windows,-Tip) |

> [!NOTE]  
> Para evitar problemas se recomienda que **Docker Desktop** esté abierto antes de comenzar la ejecución.

---

## Instrucciones

1. Clona o sitúate en la carpeta raíz de la práctica 2:
```
cd "Práctica 2 - Spring Boot y API en Python/SpringPythonAPI/"
```
2. Arranca todos los contenedores con Docker Compose:
```
docker-compose up --build
```

Esto desplegará una web en el puerto **8080** y la API en el **5050**.

> [!IMPORTANT]  
> Asegúrate de que esos puertos estén libres en tu máquina.

3. Por último introduce la dirección de la web en el navegador:
```
http://localhost:8080
```