import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';

export default function Login() {
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
        const response = await fetch('/auth/login', {        
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData),
        credentials: 'include'
      });

      const data = await response.json();

      if (response.ok) {
        window.location.href = '/';
      } else {
        setError(data.message || 'Error al iniciar sesión');
      }
    } catch (err) {
      setError('Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#111111] flex justify-center items-center bg-gradient-to-br from-[#111111] via-[#1a1a1a] to-[#141414] animate-gradient relative auth-background overflow-hidden">
      <main className="bg-[rgba(18,18,18,0.8)] backdrop-blur-lg p-12 rounded-xl w-[340px] shadow-lg transition-all duration-300 ease-out hover:translate-y-[-5px] border border-white/5 relative z-10">
        <h1 className="text-4xl font-bold text-center text-[#1db954] mb-8 transition-transform duration-300 hover:scale-105">Kinora</h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="text-red-500 text-sm text-center">{error}</div>
          )}

          <div className="space-y-2">
            <label htmlFor="username" className="block text-sm font-medium text-white">
              Nombre de usuario
            </label>
            <input
              id="username"
              name="username"
              type="text"
              value={formData.username}
              onChange={handleChange}
              required
              disabled={loading}
              className="w-full px-3 py-3 border border-white/10 rounded-md bg-white/3 text-white text-[15px] transition-all duration-200 focus:outline-none focus:border-[#1db954] focus:bg-white/5 focus:shadow-[0_0_0_3px_rgba(29,185,84,0.1)]"
            />
          </div>

          <div className="space-y-2 relative">
            <label htmlFor="password" className="block text-sm font-medium text-white">
              Contraseña
            </label>
            <div className="relative">
              <input
                id="password"
                name="password"
                type={show ? 'text' : 'password'}
                value={formData.password}
                onChange={handleChange}
                required
                disabled={loading}
                className="w-full px-3 py-3 border border-white/10 rounded-md bg-white/3 text-white text-[15px] transition-all duration-200 focus:outline-none focus:border-[#1db954] focus:bg-white/5 focus:shadow-[0_0_0_3px_rgba(29,185,84,0.1)]"
              />
              <button
                type="button"
                onClick={() => setShow(!show)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-[#1db954] transition-colors duration-200"
              >
                {show ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading || !formData.username || !formData.password}
            className="w-full bg-[#1db954] text-white font-bold py-3 px-4 rounded-full text-base tracking-wide transition-all duration-300 hover:scale-[1.02] hover:bg-[#1ed760] hover:shadow-[0_4px_12px_rgba(29,185,84,0.2)] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>

          <p className="text-center text-sm text-gray-400 mt-8">
            ¿No tienes cuenta?{' '}
            <Link to="/register" className="text-[#1db954] font-medium hover:underline hover:text-[#1ed760] transition-colors duration-200">
              Regístrate
            </Link>
          </p>
        </form>
      </main>
    </div>
  );
}