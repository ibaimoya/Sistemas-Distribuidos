import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Eye, EyeOff, ArrowLeft, UserPlus, Mail, User, Lock } from 'lucide-react';
import { motion } from 'framer-motion';

export default function Register() {
  const [show1, setShow1] = useState(false);
  const [show2, setShow2] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    usuario: '',
    email: '',
    password: '',
    confirm: ''
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
    if (formData.password !== formData.confirm) {
      setError('Las contraseñas no coinciden');
      return;
    }

    setError('');
    setLoading(true);

    try {
      const response = await fetch('/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData),
        credentials: 'include'
      });

      const data = await response.json();

      if (response.ok) {
        window.location.href = '/login';
      } else {
        setError(data.message || 'Error al registrarse');
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
          rgba(29, 185, 84, 0.06) 0%, 
          rgba(29, 185, 84, 0.03) 12%, 
          transparent 18%),
        linear-gradient(135deg, #111111 0%, #1a1a1a 50%, #141414 100%)
      `;
    };

    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  const formFields = [
    {
      id: 'usuario',
      name: 'usuario',
      type: 'text',
      label: 'Nombre de usuario',
      placeholder: 'Elige tu nombre de usuario',
      icon: User,
      delay: 0.3
    },
    {
      id: 'email',
      name: 'email',
      type: 'email',
      label: 'Correo electrónico',
      placeholder: 'tu@email.com',
      icon: Mail,
      delay: 0.4
    },
    {
      id: 'password',
      name: 'password',
      type: show1 ? 'text' : 'password',
      label: 'Contraseña',
      placeholder: 'Crea una contraseña segura',
      icon: Lock,
      hasToggle: true,
      toggleState: show1,
      toggleFunction: () => setShow1(!show1),
      delay: 0.5
    },
    {
      id: 'confirm',
      name: 'confirm',
      type: show2 ? 'text' : 'password',
      label: 'Confirmar contraseña',
      placeholder: 'Repite tu contraseña',
      icon: Lock,
      hasToggle: true,
      toggleState: show2,
      toggleFunction: () => setShow2(!show2),
      delay: 0.6
    }
  ];

  return (
    <div 
      ref={containerRef}
      className="min-h-screen bg-gradient-to-br from-[#111111] via-[#1a1a1a] to-[#141414] flex items-center justify-center relative overflow-hidden py-8"
    >
      {/* Animated background elements - similar to Landing */}
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
          <div className="absolute -top-12 -right-12 w-24 h-24 bg-[#1db954]/8 rounded-full blur-2xl" />
          <div className="absolute -bottom-12 -left-12 w-20 h-20 bg-[#1db954]/8 rounded-full blur-2xl" />

          {/* Header */}
          <motion.div 
            className="text-center mb-8"
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            <div className="flex items-center justify-center mb-4">
              <div className="p-3 bg-[#1db954]/20 rounded-full border border-[#1db954]/30">
                <UserPlus size={24} className="text-[#1db954]" />
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
              Únete a nuestra comunidad
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

            {/* Form fields */}
            {formFields.map((field) => (
              <motion.div 
                key={field.id}
                className="space-y-2"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.5, delay: field.delay }}
              >
                <label htmlFor={field.id} className="block text-sm font-medium text-white/80">
                  {field.label}
                </label>
                <div className="relative group">
                  <div className="absolute left-3 top-1/2 -translate-y-1/2 text-white/40 group-focus-within:text-[#1db954] transition-colors duration-200">
                    <field.icon size={18} />
                  </div>
                  <input
                    id={field.id}
                    name={field.name}
                    type={field.type}
                    value={formData[field.name as keyof typeof formData]}
                    onChange={handleChange}
                    required
                    disabled={loading}
                    className="w-full pl-10 pr-4 py-3 bg-white/5 border border-white/20 rounded-lg text-white placeholder-white/40 transition-all duration-300 focus:outline-none focus:border-[#1db954] focus:bg-white/10 focus:shadow-[0_0_0_3px_rgba(29,185,84,0.1)] group-hover:border-white/30 disabled:opacity-50"
                    placeholder={field.placeholder}
                    style={field.hasToggle ? { paddingRight: '48px' } : {}}
                  />
                  {field.hasToggle && (
                    <button
                      type="button"
                      onClick={field.toggleFunction}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-white/40 hover:text-[#1db954] transition-colors duration-200 p-1"
                    >
                      {field.toggleState ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  )}
                  <div className="absolute inset-0 bg-gradient-to-r from-[#1db954]/0 via-[#1db954]/5 to-[#1db954]/0 opacity-0 group-focus-within:opacity-100 transition-opacity duration-300 rounded-lg pointer-events-none" />
                </div>
              </motion.div>
            ))}

            {/* Password strength indicator */}
            {formData.password && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: 'auto' }}
                className="space-y-2"
              >
                <div className="flex space-x-1">
                  {[...Array(4)].map((_, i) => (
                    <div
                      key={i}
                      className={`h-1 flex-1 rounded transition-colors duration-300 ${
                        formData.password.length > i * 2 + 2
                          ? formData.password.length >= 8 
                            ? 'bg-[#1db954]' 
                            : 'bg-yellow-500'
                          : 'bg-white/20'
                      }`}
                    />
                  ))}
                </div>
                <p className="text-xs text-white/50">
                  {formData.password.length < 6 
                    ? 'Contraseña muy débil' 
                    : formData.password.length < 8 
                    ? 'Contraseña débil' 
                    : 'Contraseña fuerte'}
                </p>
              </motion.div>
            )}

            {/* Submit button */}
            <motion.button
              type="submit"
              disabled={loading || !formData.usuario || !formData.email || !formData.password || !formData.confirm}
              className="w-full bg-gradient-to-r from-[#1db954] to-[#1ed760] text-black font-bold py-3 px-4 rounded-lg text-base tracking-wide transition-all duration-300 hover:scale-[1.02] hover:shadow-[0_8px_25px_rgba(29,185,84,0.3)] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 relative overflow-hidden group"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.7 }}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <span className="relative z-10">
                {loading ? (
                  <div className="flex items-center justify-center space-x-2">
                    <div className="w-4 h-4 border-2 border-black border-t-transparent rounded-full animate-spin" />
                    <span>Creando cuenta...</span>
                  </div>
                ) : (
                  'Crear cuenta'
                )}
              </span>
              <div className="absolute inset-0 bg-white/20 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left" />
            </motion.button>

            {/* Login link */}
            <motion.p 
              className="text-center text-sm text-white/60 mt-6"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.8 }}
            >
              ¿Ya tienes cuenta?{' '}
              <Link 
                to="/login" 
                className="text-[#1db954] font-medium hover:text-[#1ed760] transition-colors duration-200 relative group"
              >
                Inicia sesión aquí
                <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-[#1db954] group-hover:w-full transition-all duration-300" />
              </Link>
            </motion.p>
          </form>
        </div>
      </motion.div>
    </div>
  );
}