import React, { useState, useEffect } from 'react';
import { LogOut, Heart, User, ArrowLeft, HeartOff } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link } from 'react-router-dom';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview: string;
}

const MyMovies: React.FC = () => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [username, setUsername] = useState('Usuario');
  const [hoveredMovie, setHoveredMovie] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
    fetchFavorites();
  }, []);

  const checkAuth = async () => {
    try {
      const response = await fetch('/auth/check', {
        credentials: 'include'
      });
      const data = await response.json();
      
      if (data.authenticated) {
        setUsername(data.username);
      } 
    } catch (error) {
      console.error('Error checking auth:', error);
    }
  };

  const fetchFavorites = async () => {
    try {
      const response = await fetch('/api/movies/favorites', {
        credentials: 'include'
      });
      
      const data = await response.json();
      setMovies(data.results || []);
    } catch (error) {
      console.error('Error fetching favorites:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveFavorite = async (movieId: number) => {
    try {
      const response = await fetch(`/api/movies/${movieId}/like`, {
        method: 'POST',
        credentials: 'include',
      });
      
      const data = await response.json();
      if (data.success) {
        // Quitar de la lista local
        setMovies(prev => prev.filter(movie => movie.id !== movieId));
      }
    } catch (error) {
      console.error('Error removing favorite:', error);
    }
  };

  const handleLogout = async () => {
    try {
      await fetch('/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
      window.location.href = '/welcome';
    } catch (error) {
      console.error('Error logging out:', error);
    }
  };

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      {/* Header */}
      <header className="fixed w-full z-50 flex justify-between items-center px-8 py-4 bg-gradient-to-b from-black/80 to-transparent">
        <div className="flex items-center space-x-4">
          <Link to="/" className="text-[#1db954] hover:text-[#1ed760] transition-colors">
            <ArrowLeft size={24} />
          </Link>
          <h1 className="text-3xl font-bold text-[#1db954] transition-transform duration-300 hover:scale-105 cursor-pointer">
            Mis Películas
          </h1>
        </div>
        
        <div className="relative">
          <button
            className="flex items-center space-x-2 hover:text-[#1db954] transition-colors"
            onMouseEnter={() => setShowUserMenu(true)}
            onMouseLeave={() => setShowUserMenu(false)}
          >
            <User size={24} />
            <span>{username}</span>
          </button>

          <AnimatePresence>
            {showUserMenu && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="absolute right-0 mt-2 w-48 bg-[#1a1a1a] rounded-md shadow-lg py-1"
                onMouseEnter={() => setShowUserMenu(true)}
                onMouseLeave={() => setShowUserMenu(false)}
              >
                <Link
                  to="/"
                  className="flex items-center w-full px-4 py-2 text-sm hover:bg-[#1db954] hover:text-white transition-colors"
                >
                  <Heart size={16} className="mr-2" />
                  Inicio
                </Link>
                <button
                  onClick={handleLogout}
                  className="flex items-center w-full px-4 py-2 text-sm hover:bg-[#1db954] hover:text-white transition-colors"
                >
                  <LogOut size={16} className="mr-2" />
                  Cerrar sesión
                </button>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </header>

      {/* Main Content */}
      <main className="pt-24 px-8">
        {loading ? (
          <div className="flex justify-center py-16">
            <div className="flex space-x-2">
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce"></div>
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
            </div>
          </div>
        ) : movies.length === 0 ? (
          <div className="text-center py-16">
            <Heart size={64} className="mx-auto text-gray-600 mb-4" />
            <h2 className="text-2xl font-bold text-gray-400 mb-2">No tienes películas favoritas</h2>
            <p className="text-gray-500 mb-6">Explora películas y agrégalas a tus favoritos</p>
            <Link
              to="/"
              className="inline-block bg-[#1db954] text-white font-bold py-3 px-6 rounded-full hover:bg-[#1ed760] transition-colors"
            >
              Explorar Películas
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            {movies.map((movie) => (
              <motion.div
                key={movie.id}
                className="relative aspect-[2/3] rounded-lg overflow-hidden"
                onHoverStart={() => setHoveredMovie(movie.id)}
                onHoverEnd={() => setHoveredMovie(null)}
                whileHover={{ scale: 1.05 }}
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

                <AnimatePresence>
                  {hoveredMovie === movie.id && (
                    <motion.div
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className="absolute inset-0 bg-black/70 p-4 flex flex-col justify-between"
                    >
                      <h3 className="text-lg font-semibold">{movie.title}</h3>
                      <p className="text-sm line-clamp-3">{movie.overview}</p>
                      <button
                        onClick={() => handleRemoveFavorite(movie.id)}
                        className="self-end p-2 rounded-full bg-red-600 hover:bg-red-700 transition-colors"
                        title="Quitar de favoritos"
                      >
                        <HeartOff size={20} />
                      </button>
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default MyMovies;