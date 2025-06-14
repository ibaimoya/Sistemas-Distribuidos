import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { User, Film, Trash2, ChevronDown, ChevronUp, Star, Users, UserX } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Header } from '../components';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview?: string;
  rating?: number | null;
}

interface UserDto {
  id: number;
  nombre: string;
  email: string;
  favoriteMovies?: Movie[];
}

export default function AdminPanel() {
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [expandedUser, setExpandedUser] = useState<number | null>(null);
  const [loadingMovies, setLoadingMovies] = useState<number | null>(null);

  useEffect(() => {
    fetch("/admin/users", { credentials: "include" })
      .then(r => r.json())
      .then(setUsers)
      .finally(() => setLoading(false));
  }, []);

  const fetchUserMovies = async (userId: number) => {
    if (users.find(u => u.id === userId)?.favoriteMovies) {
      setExpandedUser(expandedUser === userId ? null : userId);
      return;
    }

    setLoadingMovies(userId);
    try {
      const response = await fetch(`/admin/users/${userId}/movies`, {
        credentials: "include"
      });
      const movies = await response.json();

      setUsers(prev => prev.map(user =>
        user.id === userId
          ? { ...user, favoriteMovies: movies }
          : user
      ));
      setExpandedUser(userId);
    } catch (error) {
      console.error('Error fetching user movies:', error);
    } finally {
      setLoadingMovies(null);
    }
  };

  const handleDeleteUser = async (id: number) => {
    if (!confirm("¿Seguro que quieres borrar este usuario? Esta acción no se puede deshacer.")) return;

    try {
      await fetch(`/admin/users/${id}`, {
        method: "DELETE",
        credentials: "include"
      });
      setUsers(prev => prev.filter(u => u.id !== id));
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#111111] flex justify-center items-center">
        <div className="flex space-x-2">
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce"></div>
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      <Header 
        title="Panel de Administración" 
        showBackButton={true} 
        backTo="/" 
        showNotifications={true} 
      />

      <main className="pt-32 px-8 pb-8">
        <div className="max-w-6xl mx-auto">
          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-[#1a1a1a] rounded-xl p-6 border border-[#1db954]/20">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 rounded-full bg-[#1db954]/20 flex items-center justify-center">
                  <Users size={24} className="text-[#1db954]" />
                </div>
                <div>
                  <p className="text-2xl font-bold">{users.length}</p>
                  <p className="text-gray-400">Usuarios registrados</p>
                </div>
              </div>
            </div>

            <div className="bg-[#1a1a1a] rounded-xl p-6 border border-blue-500/20">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 rounded-full bg-blue-500/20 flex items-center justify-center">
                  <Film size={24} className="text-blue-400" />
                </div>
                <div>
                  <p className="text-2xl font-bold">
                    {users.reduce((acc, user) => acc + (user.favoriteMovies?.length || 0), 0)}
                  </p>
                  <p className="text-gray-400">Películas guardadas como favoritas</p>
                </div>
              </div>
            </div>

            <div className="bg-[#1a1a1a] rounded-xl p-6 border border-purple-500/20">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 rounded-full bg-purple-500/20 flex items-center justify-center">
                  <Star size={24} className="text-purple-400" />
                </div>
                <div>
                  <p className="text-2xl font-bold">
                    {users.filter(user => 
                      user.favoriteMovies?.some(movie => movie.rating !== undefined && movie.rating !== null)
                    ).length}
                  </p>
                  <p className="text-gray-400">Usuarios activos</p>
                </div>
              </div>
            </div>
          </div>

          {/* Users List */}
          {users.length === 0 ? (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="text-center py-16"
            >
              <div className="w-24 h-24 rounded-full bg-[#1a1a1a] flex items-center justify-center mx-auto mb-6">
                <UserX size={48} className="text-gray-600" />
              </div>
              <h2 className="text-3xl font-bold text-gray-400 mb-4">No hay usuarios registrados</h2>
              <p className="text-gray-500 mb-8 max-w-md mx-auto">
                Cuando los usuarios se registren en la plataforma, aparecerán aquí para que puedas administrarlos.
              </p>
              <div className="bg-[#1a1a1a] rounded-lg p-6 max-w-lg mx-auto">
                <h3 className="text-lg font-semibold text-[#1db954] mb-3">¿Qué puedes hacer como admin?</h3>
                <ul className="text-left text-gray-400 space-y-2">
                  <li className="flex items-center space-x-2">
                    <div className="w-2 h-2 bg-[#1db954] rounded-full"></div>
                    <span>Ver todas las películas favoritas de los usuarios</span>
                  </li>
                  <li className="flex items-center space-x-2">
                    <div className="w-2 h-2 bg-[#1db954] rounded-full"></div>
                    <span>Consultar las valoraciones que han dado</span>
                  </li>
                  <li className="flex items-center space-x-2">
                    <div className="w-2 h-2 bg-[#1db954] rounded-full"></div>
                    <span>Eliminar usuarios si es necesario</span>
                  </li>
                </ul>
              </div>
            </motion.div>
          ) : (
            <div className="bg-[#1a1a1a] rounded-xl overflow-hidden shadow-xl border border-white/10">
              <div className="bg-gradient-to-r from-[#1db954]/10 to-[#1ed760]/10 px-6 py-4 border-b border-white/10">
                <h2 className="text-xl font-semibold text-white">Usuarios Registrados</h2>
                <p className="text-sm text-gray-400 mt-1">Gestiona los {users.length} usuarios de la plataforma</p>
              </div>

              {users.map(user => (
                <div key={user.id} className="border-b border-white/5 last:border-none">
                  {/* Fila principal del usuario */}
                  <div className="flex items-center justify-between p-6 hover:bg-white/5 transition-colors">
                    <div className="flex items-center space-x-4">
                      <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#1db954] to-[#1ed760] flex items-center justify-center">
                        <span className="text-white font-bold text-lg">
                          {user.nombre.charAt(0).toUpperCase()}
                        </span>
                      </div>
                      <div>
                        <h3 className="font-semibold text-lg">{user.nombre}</h3>
                        <p className="text-sm text-gray-400">{user.email}</p>
                      </div>
                    </div>

                    <div className="flex items-center space-x-4">
                      <button
                        onClick={() => fetchUserMovies(user.id)}
                        className="flex items-center space-x-2 px-5 py-2.5 rounded-full bg-[#1db954]/10 text-[#1db954] hover:bg-[#1db954]/20 transition-all duration-200 hover:scale-105"
                        disabled={loadingMovies === user.id}
                      >
                        <Film size={18} />
                        <span className="font-medium">Ver Películas</span>
                        {loadingMovies === user.id ? (
                          <div className="w-4 h-4 border-2 border-[#1db954] border-t-transparent rounded-full animate-spin"></div>
                        ) : (
                          expandedUser === user.id ? <ChevronUp size={18} /> : <ChevronDown size={18} />
                        )}
                      </button>

                      <button
                        onClick={() => handleDeleteUser(user.id)}
                        className="p-2.5 rounded-full hover:bg-red-500/10 text-red-400 hover:text-red-300 transition-all duration-200 hover:scale-110"
                        title="Eliminar usuario"
                      >
                        <Trash2 size={20} />
                      </button>
                    </div>
                  </div>

                  {/* Lista de películas favoritas */}
                  <AnimatePresence>
                    {expandedUser === user.id && user.favoriteMovies && (
                      <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: 'auto', opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.3 }}
                        className="overflow-hidden"
                      >
                        <div className="bg-[#0d0d0d] border-t border-white/10">
                          <div className="p-6">
                            <div className="flex items-center space-x-3 mb-4">
                              <div className="w-1 h-6 bg-[#1db954] rounded-full"></div>
                              <h4 className="text-lg font-semibold text-[#1db954]">
                                Películas Favoritas de {user.nombre}
                              </h4>
                              <span className="bg-[#1db954]/20 text-[#1db954] px-2 py-1 rounded-full text-xs font-medium">
                                {user.favoriteMovies.length} películas
                              </span>
                            </div>
                            
                            {user.favoriteMovies.length === 0 ? (
                              <div className="text-center py-8">
                                <Film size={48} className="mx-auto text-gray-600 mb-4" />
                                <p className="text-gray-400">Este usuario no tiene películas favoritas todavía</p>
                              </div>
                            ) : (
                              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {user.favoriteMovies.map(movie => (
                                  <motion.div
                                    key={movie.id}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    className="bg-[#1a1a1a] rounded-lg p-4 border border-white/10 hover:border-[#1db954]/30 transition-all duration-200"
                                  >
                                    <div className="flex items-start space-x-4">
                                      <Link to={`/movie/${movie.id}`} className="shrink-0 group">
                                        <img
                                          src={movie.poster_path
                                            ? `https://image.tmdb.org/t/p/w185${movie.poster_path}`
                                            : `data:image/svg+xml;base64,${btoa(`
                                                <svg width="120" height="180" xmlns="http://www.w3.org/2000/svg">
                                                  <rect width="120" height="180" fill="#1a1a1a"/>
                                                  <text x="60" y="90" font-family="Arial" font-size="12" fill="#1db954" text-anchor="middle" dominant-baseline="middle">Sin Carátula</text>
                                                </svg>
                                              `)}`}
                                          alt={movie.title}
                                          className="w-16 h-24 object-cover rounded-md group-hover:scale-105 transition-transform duration-200"
                                        />
                                      </Link>
                                      <div className="flex-1 min-w-0">
                                        <Link to={`/movie/${movie.id}`}>
                                          <h5 className="font-semibold text-white leading-snug hover:text-[#1db954] transition-colors line-clamp-2">
                                            {movie.title}
                                          </h5>
                                        </Link>
                                        {movie.rating !== undefined && movie.rating !== null ? (
                                          <div className="flex items-center space-x-2 mt-2">
                                            <div className="flex items-center space-x-1">
                                              <Star size={14} className="fill-yellow-400 text-yellow-400" />
                                              <span className="text-yellow-400 font-medium">{movie.rating}</span>
                                              <span className="text-gray-500 text-sm">/ 5</span>
                                            </div>
                                            <span className="text-xs text-gray-500 bg-green-500/20 text-green-400 px-2 py-1 rounded-full">
                                              Valorada
                                            </span>
                                          </div>
                                        ) : (
                                          <span className="text-xs text-gray-500 bg-gray-500/20 px-2 py-1 rounded-full mt-2 inline-block">
                                            Sin valorar
                                          </span>
                                        )}
                                      </div>
                                    </div>
                                  </motion.div>
                                ))}
                              </div>
                            )}
                          </div>
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}