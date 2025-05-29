# errors.py

class AppError(Exception):
    status_code = 500
    def __init__(self, message=None, status_code=None):
        # Guarda mensaje
        self.message = message or getattr(self, 'message', 'Error interno.')
        super().__init__(self.message)
        # Permite sobre-escribir el código desde el constructor
        if status_code is not None:
            self.status_code = status_code

    def to_dict(self):
        return {'error': self.message}
    
    
class NotFoundError(AppError):
    status_code = 404
    message = "Recurso no encontrado."

class TimeoutError(AppError):
    status_code = 504
    message = "Tiempo de espera agotado."

class ExternalAPIError(AppError):
    status_code = 502
    message = "Error en servicio externo."

class FileError(AppError):
    """Errores genéricos de fichero (I/O)."""
    status_code = 400

class FileNotFoundErrorApp(FileError):
    """Fichero no encontrado."""
    def __init__(self, path):
        super().__init__(f"Fichero no encontrado: {path}", FileError.status_code)

class FileReadErrorApp(FileError):
    """Error al leer el fichero."""
    def __init__(self, path):
        super().__init__(f"No se pudo leer el fichero: {path}", FileError.status_code)