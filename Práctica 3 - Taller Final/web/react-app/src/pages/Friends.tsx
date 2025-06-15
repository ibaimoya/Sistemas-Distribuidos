import React, { useState, useEffect } from 'react';
import { 
  UserPlus, 
  Users, 
  Search, 
  Clock, 
  CheckCircle, 
  XCircle,
  MessageCircle,
  Trash2,
  Send,
  X,
  Bell,
  UserX
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Header } from '../components';

interface Friend {
  id: number;
  nombre: string;
  email: string;
  fechaAmistad: string;
}

interface SentRequest {
  id: number;
  destinatario: {
    id: number;
    nombre: string;
    email: string;
  };
  estado: string;
  fecha: string;
}

const Friends: React.FC = () => {
  const [friends, setFriends] = useState<Friend[]>([]);
  const [sentRequests, setSentRequests] = useState<SentRequest[]>([]);
  const [pendingRequestsCount, setPendingRequestsCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [searchUsername, setSearchUsername] = useState('');
  const [showAddFriend, setShowAddFriend] = useState(false);
  const [sendingRequest, setSendingRequest] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);
  const [activeTab, setActiveTab] = useState<'friends' | 'sent'>('friends');
  const navigate = useNavigate();

  useEffect(() => {
    fetchFriends();
    fetchSentRequests();
    fetchPendingRequestsCount();
  }, []);

  const fetchFriends = async () => {
    try {
      const response = await fetch('/api/friends', {
        credentials: 'include'
      });
      const data = await response.json();
      setFriends(data.friends || []);
    } catch (error) {
      console.error('Error fetching friends:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchSentRequests = async () => {
    try {
      const response = await fetch('/api/friends/requests/sent', {
        credentials: 'include'
      });
      const data = await response.json();
      setSentRequests(data.requests || []);
    } catch (error) {
      console.error('Error fetching sent requests:', error);
    }
  };

  const fetchPendingRequestsCount = async () => {
    try {
      const response = await fetch('/api/friends/requests/count', {
        credentials: 'include'
      });
      const data = await response.json();
      setPendingRequestsCount(data.count || 0);
    } catch (error) {
      console.error('Error fetching pending requests count:', error);
    }
  };

  const sendFriendRequest = async () => {
    if (!searchUsername.trim()) {
      setMessage({ type: 'error', text: 'Por favor, introduce un nombre de usuario' });
      return;
    }

    setSendingRequest(true);
    setMessage(null);

    try {
      const response = await fetch('/api/friends/request', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ username: searchUsername })
      });

      const data = await response.json();

      if (data.success) {
        setMessage({ type: 'success', text: data.message });
        setSearchUsername('');
        setShowAddFriend(false);
        fetchSentRequests();
      } else {
        setMessage({ type: 'error', text: data.message });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Error al enviar la solicitud' });
    } finally {
      setSendingRequest(false);
    }
  };

  const cancelRequest = async (requestId: number) => {
    try {
      const response = await fetch(`/api/friends/requests/${requestId}`, {
        method: 'DELETE',
        credentials: 'include'
      });

      if (response.ok) {
        setSentRequests(prev => prev.filter(r => r.id !== requestId));
        setMessage({ type: 'success', text: 'Solicitud cancelada' });
      }
    } catch (error) {
      console.error('Error canceling request:', error);
    }
  };

  const removeFriend = async (friendId: number) => {
    if (!confirm('¿Estás seguro de que quieres eliminar a este amigo?')) return;

    try {
      const response = await fetch(`/api/friends/${friendId}`, {
        method: 'DELETE',
        credentials: 'include'
      });

      if (response.ok) {
        setFriends(prev => prev.filter(f => f.id !== friendId));
        setMessage({ type: 'success', text: 'Amigo eliminado' });
      }
    } catch (error) {
      console.error('Error removing friend:', error);
    }
  };

  const getStatusIcon = (estado: string) => {
    switch (estado) {
      case 'PENDIENTE':
        return <Clock size={16} className="text-yellow-500" />;
      case 'ACEPTADA':
        return <CheckCircle size={16} className="text-green-500" />;
      case 'RECHAZADA':
        return <XCircle size={16} className="text-red-500" />;
      default:
        return null;
    }
  };

  const getStatusText = (estado: string) => {
    switch (estado) {
      case 'PENDIENTE':
        return 'Pendiente';
      case 'ACEPTADA':
        return 'Aceptada';
      case 'RECHAZADA':
        return 'Rechazada';
      default:
        return estado;
    }
  };

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      <Header 
        title="Amigos" 
        showBackButton={true} 
        backTo="/" 
        showNotifications={true} 
      />

      <main className="pt-32 px-8 pb-8">
        <div className="max-w-6xl mx-auto">
          {/* Add Friend Button */}
          <div className="flex justify-end mb-8">
            <button
              onClick={() => setShowAddFriend(true)}
              className="flex items-center space-x-2 px-6 py-3 bg-[#1db954] hover:bg-[#1ed760] rounded-full transition-all duration-200 hover:scale-105 shadow-lg shadow-[#1db954]/30"
            >
              <UserPlus size={20} />
              <span className="font-medium">Añadir amigo</span>
            </button>
          </div>

          {/* Mensaje de feedback */}
          <AnimatePresence>
            {message && (
              <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                className={`mb-6 p-4 rounded-lg ${
                  message.type === 'success' 
                    ? 'bg-green-500/20 text-green-400 border border-green-500/50' 
                    : 'bg-red-500/20 text-red-400 border border-red-500/50'
                }`}
              >
                {message.text}
              </motion.div>
            )}
          </AnimatePresence>

          {/* Tabs */}
          <div className="flex space-x-1 mb-8 bg-[#1a1a1a] p-1 rounded-lg w-fit">
            <button
              onClick={() => setActiveTab('friends')}
              className={`px-6 py-3 rounded-md transition-all font-medium ${
                activeTab === 'friends'
                  ? 'bg-[#1db954] text-white shadow-lg'
                  : 'text-gray-400 hover:text-white hover:bg-white/5'
              }`}
            >
              <span className="flex items-center space-x-2">
                <Users size={18} />
                <span>Amigos ({friends.length})</span>
                {pendingRequestsCount > 0 && (
                  <span className="ml-2 bg-red-500 text-white text-xs rounded-full px-2 py-0.5 font-semibold">
                    {pendingRequestsCount} pendientes
                  </span>
                )}
              </span>
            </button>
            <button
              onClick={() => setActiveTab('sent')}
              className={`px-6 py-3 rounded-md transition-all font-medium ${
                activeTab === 'sent'
                  ? 'bg-[#1db954] text-white shadow-lg'
                  : 'text-gray-400 hover:text-white hover:bg-white/5'
              }`}
            >
              <span className="flex items-center space-x-2">
                <Send size={18} />
                <span>Enviadas ({sentRequests.filter(r => r.estado === 'PENDIENTE').length})</span>
              </span>
            </button>
          </div>

          {/* Contenido de tabs */}
          {loading ? (
            <div className="flex justify-center py-16">
              <div className="flex space-x-2">
                <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce"></div>
                <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
                <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
              </div>
            </div>
          ) : (
            <>
              {/* Lista de amigos */}
              {activeTab === 'friends' && (
                <div>
                  {pendingRequestsCount > 0 && (
                    <motion.div
                      initial={{ opacity: 0, y: -10 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="mb-6 p-4 bg-[#1db954]/20 border border-[#1db954]/50 rounded-lg flex items-center justify-between"
                    >
                      <div className="flex items-center space-x-3">
                        <Bell size={20} className="text-[#1db954]" />
                        <p className="text-[#1db954] font-medium">
                          Tienes {pendingRequestsCount} {pendingRequestsCount === 1 ? 'solicitud pendiente' : 'solicitudes pendientes'} de amistad
                        </p>
                      </div>
                    </motion.div>
                  )}

                  {friends.length === 0 ? (
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="text-center py-16"
                    >
                      <div className="w-24 h-24 rounded-full bg-[#1a1a1a] flex items-center justify-center mx-auto mb-6">
                        <UserX size={48} className="text-gray-600" />
                      </div>
                      <h2 className="text-3xl font-bold text-gray-400 mb-4">No tienes amigos todavía</h2>
                      <p className="text-gray-500 mb-8 max-w-md mx-auto">
                        ¡Conecta con otros usuarios y comparte tus películas favoritas! Busca amigos por su nombre de usuario.
                      </p>
                      <button
                        onClick={() => setShowAddFriend(true)}
                        className="inline-flex items-center space-x-2 px-6 py-3 bg-[#1db954] hover:bg-[#1ed760] rounded-full transition-all duration-200 hover:scale-105"
                      >
                        <UserPlus size={20} />
                        <span className="font-medium">Añadir tu primer amigo</span>
                      </button>
                    </motion.div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                      {friends.map((friend) => (
                        <motion.div
                          key={friend.id}
                          initial={{ opacity: 0, scale: 0.9 }}
                          animate={{ opacity: 1, scale: 1 }}
                          className="bg-[#1a1a1a] rounded-xl p-6 hover:bg-[#222] transition-all duration-200 border border-white/10 hover:border-[#1db954]/30"
                        >
                          <div className="flex items-center justify-between mb-4">
                            <div className="flex items-center space-x-4">
                              <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#1db954] to-[#1ed760] flex items-center justify-center">
                                <span className="text-lg font-bold text-white">
                                  {friend.nombre.charAt(0).toUpperCase()}
                                </span>
                              </div>
                              <div>
                                <h3 className="font-semibold text-lg">{friend.nombre}</h3>
                                <p className="text-sm text-gray-400">{friend.email}</p>
                              </div>
                            </div>
                          </div>

                          <p className="text-xs text-gray-500 mb-4">
                            Amigos desde el {new Date(friend.fechaAmistad).toLocaleDateString('es-ES', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric'
                            })}.
                          </p>

                            <div className="flex space-x-2">
                            <button
                                onClick={() => {
                                // Redirigir a home con el chat abierto para este amigo.
                                    navigate(`/?chat=${friend.id}`);
                                }}
                                className="flex-1 flex items-center justify-center space-x-2 py-2.5 bg-[#1db954]/10 text-[#1db954] rounded-lg hover:bg-[#1db954]/20 transition-all duration-200"
                            >
                                <MessageCircle size={16} />
                                <span className="text-sm font-medium">Abrir chat</span>
                            </button>
                            <button
                                onClick={() => removeFriend(friend.id)}
                                className="p-2.5 text-red-400 hover:bg-red-500/10 rounded-lg transition-colors hover:scale-110"
                                title="Eliminar amigo"
                            >
                                <Trash2 size={16} />
                            </button>
                            </div>
                        </motion.div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {/* Solicitudes enviadas */}
              {activeTab === 'sent' && (
                <div>
                  {sentRequests.length === 0 ? (
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="text-center py-16"
                    >
                      <div className="w-24 h-24 rounded-full bg-[#1a1a1a] flex items-center justify-center mx-auto mb-6">
                        <Send size={48} className="text-gray-600" />
                      </div>
                      <h2 className="text-3xl font-bold text-gray-400 mb-4">No has enviado solicitudes</h2>
                      <p className="text-gray-500 mb-8 max-w-md mx-auto">
                        Cuando envíes solicitudes de amistad, aparecerán aquí para que puedas ver su estado.
                      </p>
                    </motion.div>
                  ) : (
                    <div className="space-y-4">
                      {sentRequests.map((request) => (
                        <motion.div
                          key={request.id}
                          initial={{ opacity: 0, x: -20 }}
                          animate={{ opacity: 1, x: 0 }}
                          className="bg-[#1a1a1a] rounded-xl p-6 flex items-center justify-between border border-white/10 hover:border-white/20 transition-colors"
                        >
                          <div className="flex items-center space-x-4">
                            <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#1db954] to-[#1ed760] flex items-center justify-center">
                              <span className="text-white font-bold text-lg">
                                {request.destinatario.nombre.charAt(0).toUpperCase()}
                              </span>
                            </div>
                            <div>
                              <h3 className="font-semibold text-lg">{request.destinatario.nombre}</h3>
                              <p className="text-sm text-gray-400">{request.destinatario.email}</p>
                              <p className="text-xs text-gray-500 mt-1">
                                Enviado el {new Date(request.fecha).toLocaleDateString('es-ES')}
                              </p>
                            </div>
                          </div>

                          <div className="flex items-center space-x-4">
                            <div className="flex items-center space-x-2 px-3 py-1.5 rounded-full bg-white/10">
                              {getStatusIcon(request.estado)}
                              <span className="text-sm font-medium">{getStatusText(request.estado)}</span>
                            </div>

                            {request.estado === 'PENDIENTE' && (
                              <button
                                onClick={() => cancelRequest(request.id)}
                                className="text-red-400 hover:text-red-300 hover:bg-red-500/10 p-2 rounded-lg transition-all duration-200"
                                title="Cancelar solicitud"
                              >
                                <X size={20} />
                              </button>
                            )}
                          </div>
                        </motion.div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </>
          )}
        </div>
      </main>

      {/* Modal añadir amigo */}
      <AnimatePresence>
        {showAddFriend && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-4"
            onClick={() => setShowAddFriend(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-[#1a1a1a] rounded-xl p-8 max-w-md w-full border border-white/20"
              onClick={(e) => e.stopPropagation()}
            >
              <h2 className="text-2xl font-bold mb-6 text-[#1db954]">Añadir amigo</h2>
              
              <div className="mb-6">
                <label className="block text-sm font-medium mb-3 text-gray-300">
                  Nombre de usuario
                </label>
                <div className="relative">
                  <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    value={searchUsername}
                    onChange={(e) => setSearchUsername(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && sendFriendRequest()}
                    placeholder="Introduce el nombre de usuario..."
                    className="w-full pl-10 pr-4 py-3 bg-[#111] border border-[#333] rounded-lg text-white focus:outline-none focus:border-[#1db954] focus:ring-2 focus:ring-[#1db954]/20 transition-all"
                    disabled={sendingRequest}
                  />
                </div>
              </div>

              <div className="flex space-x-3">
                <button
                  onClick={sendFriendRequest}
                  disabled={sendingRequest || !searchUsername.trim()}
                  className="flex-1 py-3 bg-[#1db954] hover:bg-[#1ed760] text-white rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2 font-medium shadow-lg hover:shadow-[#1db954]/30"
                >
                  {sendingRequest ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      <span>Enviando...</span>
                    </>
                  ) : (
                    <>
                      <UserPlus size={20} />
                      <span>Enviar solicitud</span>
                    </>
                  )}
                </button>
                <button
                  onClick={() => {
                    setShowAddFriend(false);
                    setSearchUsername('');
                    setMessage(null);
                  }}
                  className="px-6 py-3 bg-[#333] hover:bg-[#444] text-white rounded-lg transition-colors font-medium"
                >
                  Cancelar
                </button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default Friends;