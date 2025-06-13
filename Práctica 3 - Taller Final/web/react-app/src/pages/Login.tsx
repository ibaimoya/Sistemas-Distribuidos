import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Eye, EyeOff, ArrowLeft, Film } from 'lucide-react';
import { motion } from 'framer-motion';

export default function Login() {
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const containerRef = useRef<HTMLDivElement>(null);

  const stars = React.useMemo(
    () => Array.from({ length: 80 }, () => ({
      top : Math.random() * 100,
      left: Math.random() * 100,
      s   : Math.random() * 2 + 1,
      op  : Math.random() * 0.5 + 0.2,
      d   : Math.random() * 3 + 2,
      de  : Math.random() * 2
    })),
    []
  );

  const brightStars = React.useMemo(
    () => Array.from({ length: 20 }, () => ({
      top : Math.random() * 100,
      left: Math.random() * 100,
      s   : Math.random() * 3 + 2,
      op  : Math.random() * 0.3 + 0.5,
      d   : Math.random() * 3 + 2,
      de  : Math.random() * 2
    })),
    []
  );


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

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!containerRef.current) return;
      
      const { clientX, clientY } = e;
      const { innerWidth, innerHeight } = window;
      
      const x = (clientX / innerWidth) * 100;
      const y = (clientY / innerHeight) * 100;
      
      containerRef.current.style.background = `
        radial-gradient(circle at ${x}% ${y}%, 
          rgba(29, 185, 84, 0.08) 0%, 
          rgba(29, 185, 84, 0.03) 12%, 
          transparent 18%),
        linear-gradient(135deg, #111111 0%, #1a1a1a 50%, #141414 100%)
      `;
    };

    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  return (
    <div 
      ref={containerRef}
      className="min-h-screen bg-gradient-to-br from-[#111111] via-[#1a1a1a] to-[#141414] flex items-center justify-center relative overflow-hidden"
    >
      {/* Animated background elements */}
      <div className="fixed inset-0">
        {/* Stars */}
        {stars.map((st, i) => (
          <div
            key={i}
            className="absolute"
            style={{
              top : `${st.top}%`,
              left: `${st.left}%`,
              width : `${st.s}px`,
              height: `${st.s}px`,
              opacity: st.op,
              animation: `twinkle ${st.d}s ease-in-out infinite`,
              animationDelay: `${st.de}s`,
              background: 'radial-gradient(circle at center, #1db954, #25d366 30%, transparent 70%)',
              boxShadow: '0 0 10px 2px rgba(29,185,84,0.4)',
              borderRadius: '50%',
              filter: 'blur(0.5px)'
            }}
          />
        ))}

        {/* Bright stars */}
        {brightStars.map((st, i) => (
          <div
            key={`bright-${i}`}
            className="absolute"
            style={{
              top : `${st.top}%`,
              left: `${st.left}%`,
              width : `${st.s}px`,
              height: `${st.s}px`,
              opacity: st.op,
              animation: `twinkle ${st.d}s ease-in-out infinite`,
              animationDelay: `${st.de}s`,
              background: 'radial-gradient(circle at center, #1db954, #25d366 40%, transparent 80%)',
              boxShadow: '0 0 15px 4px rgba(29,185,84,0.6)',
              borderRadius: '50%',
              filter: 'blur(1px)'
            }}
          />
        ))}
      </div>

      {/* CSS for twinkle animation */}
      <style jsx>{`
        @keyframes twinkle {
          0%, 100% {
            opacity: 0.2;
            filter: blur(1px);
          }
          50% {
            opacity: 1;
            filter: blur(0.5px);
          }
        }
      `}</style>

      {/* Back button */}
      <motion.div 
        className="absolute top-8 left-8 z-20"
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Link
          to="/welcome"
          className="flex items-center space-x-2 text-white/70 hover:text-[#1db954] transition-all duration-300 group"
        >
          <ArrowLeft size={20} className="transform group-hover:-translate-x-1 transition-transform duration-300" />
          <span className="text-sm font-medium">Volver</span>
        </Link>
      </motion.div>

      {/* Main content */}
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.95 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="relative z-10 w-full max-w-md mx-4"
      >
        <div className="bg-black/40 backdrop-blur-xl border border-white/10 rounded-2xl p-8 shadow-2xl relative overflow-hidden">
          {/* Decorative elements */}
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-[#1db954] to-transparent" />
          <div className="absolute -top-10 -right-10 w-20 h-20 bg-[#1db954]/10 rounded-full blur-xl" />
          <div className="absolute -bottom-10 -left-10 w-16 h-16 bg-[#1db954]/10 rounded-full blur-xl" />

          {/* Header */}
          <motion.div 
            className="text-center mb-8"
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            <div className="flex items-center justify-center mb-4">
              <div className="p-3 bg-[#1db954]/20 rounded-full border border-[#1db954]/30">
                <Film size={24} className="text-[#1db954]" />
              </div>
            </div>
            <motion.h1 
              className="text-4xl font-bold text-[#1db954] mb-2 tracking-tight cursor-pointer"
              whileHover={{ scale: 1.05 }}
              transition={{ duration: 0.2 }}
            >
              Kinora
            </motion.h1>
            <p className="text-white/60 text-sm">
              Bienvenido de vuelta
            </p>
          </motion.div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Error message */}
            {error && (
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-red-500/10 border border-red-500/30 text-red-400 text-sm text-center p-3 rounded-lg backdrop-blur-sm"
              >
                {error}
              </motion.div>
            )}

            {/* Username field */}
            <motion.div 
              className="space-y-2"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.5, delay: 0.3 }}
            >
              <label htmlFor="username" className="block text-sm font-medium text-white/80">
                Nombre de usuario
              </label>
              <div className="relative group">
                <input
                  id="username"
                  name="username"
                  type="text"
                  value={formData.username}
                  onChange={handleChange}
                  required
                  disabled={loading}
                  className="w-full px-4 py-3 bg-white/5 border border-white/20 rounded-lg text-white placeholder-white/40 transition-all duration-300 focus:outline-none focus:border-[#1db954] focus:bg-white/10 focus:shadow-[0_0_0_3px_rgba(29,185,84,0.1)] group-hover:border-white/30 disabled:opacity-50"
                  placeholder="Introduce tu usuario"
                />
                <div className="absolute inset-0 bg-gradient-to-r from-[#1db954]/0 via-[#1db954]/5 to-[#1db954]/0 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 rounded-lg pointer-events-none" />
              </div>
            </motion.div>

            {/* Password field */}
            <motion.div 
              className="space-y-2"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.5, delay: 0.4 }}
            >
              <label htmlFor="password" className="block text-sm font-medium text-white/80">
                Contraseña
              </label>
              <div className="relative group">
                <input
                  id="password"
                  name="password"
                  type={show ? 'text' : 'password'}
                  value={formData.password}
                  onChange={handleChange}
                  required
                  disabled={loading}
                  className="w-full px-4 py-3 pr-12 bg-white/5 border border-white/20 rounded-lg text-white placeholder-white/40 transition-all duration-300 focus:outline-none focus:border-[#1db954] focus:bg-white/10 focus:shadow-[0_0_0_3px_rgba(29,185,84,0.1)] group-hover:border-white/30 disabled:opacity-50"
                  placeholder="Introduce tu contraseña"
                />
                <button
                  type="button"
                  onClick={() => setShow(!show)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-white/40 hover:text-[#1db954] transition-colors duration-200 p-1"
                >
                  {show ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
                <div className="absolute inset-0 bg-gradient-to-r from-[#1db954]/0 via-[#1db954]/5 to-[#1db954]/0 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 rounded-lg pointer-events-none" />
              </div>
            </motion.div>

            {/* Submit button */}
            <motion.button
              type="submit"
              disabled={loading || !formData.username || !formData.password}
              className="w-full bg-gradient-to-r from-[#1db954] to-[#1ed760] text-black font-bold py-3 px-4 rounded-lg text-base tracking-wide transition-all duration-300 hover:scale-[1.02] hover:shadow-[0_8px_25px_rgba(29,185,84,0.3)] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 relative overflow-hidden group"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.5 }}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <span className="relative z-10">
                {loading ? (
                  <div className="flex items-center justify-center space-x-2">
                    <div className="w-4 h-4 border-2 border-black border-t-transparent rounded-full animate-spin" />
                    <span>Iniciando...</span>
                  </div>
                ) : (
                  'Iniciar sesión'
                )}
              </span>
              <div className="absolute inset-0 bg-white/20 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left" />
            </motion.button>

            {/* Register link */}
            <motion.p 
              className="text-center text-sm text-white/60 mt-6"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.6 }}
            >
              ¿No tienes cuenta?{' '}
              <Link 
                to="/register" 
                className="text-[#1db954] font-medium hover:text-[#1ed760] transition-colors duration-200 relative group"
              >
                Regístrate aquí
                <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-[#1db954] group-hover:w-full transition-all duration-300" />
              </Link>
            </motion.p>
          </form>
        </div>
      </motion.div>
    </div>
  );
}