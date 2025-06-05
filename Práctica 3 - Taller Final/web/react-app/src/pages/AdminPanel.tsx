import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, User, Film, Trash2, ChevronDown, ChevronUp } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
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
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      {/* Header */}
      <header className="fixed w-full z-50 flex justify-between items-center px-8 py-4 bg-gradient-to-b from-black/80 to-transparent">
        <div className="flex items-center space-x-4">
          <Link to="/" className="text-[#1db954] hover:text-[#1ed760] transition-colors">
            <ArrowLeft size={24} />
          </Link>
          <h1 className="text-3xl font-bold text-[#1db954]">Panel de Administración</h1>
        </div>
      </header>

      <main className="pt-24 px-8">
        <div className="max-w-6xl mx-auto">
          <div className="bg-[#1a1a1a] rounded-xl overflow-hidden shadow-xl">
            {users.map(user => (
              <div key={user.id} className="border-b border-white/10 last:border-none">
                <div className="flex items-center justify-between p-6 hover:bg-white/5 transition-colors">
                  <div className="flex items-center space-x-4">
                    <div className="w-10 h-10 rounded-full bg-[#1db954]/20 flex items-center justify-center">
                      <User size={20} className="text-[#1db954]" />
                    </div>
                    <div>
                      <h3 className="font-semibold">{user.nombre}</h3>
                      <p className="text-sm text-gray-400">{user.email}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-center space-x-4">
                    <button
                      onClick={() => fetchUserMovies(user.id)}
                      className="flex items-center space-x-2 px-4 py-2 rounded-full bg-[#1db954]/10 text-[#1db954] hover:bg-[#1db954]/20 transition-colors"
                      disabled={loadingMovies === user.id}
                    >
                      <Film size={16} />
                      <span>Películas</span>
                      {loadingMovies !== user.id && (
                        expandedUser === user.id ? <ChevronUp size={16} /> : <ChevronDown size={16} />
                      )}
                    </button>
                    
                    <button
                      onClick={() => handleDeleteUser(user.id)}
                      className="p-2 rounded-full hover:bg-red-500/10 text-red-500 transition-colors"
                    >
                      <Trash2 size={20} />
                    </button>
                  </div>
                </div>

                <AnimatePresence>
                  {expandedUser === user.id && user.favoriteMovies && (
                    <motion.div
                      initial={{ height: 0, opacity: 0 }}
                      animate={{ height: "auto", opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }}
                      transition={{ duration: 0.3 }}
                      className="overflow-hidden"
                    >
                      <div className="p-6 pt-0">
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                          {user.favoriteMovies.length === 0 ? (
                            <p className="text-gray-400 col-span-full text-center py-4">
                              Este usuario no tiene películas favoritas
                            </p>
                          ) : (
                            user.favoriteMovies.map(movie => (
                              <motion.div
                                key={movie.id}
                                initial={{ scale: 0.9, opacity: 0 }}
                                animate={{ scale: 1, opacity: 1 }}
                                className="relative aspect-[2/3] rounded-lg overflow-hidden group"
                              >
                                <img
                                  src={movie.poster_path 
                                    ? `https://image.tmdb.org/t/p/w500${movie.poster_path}`
                                    : `data:image/svg+xml;base64,${btoa(`
                                        <svg width="500" height="750" xmlns="http://www.w3.org/2000/svg">
                                          <rect width="500" height="750" fill="#1a1a1a"/>
                                          <text x="250" y="375" font-family="Arial" font-size="24" fill="#1db954" text-anchor="middle" dominant-baseline="middle">Sin Carátula</text>
                                        </svg>
                                      `)}`
                                  }
                                  alt={movie.title}
                                  className="w-full h-full object-cover"
                                />
                                <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-end p-3">
                                  <p className="text-sm font-medium line-clamp-2">{movie.title}</p>
                                </div>
                              </motion.div>
                            ))
                          )}
                        </div>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}