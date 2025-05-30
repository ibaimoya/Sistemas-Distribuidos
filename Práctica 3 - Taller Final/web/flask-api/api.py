import os
from flask import Flask, jsonify, abort, request
from flask_cors import CORS
import requests
from errors import (AppError, NotFoundError, TimeoutError,
                    ExternalAPIError, FileNotFoundErrorApp, FileReadErrorApp)

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "*"}})

# ───────────────────────────────
#      CONFIGURACIÓN TMDB
# ───────────────────────────────
TMDB_API_KEY = os.getenv("TMDB_API_KEY")
if not TMDB_API_KEY:
    raise RuntimeError(
        "Define la variable de entorno TMDB_API_KEY con tu clave de The Movie Database."
    )

TMDB_MOVIE_URL   = "https://api.themoviedb.org/3/movie/{}"
TMDB_SEARCH_URL  = "https://api.themoviedb.org/3/search/movie"
TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular"


def tmdb_get(url, **params):
    """Wrapper que añade api_key y maneja timeouts / HTTP."""
    params.setdefault("api_key", TMDB_API_KEY)
    try:
        r = requests.get(url, params=params, timeout=4)
        r.raise_for_status()
        return r.json()
    except requests.exceptions.HTTPError:
        raise NotFoundError("Recurso no encontrado en TMDB.")
    except requests.exceptions.Timeout:
        raise TimeoutError()
    except Exception:
        raise ExternalAPIError()


# ───────────────────────────────
#  MANEJO DE ERRORES GENÉRICOS
# ───────────────────────────────
@app.errorhandler(AppError)
def handle_app_error(exc: AppError):
    resp = jsonify(exc.to_dict())
    resp.status_code = exc.status_code
    return resp


@app.errorhandler(Exception)
def handle_unexpected(exc: Exception):
    generic = AppError("Error interno del servidor.")
    return handle_app_error(generic)


# ───────────────────────────────
#      ENDPOINTS PRINCIPALES
# ───────────────────────────────
@app.route("/api/movie/<int:movie_id>")
def get_movie(movie_id: int):
    """Detalles de una película por ID."""
    data = tmdb_get(TMDB_MOVIE_URL.format(movie_id))
    return jsonify(data)


@app.route("/api/movies")
def get_movies():
    """
    Lista de películas.
    • Si viene ?query=… → búsqueda por título.
    • Sin query → lista popular (paginada).
    Acepta ?page=N (1-1000).
    """
    query = request.args.get("query")
    page  = request.args.get("page", 1)

    if query:
        data = tmdb_get(TMDB_SEARCH_URL, query=query, page=page)
    else:
        data = tmdb_get(TMDB_POPULAR_URL, page=page)

    return jsonify(data)


# ───────────────────────────────
#      ENDPOINTS DE PRUEBA 
# ───────────────────────────────
@app.route("/api/test/timeout")
def test_timeout():
    abort(504, "Tiempo de espera agotado.")


@app.route("/api/test/error")
def test_error():
    raise RuntimeError("Error interno simulado")


@app.route("/api/test/file")
def test_file():
    path = request.args.get("path")
    if not path:
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
