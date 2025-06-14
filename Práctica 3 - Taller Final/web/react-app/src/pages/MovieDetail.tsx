import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Star, ArrowLeft, Clock, Calendar, Heart, X, Users, Shield } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import Map from "../components/Map";
import { COUNTRY_COORDS } from "../constants/countryCoords";
import { Header } from '../components';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview: string;
  release_date: string;
  runtime: number;
  vote_average: number;
  genres: { id: number; name: string }[];
}

interface RatingData {
  userRating: number;
  hasRated: boolean;
  averageRating: number;
  totalRatings: number;
}

const MovieDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [movie, setMovie] = useState<Movie | null>(null);
  const [loading, setLoading] = useState(true);
  const [userRating, setUserRating] = useState<number>(0);
  const [hoverRating, setHoverRating] = useState<number>(0);
  const [isLiked, setIsLiked] = useState(false);
  const [animatingLike, setAnimatingLike] = useState(false);
  const [feedbackType, setFeedbackType] = useState<'add' | 'remove' | null>(null);
  const [ratingData, setRatingData] = useState<RatingData>({
    userRating: 0,
    hasRated: false,
    averageRating: 0,
    totalRatings: 0
  });
  const [isRatingLoading, setIsRatingLoading] = useState(false);
  const [ratingMessage, setRatingMessage] = useState<string>('');
  const iso = movie?.production_countries?.[0]?.iso_3166_1;
  const coords = COUNTRY_COORDS[iso ?? ""] ?? { lat: 20, lng: 0 };

  const [blockHash, setBlockHash] = useState<string>('');

  useEffect(() => {
    fetchMovieDetails();
    checkIfLiked();
    loadUserRating();
  }, [id]);

  const fetchMovieDetails = async () => {
    try {
      const response = await fetch(`/api/movies/${id}`, {
        credentials: 'include'
      });
      const data = await response.json();
      setMovie(data);
    } catch (error) {
      console.error('Error fetching movie details:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkIfLiked = async () => {
    try {
      const response = await fetch('/api/movies/favorites', {
        credentials: 'include'
      });
      const data = await response.json();
      setIsLiked(data.results?.some((m: Movie) => m.id === Number(id)) || false);
    } catch (error) {
      console.error('Error checking if movie is liked:', error);
    }
  };

  const loadUserRating = async () => {
    try {
      const response = await fetch(`/api/movies/${id}/rating`, {
        credentials: 'include'
      });
      const data = await response.json();
      
      setRatingData(data);
      setUserRating(data.userRating);
      
    } catch (error) {
      console.error('Error loading user rating:', error);
    }
  };

  const handleRating = async (rating: number) => {
    if (isRatingLoading) return;
    
    setIsRatingLoading(true);
    setRatingMessage('');
    
    try {
      const response = await fetch(`/api/movies/${id}/rate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ rating }),
        credentials: 'include'
      });
      
      const data = await response.json();
      
      if (data.success) {
        setUserRating(rating);
        setRatingData({
          userRating: rating,
          hasRated: true,
          averageRating: data.averageRating,
          totalRatings: data.totalRatings
        });
        
        // Guardar el hash del bloque
        if (data.blockHash) {
          setBlockHash(data.blockHash);
        }
        
        // Mostrar mensaje de confirmación
        if (data.action === 'updated') {
          setRatingMessage('¡Valoración actualizada!');
        } else {
          setRatingMessage('¡Valoración guardada!');
        }
        
        // Limpiar mensaje después de 5 segundos para que se vea el hash
        setTimeout(() => setRatingMessage(''), 5000);
      } else {
        setRatingMessage('Error al guardar valoración');
        setTimeout(() => setRatingMessage(''), 3000);
      }
    } catch (error) {
      console.error('Error rating movie:', error);
      setRatingMessage('Error de conexión');
      setTimeout(() => setRatingMessage(''), 3000);
    } finally {
      setIsRatingLoading(false);
    }
  };

  const handleLike = async () => {
    try {
      const response = await fetch(`/api/movies/${id}/like`, {
        method: 'POST',
        credentials: 'include',
      });
      
      const data = await response.json();
      if (data.success) {
        const newLikeState = !isLiked;
        setAnimatingLike(true);
        setFeedbackType(newLikeState ? 'add' : 'remove');

        setTimeout(() => {
          setIsLiked(newLikeState);
          setAnimatingLike(false);
          setFeedbackType(null);
        }, 600);
      }
    } catch (error) {
      console.error('Error toggling like:', error);
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

  if (!movie) {
    return (
      <div className="min-h-screen bg-[#111111] flex justify-center items-center text-white">
        <p>Película no encontrada</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      <Header 
        title={movie.title} 
        showBackButton={true} 
        backTo="/" 
        showNotifications={true} 
      />

      <main className="pt-32 px-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row gap-8">
            {/* Poster */}
            <div className="w-full md:w-1/3">
              <motion.div 
                className="relative rounded-lg overflow-hidden shadow-2xl"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
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
                  className="w-full aspect-[2/3] object-cover"
                />
              </motion.div>
            </div>

            {/* Movie Info */}
            <div className="flex-1">
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: 0.2 }}
              >
                <div className="flex justify-between items-start mb-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-4 text-gray-400 mb-4">
                      <div className="flex items-center">
                        <Clock size={16} className="mr-1" />
                        <span>{movie.runtime} min</span>
                      </div>
                      <div className="flex items-center">
                        <Calendar size={16} className="mr-1" />
                        <span>{new Date(movie.release_date).getFullYear()}</span>
                      </div>
                    </div>
                  </div>
                  <motion.button
                    onClick={handleLike}
                    className={`relative p-3 rounded-full transition-all duration-300 ${
                      isLiked 
                        ? 'bg-red-500 hover:bg-red-600 shadow-lg shadow-red-500/50' 
                        : 'bg-[#1db954] hover:bg-[#1ed760] shadow-lg shadow-[#1db954]/30'
                    }`}
                    animate={{
                      scale: animatingLike ? [1, 1.5, 1] : 1,
                      rotate: animatingLike && feedbackType === 'add' ? [0, -15, 15, 0] : 0
                    }}
                    transition={{ duration: 0.8, ease: "easeOut" }}
                  >
                    <div className="relative">
                      {isLiked ? (
                        <>
                          <Heart size={24} fill="white" className="text-white" />
                          <X 
                            size={16} 
                            className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-white stroke-[3]"
                          />
                        </>
                      ) : (
                        <Heart size={24} className="text-white" />
                      )}
                    </div>

                    <AnimatePresence>
                      {animatingLike && feedbackType === 'add' && (
                        <>
                          <motion.div
                            className="absolute top-1/2 left-1/2 border-4 border-red-400 rounded-full"
                            style={{
                              width: '50px',
                              height: '50px',
                              marginTop: '-25px',
                              marginLeft: '-25px'
                            }}
                            initial={{ scale: 0, opacity: 1 }}
                            animate={{ scale: [0, 2, 3], opacity: [1, 0.5, 0] }}
                            transition={{ duration: 1.0, ease: "easeOut" }}
                          />
                          
                          {[...Array(8)].map((_, i) => (
                            <motion.div
                              key={i}
                              className="absolute top-1/2 left-1/2"
                              initial={{ scale: 0, x: 0, y: 0, opacity: 1 }}
                              animate={{ 
                                scale: [0, 1, 0],
                                x: [0, (Math.cos(i * 45) * 60)],
                                y: [0, (Math.sin(i * 45) * 60)],
                                opacity: [1, 1, 0]
                              }}
                              transition={{ 
                                duration: 1.0,
                                delay: i * 0.1,
                                ease: "easeOut"
                              }}
                            >
                              <Heart size={12} fill="red" className="text-red-400" />
                            </motion.div>
                          ))}
                        </>
                      )}
                    </AnimatePresence>
                  </motion.button>
                </div>

                <div className="flex flex-wrap gap-2 mb-6">
                  {movie.genres.map(genre => (
                    <span 
                      key={genre.id}
                      className="px-3 py-1 bg-[#1db954]/10 text-[#1db954] rounded-full text-sm"
                    >
                      {genre.name}
                    </span>
                  ))}
                </div>

                <p className="text-gray-300 mb-8 leading-relaxed">
                  {movie.overview}
                </p>

                {/* Rating Section */}
                <div className="bg-[#1a1a1a] rounded-lg p-6 mb-6">
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="text-xl font-semibold">Tu valoración</h3>
                    {ratingData.hasRated && (
                      <span className="text-sm text-[#1db954]">
                        Ya has valorado esta película
                      </span>
                    )}
                  </div>
                  
                  <div className="flex items-center gap-2 mb-4">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <motion.button
                        key={star}
                        whileHover={{ scale: 1.2 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => handleRating(star)}
                        onMouseEnter={() => setHoverRating(star)}
                        onMouseLeave={() => setHoverRating(0)}
                        className="relative"
                        disabled={isRatingLoading}
                      >
                        <Star
                          size={32}
                          className={`transition-colors duration-200 ${
                            (hoverRating || userRating) >= star
                              ? 'text-yellow-400 fill-yellow-400'
                              : 'text-gray-600'
                          } ${isRatingLoading ? 'opacity-50' : ''}`}
                        />
                        {userRating === star && !isRatingLoading && (
                          <motion.div
                            className="absolute inset-0 rounded-full"
                            initial={{ scale: 0 }}
                            animate={{ scale: [0, 1.2, 1] }}
                            transition={{ duration: 0.3 }}
                          />
                        )}
                      </motion.button>
                    ))}
                    <span className="ml-4 text-2xl font-bold text-yellow-400">
                      {userRating > 0 ? userRating : ''}
                    </span>
                    {isRatingLoading && (
                      <div className="ml-2 w-4 h-4 border-2 border-[#1db954] border-t-transparent rounded-full animate-spin"></div>
                    )}
                  </div>

                  {/* Mensaje de confirmación */}
                  <AnimatePresence>
                    {ratingMessage && (
                      <motion.div
                        initial={{ opacity: 0, y: -10 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -10 }}
                        className={`text-sm mt-2 ${
                          ratingMessage.includes('Error') ? 'text-red-400' : 'text-[#1db954]'
                        }`}
                      >
                        {ratingMessage}
                      </motion.div>
                    )}
                  </AnimatePresence>

                  {/* Mostrar hash del bloque si existe */}
                  {blockHash && (
                    <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="mt-4 p-3 bg-[#1db954]/10 rounded-lg border border-[#1db954]/20"
                    >
                      <div className="flex items-center space-x-2 mb-1">
                        <Shield size={16} className="text-[#1db954]" />
                        <span className="text-xs font-semibold text-[#1db954]">Registrado en Blockchain</span>
                      </div>
                      <p className="text-xs text-gray-400 break-all font-mono">
                        Hash: {blockHash.substring(0, 32)}...
                      </p>
                    </motion.div>
                  )}
                </div>

                {/* Community Rating */}
                <div className="bg-[#1a1a1a] rounded-lg p-6 mb-6">
                  <h3 className="text-xl font-semibold mb-4">Valoración de la comunidad</h3>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <Star size={24} className="text-yellow-400 fill-yellow-400 mr-2" />
                      <span className="text-2xl font-bold">
                        {ratingData.averageRating > 0 ? ratingData.averageRating : 'Sin valorar'}
                      </span>
                      {ratingData.averageRating > 0 && (
                        <span className="text-gray-400 ml-2">/ 5</span>
                      )}
                    </div>
                    <div className="flex items-center text-gray-400">
                      <Users size={20} className="mr-2" />
                      <span>{ratingData.totalRatings} valoraciones</span>
                    </div>
                  </div>
                </div>

                {/* TMDB Rating */}
                <div className="bg-[#1a1a1a] rounded-lg p-6">
                  <h3 className="text-xl font-semibold mb-4">Valoración TMDB</h3>
                  <div className="flex items-center">
                    <div className="flex items-center">
                      <Star size={24} className="text-yellow-400 fill-yellow-400 mr-2" />
                      <span className="text-2xl font-bold">{movie.vote_average.toFixed(1)}</span>
                    </div>
                    <span className="text-gray-400 ml-2">/ 10</span>
                  </div>
                </div>
                  {iso && (
                    <div className="bg-[#1a1a1a] rounded-lg p-6 mt-8">
                      <h2 className="text-xl font-semibold mb-4">Dónde se produjo</h2>
                      <Map center={coords} height={250} />
                    </div>
                  )}
              </motion.div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default MovieDetail;