import React, { useState, useEffect } from 'react';
import { Heart, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import { Header } from '../components';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview: string;
}

const MyMovies: React.FC = () => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [hoveredMovie, setHoveredMovie] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [animatingMovie, setAnimatingMovie] = useState<number | null>(null);
  const [feedbackType, setFeedbackType] = useState<'remove' | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchFavorites();
  }, []);

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

  const showFeedback = (movieId: number) => {
    setAnimatingMovie(movieId);
    setFeedbackType('remove');
    
    setTimeout(() => {
      setAnimatingMovie(null);
      setFeedbackType(null);
    }, 1200);
  };

  const handleRemoveFavorite = async (movieId: number) => {
    try {
      const response = await fetch(`/api/movies/${movieId}/like`, {
        method: 'POST',
        credentials: 'include',
      });
      
      const data = await response.json();
      if (data.success) {
        // Primero mostrar la animación
        showFeedback(movieId);
        
        // Esperar a que termine la animación antes de quitar de la lista
        setTimeout(() => {
          setMovies(prev => prev.filter(movie => movie.id !== movieId));
        }, 1000);
      }
    } catch (error) {
      console.error('Error removing favorite:', error);
    }
  };

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      <Header title="Mis Películas" showBackButton={true} backTo="/" />

      {/* Main Content */}
      <main className="pt-32 px-8">
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
                className="relative aspect-[2/3] rounded-lg overflow-hidden cursor-pointer"
                onHoverStart={() => setHoveredMovie(movie.id)}
                onHoverEnd={() => setHoveredMovie(null)}
                whileHover={{ scale: 1.05 }}
                onClick={() => navigate(`/movie/${movie.id}`)}
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
                      <div className="relative">
                        <motion.button
                          onClick={(e) => {
                               e.stopPropagation();
                               handleRemoveFavorite(movie.id);
                          }}
                          className="relative self-end p-3 rounded-full bg-red-500 hover:bg-red-600 shadow-lg shadow-red-500/50 transition-all duration-300"
                          animate={{
                            scale: animatingMovie === movie.id ? [1, 1.5, 1] : 1,
                          }}
                          transition={{ duration: 0.8, ease: "easeOut" }}
                        >
                          <div className="relative">
                            {/* Corazón rojo tachado - todas están en favoritos */}
                            <Heart 
                              size={24} 
                              fill="white"
                              className="text-white"
                            />
                            <X 
                              size={16} 
                              className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-white stroke-[3]"
                            />
                          </div>
                          
                          {/* Efecto cuando se elimina */}
                          <AnimatePresence>
                            {animatingMovie === movie.id && (
                              <>
                                {/* Ondas de eliminación */}
                                {[...Array(4)].map((_, i) => (
                                  <motion.div
                                    key={`wave-${i}`}
                                    className="absolute top-1/2 left-1/2 border-3 border-gray-500 rounded-full"
                                    style={{
                                      width: '40px',
                                      height: '40px',
                                      marginTop: '-20px',
                                      marginLeft: '-20px'
                                    }}
                                    initial={{ scale: 0, opacity: 0.8 }}
                                    animate={{ 
                                      scale: [0, 4],
                                      opacity: [0.8, 0]
                                    }}
                                    transition={{ 
                                      duration: 1.2,
                                      delay: i * 0.2,
                                      ease: "easeOut"
                                    }}
                                  />
                                ))}
                                
                                {/* X grande flotante */}
                                <motion.div
                                  className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
                                  initial={{ opacity: 0, scale: 0 }}
                                  animate={{ opacity: [0, 1, 0], scale: [0, 2, 2] }}
                                  transition={{ duration: 1.0, ease: "easeOut" }}
                                >
                                  <X size={32} className="text-gray-400 stroke-[3]" />
                                </motion.div>
                                
                                {/* Partículas que se alejan */}
                                {[...Array(6)].map((_, i) => (
                                  <motion.div
                                    key={`particle-${i}`}
                                    className="absolute top-1/2 left-1/2"
                                    initial={{ 
                                      scale: 1, 
                                      x: 0, 
                                      y: 0,
                                      opacity: 1 
                                    }}
                                    animate={{ 
                                      scale: [1, 0],
                                      x: [0, (Math.cos(i * 60) * 80)],
                                      y: [0, (Math.sin(i * 60) * 80)],
                                      opacity: [1, 0]
                                    }}
                                    transition={{ 
                                      duration: 1.0,
                                      delay: i * 0.1,
                                      ease: "easeOut"
                                    }}
                                  >
                                    <div className="w-2 h-2 bg-gray-400 rounded-full" />
                                  </motion.div>
                                ))}
                              </>
                            )}
                          </AnimatePresence>
                        </motion.button>
                      </div>
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