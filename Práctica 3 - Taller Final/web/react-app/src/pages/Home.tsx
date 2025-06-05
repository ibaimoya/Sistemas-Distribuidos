import React, { useState, useEffect } from 'react';
import { LogOut, Heart, User } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

interface Movie {
  id: number;
  title: string;
  poster_path: string;
  overview: string;
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

  useEffect(() => {
    checkAuth();
    fetchMovies();
  }, []);

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
      } 
    } catch (error) {
      console.error('Error checking auth:', error);
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

  const handleLike = async (movieId: number) => {
    try {
      const response = await fetch(`/api/movies/${movieId}/like`, {
        method: 'POST',
        credentials: 'include',
      });
      
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
              className="relative aspect-[2/3] rounded-lg overflow-hidden"
              onHoverStart={() => setHoveredMovie(movie.id)}
              onHoverEnd={() => setHoveredMovie(null)}
              whileHover={{ scale: 1.05 }}
            >
              <img
                src={`https://image.tmdb.org/t/p/w500${movie.poster_path}`}
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
                      onClick={() => handleLike(movie.id)}
                      className="self-end p-2 rounded-full bg-[#1db954] hover:bg-[#1ed760] transition-colors"
                    >
                      <Heart size={20} />
                    </button>
                  </motion.div>
                )}
              </AnimatePresence>
            </motion.div>
          ))}
        </div>

        {/* Loading indicator */}
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
            No hay más películas que mostrar
          </div>
        )} 

      </main>
    </div>
  );
};

export default Home;