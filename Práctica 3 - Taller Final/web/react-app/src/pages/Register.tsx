import React, { useState } from "react";

export default function Register() {
  const [show1, setShow1] = useState(false);
  const [show2, setShow2] = useState(false);

  return (
    <main className="card">
      <h1 className="brand">Kinora</h1>

      <form action="/register" method="post">
        <div className="field">
          <label htmlFor="usuario">Usuario</label>
          <input id="usuario" name="usuario" required />
        </div>

        <div className="field">
          <label htmlFor="email">Correo</label>
          <input id="email" name="email" type="email" required />
        </div>

        <div className="field">
          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            name="password"
            type={show1 ? "text" : "password"}
            required
          />
          <i
            className={`password-toggle fas fa-eye${show1 ? "-slash" : ""}`}
            onClick={() => setShow1(!show1)}
          />
        </div>

        <div className="field">
          <label htmlFor="confirm">Confirmar contraseña</label>
          <input
            id="confirm"
            name="confirm"
            type={show2 ? "text" : "password"}
            required
          />
          <i
            className={`password-toggle fas fa-eye${show2 ? "-slash" : ""}`}
            onClick={() => setShow2(!show2)}
          />
        </div>

        <button className="btn-green">Crear cuenta</button>

        <p className="muted">
          ¿Ya tienes cuenta? <a href="/app/login">Inicia sesión</a>
        </p>
      </form>
    </main>
  );
}