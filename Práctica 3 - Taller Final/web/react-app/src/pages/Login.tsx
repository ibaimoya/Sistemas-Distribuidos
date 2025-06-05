import React, { useState } from "react";

export default function Login() {
  const [show, setShow] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    password: ""
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch("/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(formData),
        credentials: "include"
      });

      const data = await response.json();

      if (data.success) {
        // Redirige al home
        window.location.href = "/";
      } else {
        setError(data.message || "Error al iniciar sesión");
      }
    } catch (err) {
      setError("Error de conexión");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="card">
      <h1 className="brand">Kinora</h1>

      <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
        {error && (
          <div className="error-message" style={{ color: "red", marginBottom: "1rem" }}>
            {error}
          </div>
        )}

        <div className="field">
          <label htmlFor="username">Usuario o Correo</label>
          <input 
            id="username" 
            name="username" 
            type="text" 
            value={formData.username}
            onChange={handleChange}
            required 
            disabled={loading}
          />
        </div>

        <div className="field">
          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            name="password"
            type={show ? "text" : "password"}
            value={formData.password}
            onChange={handleChange}
            required
            disabled={loading}
          />
          <i
            className={`password-toggle fas fa-eye${show ? "-slash" : ""}`}
            onClick={() => setShow(!show)}
          />
        </div>

        <button 
          className="btn-green" 
          onClick={handleSubmit}
          disabled={loading || !formData.username || !formData.password}
        >
          {loading ? "Iniciando sesión..." : "Iniciar sesión"}
        </button>

        <p className="muted">
          ¿No tienes cuenta? <a href="/register">Regístrate</a>
        </p>
      </div>
    </main>
  );
}