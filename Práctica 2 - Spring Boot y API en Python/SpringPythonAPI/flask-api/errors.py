class AppError(Exception):
    status_code = 500
    def __init__(self, message=None):
        super().__init__(message or self.message)
        self.message = message or getattr(self, 'message', 'Error interno.')
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