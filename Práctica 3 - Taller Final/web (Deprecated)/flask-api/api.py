from flask import Flask, jsonify, abort, request
from flask_cors import CORS
import requests
from errors import AppError, NotFoundError, TimeoutError, ExternalAPIError, FileNotFoundErrorApp, FileReadErrorApp

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "*"}})

@app.errorhandler(AppError)
def handle_app_error(exc: AppError):
    """Convierte cualquier AppError en un JSON {'error': mensaje} con su código HTTP."""
    resp = jsonify(exc.to_dict())
    resp.status_code = exc.status_code
    return resp

@app.errorhandler(Exception)
def handle_unexpected(exc: Exception):
    """Todo lo demás (bugs, errores inesperados) cae aquí y devuelve un 500 genérico."""
    generic = AppError("Error interno del servidor.")
    return handle_app_error(generic)

POKE_URL = "https://pokeapi.co/api/v2/pokemon/{}"
POKE_LIST_URL = "https://pokeapi.co/api/v2/pokemon"


@app.route("/api/pokemon/<name_or_id>")
def get_pokemon(name_or_id: str):
    try:
        r = requests.get(POKE_URL.format(name_or_id.lower()), timeout=4)
        r.raise_for_status()
        return jsonify(r.json())
    except requests.exceptions.HTTPError:
        raise NotFoundError(f"Pokémon “{name_or_id}” no encontrado.")
    except requests.exceptions.Timeout:
        raise TimeoutError()
    except Exception:
        raise ExternalAPIError()


@app.route("/api/pokemons")
def get_pokemon_list():

    limit  = request.args.get("limit", 2000)
    offset = request.args.get("offset", 0)
    try:
        r = requests.get(
            f"{POKE_LIST_URL}?limit={limit}&offset={offset}",
            timeout=4
        )
        r.raise_for_status()
        return jsonify(r.json())
    except requests.exceptions.HTTPError:
        raise NotFoundError(f"Lista no encontrada.")
    except requests.exceptions.Timeout:
        raise TimeoutError()
    except Exception:
        raise ExternalAPIError()


@app.errorhandler(404)
def handle_404(e):
    return jsonify(error=str(e)), 404

@app.errorhandler(504)
def handle_504(e):
    return jsonify(error=str(e)), 504

@app.errorhandler(500)
def handle_500(e):

    return jsonify(error=str(e)), 500

# Simulaciones de errores para pruebas. #

@app.route("/api/test/timeout")
def test_timeout():
    # Simula un timeout directamente
    abort(504, "Tiempo de espera agotado.")

@app.route("/api/test/error")
def test_error():
    # Lanza una excepción no manejada para probar el 500
    raise RuntimeError("Error interno simulado")


@app.route("/api/test/file")
def test_file():
    path = request.args.get("path")
    if not path:
        # ahora no hace falta pasar status_code
        raise AppError("Falta el parámetro 'path'.", status_code=400)

    try:
        with open(path, "r", encoding="utf-8") as f:
            contenido = f.read()
        return jsonify({"content": contenido})
    except FileNotFoundError:
        raise FileNotFoundErrorApp(path)
    except PermissionError:
        raise FileReadErrorApp(path)
    


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
