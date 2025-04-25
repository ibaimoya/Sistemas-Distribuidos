from flask import Flask, jsonify, abort, request
from flask_cors import CORS   # para permitir peticiones desde 8080
import requests

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "*"}})   # CORS abierto

POKE_URL = "https://pokeapi.co/api/v2/pokemon/{}"
POKE_LIST_URL = "https://pokeapi.co/api/v2/pokemon"


@app.route("/api/pokemon/<name_or_id>")
def get_pokemon(name_or_id: str):
    try:
        r = requests.get(POKE_URL.format(name_or_id.lower()), timeout=4)
        r.raise_for_status()
        return jsonify(r.json())
    except requests.exceptions.HTTPError:
        abort(404, "Pokémon no encontrado.")
    except requests.exceptions.Timeout:
        abort(504, "Tiempo de espera agotado.")
    except Exception:
        abort(500, "Error al contactar con PokeAPI.")

@app.route("/api/pokemons")
def get_pokemon_list():
    # opcional: ?limit=100&offset=0
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
        abort(502, "Error al obtener la lista de Pokémon.")
    except requests.exceptions.Timeout:
        abort(504, "Tiempo de espera agotado.")
    except Exception:
        abort(500, "Error al contactar con PokeAPI.")

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
