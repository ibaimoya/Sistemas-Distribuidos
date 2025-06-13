import React, { useState, useEffect } from 'react';
import { LogOut, Heart, User, X, Shield, Users, Bell, UserPlus, Check, Link as LinkIcon } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview: string;
}

interface FriendRequest {
  id: number;
  remitente: {
    id: number;
    nombre: string;
    email: string;
  };
  fecha: string;
}

const Home: React.FC = () => {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [username, setUsername] = useState('Usuario');
  const [hoveredMovie, setHoveredMovie] = useState<number | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [likedMovies, setLikedMovies] = useState<Set<number>>(new Set());
  const [animatingMovie, setAnimatingMovie] = useState<number | null>(null);
  const [feedbackType, setFeedbackType] = useState<'add' | 'remove' | null>(null);  
  const navigate = useNavigate();
  const [isAdmin, setIsAdmin] = useState(false);
  
  // Sistema de notificaciones
  const [notificationCount, setNotificationCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const [friendRequests, setFriendRequests] = useState<FriendRequest[]>([]);
  const [loadingRequests, setLoadingRequests] = useState(false);

  useEffect(() => {
    checkAuth();
    fetchMovies();
    loadUserFavorites();
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      fetchNotificationCount();
      const interval = setInterval(fetchNotificationCount, 30000); // Cada 30 segundos
      return () => clearInterval(interval);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    const handleScroll = () => {
      if (
        window.innerHeight + document.documentElement.scrollTop >= 
        document.documentElement.offsetHeight - 1000 &&
        hasMore && 
        !loading
      ) {
        const nextPage = page + 1;
        setPage(nextPage);
        fetchMovies(nextPage, true, searchQuery);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [page, hasMore, loading, searchQuery]);

  const checkAuth = async () => {
    try {
      const response = await fetch('/auth/check', {
        credentials: 'include'
      });
      const data = await response.json();
      
      if (data.authenticated) {
        setIsAuthenticated(true);
        setUsername(data.username);
        setIsAdmin(Boolean(data.admin));
      } 
    } catch (error) {
      console.error('Error checking auth:', error);
    }
  };

  const fetchNotificationCount = async () => {
    try {
      const response = await fetch('/api/friends/requests/count', {
        credentials: 'include'
      });
      const data = await response.json();
      setNotificationCount(data.count || 0);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  };

  const fetchFriendRequests = async () => {
    setLoadingRequests(true);
    try {
      const response = await fetch('/api/friends/requests/pending', {
        credentials: 'include'
      });
      const data = await response.json();
      setFriendRequests(data.requests || []);
    } catch (error) {
      console.error('Error fetching friend requests:', error);
    } finally {
      setLoadingRequests(false);
    }
  };

  const handleAcceptRequest = async (requestId: number) => {
    try {
      const response = await fetch(`/api/friends/requests/${requestId}/accept`, {
        method: 'POST',
        credentials: 'include'
      });
      
      if (response.ok) {
        // Actualizar la lista y el contador
        setFriendRequests(prev => prev.filter(r => r.id !== requestId));
        setNotificationCount(prev => Math.max(0, prev - 1));
        
        
      }
    } catch (error) {
      console.error('Error accepting request:', error);
    }
  };

  const handleRejectRequest = async (requestId: number) => {
    try {
      const response = await fetch(`/api/friends/requests/${requestId}/reject`, {
        method: 'POST',
        credentials: 'include'
      });
      
      if (response.ok) {
        setFriendRequests(prev => prev.filter(r => r.id !== requestId));
        setNotificationCount(prev => Math.max(0, prev - 1));
      }
    } catch (error) {
      console.error('Error rejecting request:', error);
    }
  };

  const handleNotificationClick = () => {
    setShowNotifications(!showNotifications);
    if (!showNotifications && notificationCount > 0) {
      fetchFriendRequests();
    }
  };

  const handleSearch = async (query: string) => {
    setSearchQuery(query);
    setIsSearching(query.length > 0);
    setPage(1);
    setHasMore(true);
    
    if (query.trim() === '') {
      fetchMovies(1, false, '');
    } else {
      fetchMovies(1, false, query);
    }
  };

  const fetchMovies = async (pageNumber = 1, append = false, query = '') => {
    if (loading) return;
    
    setLoading(true);
    try {
      const url = query 
        ? `/api/movies?query=${encodeURIComponent(query)}&page=${pageNumber}`
        : `/api/movies?page=${pageNumber}`;
        
      const response = await fetch(url, {
        credentials: 'include'
      });
      
      const data = await response.json();
      const newMovies = data.results || [];
      
      if (append) {
        setMovies(prev => [...prev, ...newMovies]);
      } else {
        setMovies(newMovies);
      }
      
      setHasMore(newMovies.length > 0 && pageNumber < (data.total_pages || 500));
      
    } catch (error) {
      console.error('Error fetching movies:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadUserFavorites = async () => {
    try {
      const response = await fetch('/api/movies/favorites', {
        credentials: 'include'
      });
      const data = await response.json();
      const favoriteIds = new Set(data.results?.map((movie: Movie) => movie.id) || []);
      setLikedMovies(favoriteIds);
    } catch (error) {
      console.error('Error loading favorites:', error);
    }
  };

  const showFeedback = (movieId: number, type: 'add' | 'remove') => {
    setAnimatingMovie(movieId);
    setFeedbackType(type);
    
    setTimeout(() => {
      setAnimatingMovie(null);
      setFeedbackType(null);
    }, 1200);
  };

  const handleLike = async (movieId: number) => {
    try {
      const response = await fetch(`/api/movies/${movieId}/like`, {
        method: 'POST',
        credentials: 'include',
      });
      
      const data = await response.json();
      if (data.success) {
        const isAdding = data.isLiked;
        
        showFeedback(movieId, isAdding ? 'add' : 'remove');
        
        setTimeout(() => {
          if (isAdding) {
            setLikedMovies(prev => new Set([...prev, movieId]));
          } else {
            setLikedMovies(prev => {
              const newSet = new Set(prev);
              newSet.delete(movieId);
              return newSet;
            });
          }
        }, 600);
      }
      
      if (response.status === 401) {
        window.location.href = '/login';
      }
    } catch (error) {
      console.error('Error liking movie:', error);
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
        <h1 className="text-3xl font-bold text-[#1db954] transition-transform duration-300 hover:scale-105 cursor-pointer">Kinora</h1>
        <div className="flex items-center space-x-4">
          {/* Notificaciones */}
          <div className="relative">
            <button
              onClick={handleNotificationClick}
              className="relative p-2 hover:bg-white/10 rounded-full transition-colors"
            >
              <Bell size={24} className={notificationCount > 0 ? 'text-[#1db954]' : 'text-white'} />
              {notificationCount > 0 && (
                <motion.span
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
                >
                  {notificationCount}
                </motion.span>
              )}
            </button>

            <AnimatePresence>
              {showNotifications && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="absolute right-0 mt-2 w-80 bg-[#1a1a1a] rounded-lg shadow-xl py-2 max-h-96 overflow-y-auto"
                  onMouseLeave={() => setShowNotifications(false)}
                >
                  <div className="px-4 py-2 border-b border-white/10">
                    <h3 className="font-semibold flex items-center">
                      <UserPlus size={18} className="mr-2 text-[#1db954]" />
                      Solicitudes de amistad
                    </h3>
                  </div>
                  
                  {loadingRequests ? (
                    <div className="p-4 text-center">
                      <div className="w-6 h-6 border-2 border-[#1db954] border-t-transparent rounded-full animate-spin mx-auto"></div>
                    </div>
                  ) : friendRequests.length === 0 ? (
                    <div className="p-4 text-center text-gray-400">
                      No tienes solicitudes pendientes
                    </div>
                  ) : (
                    <div className="py-2">
                      {friendRequests.map((request) => (
                        <motion.div
                          key={request.id}
                          initial={{ opacity: 0, x: -20 }}
                          animate={{ opacity: 1, x: 0 }}
                          className="px-4 py-3 hover:bg-white/5 transition-colors"
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex-1">
                              <p className="font-medium">{request.remitente.nombre}</p>
                              <p className="text-sm text-gray-400">
                                {new Date(request.fecha).toLocaleDateString()}
                              </p>
                            </div>
                            <div className="flex space-x-2">
                              <button
                                onClick={() => handleAcceptRequest(request.id)}
                                className="p-2 bg-[#1db954] hover:bg-[#1ed760] rounded-full transition-colors"
                              >
                                <Check size={16} />
                              </button>
                              <button
                                onClick={() => handleRejectRequest(request.id)}
                                className="p-2 bg-red-500/20 hover:bg-red-500/30 text-red-400 rounded-full transition-colors"
                              >
                                <X size={16} />
                              </button>
                            </div>
                          </div>
                        </motion.div>
                      ))}
                    </div>
                  )}
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Menu de usuario */}
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
                    to="/my-movies"
                    className="flex items-center w-full px-4 py-2 text-sm hover:bg-[#1db954] hover:text-white transition-colors"
                  >
                    <Heart size={16} className="mr-2" />
                    Mis Películas
                  </Link>
                  <Link
                    to="/blockchain"
                    className="flex items-center w-full px-4 py-2 text-sm hover:bg-[#1db954] hover:text-white transition-colors"
                  >
                    <LinkIcon size={16} className="mr-2" />
                    Blockchain
                  </Link>
                  {isAdmin && (
                    <Link
                      to="/admin"
                      className="flex items-center w-full px-4 py-2 text-sm hover:bg-[#1db954] hover:text-white transition-colors"
                    >
                      <Shield size={16} className="mr-2" />
                      Panel admin
                    </Link>
                  )}
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
        </div>
      </header>

      <div className="pt-24 pb-8 px-8">
        <div className="max-w-2xl mx-auto relative">
          <div className="relative">
            <input
              type="text"
              placeholder="Buscar películas..."
              value={searchQuery}
              onChange={(e) => handleSearch(e.target.value)}
              className="w-full px-6 py-4 bg-[#1a1a1a] text-white placeholder-gray-400 rounded-full border border-[#333] focus:outline-none focus:border-[#1db954] focus:ring-2 focus:ring-[#1db954]/20 transition-all duration-300 text-lg"
            />
            <div className="absolute right-4 top-1/2 transform -translate-y-1/2">
              <svg className="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          {isSearching && (
            <div className="mt-2 text-center text-sm text-[#1db954]">
              Buscando: "{searchQuery}"
            </div>
          )}
        </div>
      </div>
      
      {/* Main Content */}
      <main className="px-8">        
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
                onError={(e) => {
                  e.currentTarget.src = `data:image/svg+xml;base64,${btoa(`
                    <svg width="500" height="750" xmlns="http://www.w3.org/2000/svg">
                      <rect width="500" height="750" fill="#1a1a1a"/>
                      <text x="250" y="375" font-family="Arial" font-size="24" fill="#1db954" text-anchor="middle" dominant-baseline="middle">Sin Carátula</text>
                    </svg>
                  `)}`;
                }}
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
                            handleLike(movie.id);
                          }}
                          className={`relative self-end p-3 rounded-full transition-all duration-300 ${
                            likedMovies.has(movie.id) 
                              ? 'bg-red-500 hover:bg-red-600 shadow-lg shadow-red-500/50' 
                              : 'bg-[#1db954] hover:bg-[#1ed760] shadow-lg shadow-[#1db954]/30'
                          }`}
                        animate={{
                          scale: animatingMovie === movie.id ? [1, 1.5, 1] : 1,
                          rotate: animatingMovie === movie.id && feedbackType === 'add' ? [0, -15, 15, 0] : 0
                        }}
                        transition={{ duration: 0.8, ease: "easeOut" }}
                      >
                        <div className="relative">
                          {likedMovies.has(movie.id) ? (
                            <>
                              <Heart 
                                size={24} 
                                fill="white"
                                className="text-white"
                              />
                              <X 
                                size={16} 
                                className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-white stroke-[3]"
                              />
                            </>
                          ) : (
                            <Heart 
                              size={24} 
                              fill="none"
                              className="text-white stroke-2"
                            />
                          )}
                        </div>
                        
                        <AnimatePresence>
                          {animatingMovie === movie.id && feedbackType === 'add' && (
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
                                  initial={{ 
                                    scale: 0, 
                                    x: 0, 
                                    y: 0,
                                    opacity: 1 
                                  }}
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
                        
                        <AnimatePresence>
                          {animatingMovie === movie.id && feedbackType === 'remove' && (
                            <>
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
                              
                              <motion.div
                                className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
                                initial={{ opacity: 0, scale: 0 }}
                                animate={{ opacity: [0, 1, 0], scale: [0, 2, 2] }}
                                transition={{ duration: 1.0, ease: "easeOut" }}
                              >
                                <X size={32} className="text-gray-400 stroke-[3]" />
                              </motion.div>
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

        {loading && (
          <div className="flex justify-center py-8">
            <div className="flex space-x-2">
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce"></div>
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
              <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
            </div>
          </div>
        )}

        {!hasMore && movies.length > 0 && (
          <div className="text-center py-8 text-gray-400">
            ¡Has llegado al final! No hay más películas... por ahora.
          </div>
        )} 

      </main>
    </div>
  );
};

export default Home;