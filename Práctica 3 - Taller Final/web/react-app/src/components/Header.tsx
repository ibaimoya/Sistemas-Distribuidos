import React, { useState, useEffect } from 'react';
import { LogOut, Heart, User, ArrowLeft, Shield, Bell, UserPlus, Users, Check, X, Link as LinkIcon } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';

interface FriendRequest {
  id: number;
  remitente: {
    id: number;
    nombre: string;
    email: string;
  };
  fecha: string;
}

interface HeaderProps {
  title?: string;
  showBackButton?: boolean;
  backTo?: string;
  showNotifications?: boolean;
}

export default function Header({ 
  title = "Kinora", 
  showBackButton = false, 
  backTo = "/",
  showNotifications = false 
}: HeaderProps) {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [username, setUsername] = useState('Usuario');
  const [isAdmin, setIsAdmin] = useState(false);
  const [notificationCount, setNotificationCount] = useState(0);
  const [showNotificationPanel, setShowNotificationPanel] = useState(false);
  const [friendRequests, setFriendRequests] = useState<FriendRequest[]>([]);
  const [loadingRequests, setLoadingRequests] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    checkAuth();
    if (showNotifications) {
      fetchNotificationCount();
      const interval = setInterval(fetchNotificationCount, 30000);
      return () => clearInterval(interval);
    }
  }, [showNotifications]);

  const checkAuth = async () => {
    try {
      const response = await fetch('/auth/check', {
        credentials: 'include'
      });
      const data = await response.json();
      
      if (data.authenticated) {
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
    setShowNotificationPanel(!showNotificationPanel);
    if (!showNotificationPanel && notificationCount > 0) {
      fetchFriendRequests();
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
    <header className="fixed w-full z-50 bg-gradient-to-b from-black/95 via-black/80 to-transparent backdrop-blur-sm border-b border-white/5">
      <div className="flex justify-between items-center px-8 py-6">
        {/* Left side - Title with optional back button */}
        <div className="flex items-center space-x-4">
          {showBackButton && (
            <Link 
              to={backTo} 
              className="group flex items-center justify-center w-10 h-10 rounded-full bg-white/5 hover:bg-[#1db954]/20 transition-all duration-300 hover:scale-110"
            >
              <ArrowLeft size={20} className="text-white group-hover:text-[#1db954] transition-colors" />
            </Link>
          )}
          <div className="flex items-center space-x-3">
            <div className="w-2 h-8 bg-gradient-to-b from-[#1db954] to-[#1ed760] rounded-full"></div>
            <h1 className="text-3xl font-bold bg-gradient-to-r from-[#1db954] to-[#1ed760] bg-clip-text text-transparent hover:scale-105 transition-transform duration-300 cursor-pointer">
              {title}
            </h1>
          </div>
        </div>

        {/* Right side - Notifications and User menu */}
        <div className="flex items-center space-x-4">
          {/* Notifications */}
          {showNotifications && (
            <div className="relative">
              <button
                onClick={handleNotificationClick}
                className="relative group flex items-center justify-center w-10 h-10 rounded-full bg-white/5 hover:bg-[#1db954]/20 transition-all duration-300 hover:scale-110"
              >
                <Bell size={20} className={`transition-colors ${notificationCount > 0 ? 'text-[#1db954]' : 'text-white group-hover:text-[#1db954]'}`} />
                {notificationCount > 0 && (
                  <motion.span
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-semibold shadow-lg"
                  >
                    {notificationCount}
                  </motion.span>
                )}
              </button>

              <AnimatePresence>
                {showNotificationPanel && (
                  <motion.div
                    initial={{ opacity: 0, y: -10, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: -10, scale: 0.95 }}
                    className="absolute right-0 mt-3 w-80 bg-[#1a1a1a]/95 backdrop-blur-xl rounded-xl shadow-2xl border border-white/10 py-2 max-h-96 overflow-y-auto"
                    onMouseLeave={() => setShowNotificationPanel(false)}
                  >
                    <div className="px-4 py-3 border-b border-white/10">
                      <h3 className="font-semibold flex items-center text-white">
                        <UserPlus size={18} className="mr-2 text-[#1db954]" />
                        Solicitudes de amistad
                      </h3>
                    </div>
                    
                    {loadingRequests ? (
                      <div className="p-6 text-center">
                        <div className="w-6 h-6 border-2 border-[#1db954] border-t-transparent rounded-full animate-spin mx-auto"></div>
                      </div>
                    ) : friendRequests.length === 0 ? (
                      <div className="p-6 text-center text-gray-400">
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
                                <p className="font-medium text-white">{request.remitente.nombre}</p>
                                <p className="text-sm text-gray-400">
                                  {new Date(request.fecha).toLocaleDateString()}
                                </p>
                              </div>
                              <div className="flex space-x-2">
                                <button
                                  onClick={() => handleAcceptRequest(request.id)}
                                  className="p-2 bg-[#1db954] hover:bg-[#1ed760] rounded-full transition-colors shadow-lg"
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
          )}

          {/* User Menu */}
          <div className="relative">
            <button
              className="group flex items-center space-x-3 px-4 py-2 rounded-full bg-white/5 hover:bg-[#1db954]/20 transition-all duration-300 hover:scale-105"
              onMouseEnter={() => setShowUserMenu(true)}
              onMouseLeave={() => setShowUserMenu(false)}
            >
              <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#1db954] to-[#1ed760] flex items-center justify-center">
                <User size={16} className="text-white" />
              </div>
              <span className="text-white font-medium group-hover:text-[#1db954] transition-colors">{username}</span>
            </button>

            <AnimatePresence>
              {showUserMenu && (
                <motion.div
                  initial={{ opacity: 0, y: -10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: -10, scale: 0.95 }}
                  className="absolute right-0 mt-3 w-56 bg-[#1a1a1a]/95 backdrop-blur-xl rounded-xl shadow-2xl border border-white/10 py-2"
                  onMouseEnter={() => setShowUserMenu(true)}
                  onMouseLeave={() => setShowUserMenu(false)}
                >
                  <Link
                    to="/my-movies"
                    className="flex items-center w-full px-4 py-3 text-sm text-white hover:bg-[#1db954]/20 hover:text-[#1db954] transition-all duration-200 group"
                  >
                    <Heart size={16} className="mr-3 group-hover:scale-110 transition-transform" />
                    Mis Películas
                  </Link>
                  <Link
                    to="/friends"
                    className="flex items-center w-full px-4 py-3 text-sm text-white hover:bg-[#1db954]/20 hover:text-[#1db954] transition-all duration-200 group"
                    >
                    <Users size={16} className="mr-3 group-hover:scale-110 transition-transform" />
                    Amigos
                  </Link>
                  <Link
                    to="/blockchain"
                    className="flex items-center w-full px-4 py-3 text-sm text-white hover:bg-[#1db954]/20 hover:text-[#1db954] transition-all duration-200 group"
                  >
                    <LinkIcon size={16} className="mr-3 group-hover:scale-110 transition-transform" />
                    Blockchain
                  </Link>
                  {isAdmin && (
                    <Link
                      to="/admin"
                      className="flex items-center w-full px-4 py-3 text-sm text-white hover:bg-[#1db954]/20 hover:text-[#1db954] transition-all duration-200 group"
                    >
                      <Shield size={16} className="mr-3 group-hover:scale-110 transition-transform" />
                      Panel admin
                    </Link>
                  )}
                  <div className="border-t border-white/10 my-2"></div>
                  <button
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-3 text-sm text-white hover:bg-red-500/20 hover:text-red-400 transition-all duration-200 group"
                  >
                    <LogOut size={16} className="mr-3 group-hover:scale-110 transition-transform" />
                    Cerrar sesión
                  </button>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </header>
  );
}