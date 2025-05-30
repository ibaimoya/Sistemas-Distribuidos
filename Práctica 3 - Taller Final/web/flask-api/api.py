import os
from flask import Flask, jsonify, request, abort
from flask_cors import CORS
import requests
from errors import (
    AppError, NotFoundError, TimeoutError,
    ExternalAPIError, FileNotFoundErrorApp, FileReadErrorApp
)

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "*"}})

# ───────────────────────────────
#      CONFIGURACIÓN TMDB
# ───────────────────────────────
TMDB_API_KEY       = os.getenv("TMDB_API_KEY")
TMDB_ACCESS_TOKEN  = os.getenv("TMDB_ACCESS_TOKEN")

if not TMDB_API_KEY and not TMDB_ACCESS_TOKEN:
    raise RuntimeError(
        "Debes definir TMDB_API_KEY o TMDB_ACCESS_TOKEN en variables de entorno."
    )

TMDB_MOVIE_URL   = "https://api.themoviedb.org/3/movie/{}"
TMDB_SEARCH_URL  = "https://api.themoviedb.org/3/search/movie"
TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular"


def tmdb_get(url: str, **params):
    """
    Llama a TMDB usando:
      • Authorization: Bearer <token-v4>   (si lo configuraste)
      • o ?api_key=<v3>                    (en su defecto)
    Maneja timeouts y mapea a nuestros AppErrors.
    """
    headers = {}
    if TMDB_ACCESS_TOKEN:
        headers["Authorization"] = f"Bearer {TMDB_ACCESS_TOKEN}"
    else:
        params.setdefault("api_key", TMDB_API_KEY)

    try:
        r = requests.get(url, headers=headers, params=params, timeout=4)
        r.raise_for_status()
        return r.json()

    except requests.exceptions.HTTPError as exc:
        # 404 ó 401 → NotFound / Unauthorized
        status = exc.response.status_code
        if status == 404:
            raise NotFoundError("Recurso no encontrado en TMDB.")
        elif status == 401:
            raise ExternalAPIError("Credenciales TMDB inválidas (401).")
        raise ExternalAPIError()

    except requests.exceptions.Timeout:
        raise TimeoutError()

    except Exception:
        raise ExternalAPIError()


# ───────────────────────────────
#  MANEJO DE ERRORES GENÉRICOS
# ───────────────────────────────
@app.errorhandler(AppError)
def handle_app_error(exc: AppError):
    return jsonify(exc.to_dict()), exc.status_code


@app.errorhandler(404)
def handle_not_found(error):
    return jsonify({"error": "Endpoint no encontrado."}), 404


@app.errorhandler(Exception)
def handle_unexpected(exc: Exception):
    return jsonify({"error": "Error interno del servidor."}), 500


# ───────────────────────────────
#            ENDPOINTS
# ───────────────────────────────
@app.route("/api/movie/<int:movie_id>")
def get_movie(movie_id: int):
    """Devuelve los detalles de una película por ID."""
    data = tmdb_get(TMDB_MOVIE_URL.format(movie_id))
    return jsonify(data)


@app.route("/api/tmdb/movie/<int:movie_id>")
def get_tmdb_movie(movie_id: int):
    """Devuelve los detalles de una película por ID (ruta alternativa)."""
    data = tmdb_get(TMDB_MOVIE_URL.format(movie_id))
    return jsonify(data)


@app.route("/api/movies")
def get_movies():
    """
    • Con ?query=… => búsqueda por título.
    • Sin query     => listado popular.
      Acepta ?page=N (1-1000).
    """
    query = request.args.get("query", "").strip()
    page_raw = request.args.get("page", "1")

    # Validar page
    try:
        page = max(1, int(page_raw))
    except ValueError:
        raise AppError("Parámetro 'page' debe ser numérico.", status_code=400)

    if query:
        data = tmdb_get(TMDB_SEARCH_URL, query=query, page=page, include_adult=False)
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
            return jsonify({"content": f.read()})
    except FileNotFoundError:
        raise FileNotFoundErrorApp(path)
    except PermissionError:
        raise FileReadErrorApp(path)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)