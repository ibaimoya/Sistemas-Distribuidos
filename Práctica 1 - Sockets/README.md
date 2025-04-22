## Práctica 1 - Sockets


### Autor: 
- [Ibai Moya Aroz](mailto:ima1013@alu.ubu.es)

****

### Instrucciones:

Para ejecutar el proyecto harán falta además de tener los [recursos](https://github.com/ibaimoya/Sistemas-Distribuidos/tree/main?tab=readme-ov-file#requisitos) instalados, ejecutar los siguientes comandos desde la carpeta de la práctica en concreto.

Primero se debe levantar el servidor que se corresponderá con el host del chat con el comando (desde la [carpeta raíz](https://github.com/ibaimoya/Sistemas-Distribuidos/tree/main/Práctica%201%20-%20Sockets/chat1.0) del proyecto):
```maven
mvn exec:java@server
```

> [!IMPORTANT]  
> El servidor tiene que ser el **primer** elemento en activarse.

Una vez lanzado el servidor se pueden crear los clientes en cualquier orden:
- Para lanzar el cliente **pio**:
```maven
mvn exec:java@cliente-pio
```

- Para lanzar el cliente **blas**:
```maven
mvn exec:java@cliente-blas
```

Una vez dentro del chat se puede vetar a otro usuario para que no aparezcan sus mensajes usando:
```
ban <usuario>
```

Si se quiere anular el veto se puede hacer de la siguiente manera:
```
unban <usuario>
```

Si se quiere cerrar sesion se puede usar:
```
logout
```

Y, por último, para apagar el servidor, se puede usar el siguiente comando (desde el lado del servidor):
```
shutdown
```

****

### Documentación:

La documentación se encuentra en el directorio **[/doc](https://github.com/ibaimoya/Sistemas-Distribuidos/tree/main/Práctica%201%20-%20Sockets/chat1.0/doc)**, aunque para generar nueva documentación, desde la [carpeta raíz](https://github.com/ibaimoya/Sistemas-Distribuidos/tree/main/Práctica%201%20-%20Sockets/chat1.0) basta con ejecutar el siguiente comando:
```ant
ant javadoc
```

****
