import React, { useState, useEffect } from 'react';
import { MessageCircle, X, Circle, Search } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import ChatWindow from './ChatWindow';
import WebSocketService from '../../services/WebSocketService';
import { useNavigate, useLocation } from 'react-router-dom';

interface Friend {
  id: number;
  nombre: string;
  email: string;
}

interface Conversation {
  friendId: number;
  friendName: string;
  lastMessage: string;
  lastMessageTime: string;
  unreadCount: number;
  isLastMessageFromMe: boolean;
}

interface ChatProps {
  currentUserId: number;
}

const Chat: React.FC<ChatProps> = ({ currentUserId }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [friends, setFriends] = useState<Friend[]>([]);
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedFriend, setSelectedFriend] = useState<Friend | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [totalUnread, setTotalUnread] = useState(0);
  const [isConnected, setIsConnected] = useState(false);
  const [connectionError, setConnectionError] = useState<string>('');
  const navigate = useNavigate();
  const location = useLocation();

  /* Conecta el WebSocket y carga datos iniciales. */
  useEffect(() => {
    WebSocketService.connect(currentUserId)
      .then(() => {
        setIsConnected(true);
        setConnectionError('');
      })
      .catch(err => {
        setConnectionError('Error al conectar con el chat');
        console.error('WebSocket connection error:', err);
      });

    fetchFriends();
    fetchConversations();
    fetchUnreadCount();

    return () => WebSocketService.disconnect();
  }, [currentUserId]);

  /* Obtiene la lista de amigos. */
  const fetchFriends = async () => {
    try {
      const response = await fetch('/api/friends', { credentials: 'include' });
      const data = await response.json();
      setFriends(data.friends || []);
    } catch (error) {
      console.error('Error fetching friends:', error);
    }
  };

  /* Abre automáticamente el chat si la URL trae ?chat=ID. */
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const chatId = params.get('chat');
    if (!chatId || friends.length === 0) return;

    const friendToOpen = friends.find(f => f.id === Number(chatId));
    if (friendToOpen) {
      handleSelectFriend(friendToOpen);
      setIsOpen(true);
    }

    params.delete('chat');
    navigate({ pathname: '/', search: params.toString() }, { replace: true });
  }, [location.search, friends]);

  /* Obtiene las conversaciones. */
  const fetchConversations = async () => {
    try {
      const response = await fetch('/api/chat/conversations', { credentials: 'include' });
      const data = await response.json();
      setConversations(data || []);
    } catch (error) {
      console.error('Error fetching conversations:', error);
    }
  };

  /* Obtiene el total de mensajes no leídos. */
  const fetchUnreadCount = async () => {
    try {
      const response = await fetch('/api/chat/unread-count', { credentials: 'include' });
      const data = await response.json();
      setTotalUnread(data.count || 0);
    } catch (error) {
      console.error('Error fetching unread count:', error);
    }
  };

  /* Selecciona un amigo y reinicia su contador de no leídos. */
  const handleSelectFriend = (friend: Friend) => {
    setSelectedFriend(friend);

    setConversations(prev =>
      prev.map(conv =>
        conv.friendId === friend.id ? { ...conv, unreadCount: 0 } : conv
      )
    );

    const newTotal = conversations.reduce(
      (sum, conv) => (conv.friendId === friend.id ? sum : sum + conv.unreadCount),
      0
    );
    setTotalUnread(newTotal);
  };

  const handleCloseChat = () => setSelectedFriend(null);

  const handleNewMessage = (friendId: number) => {
    fetchConversations();
    fetchUnreadCount();
  };

  const filteredFriends = friends.filter(friend =>
    friend.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  /* Combina la info de amigos y conversaciones. */
  const friendsWithConversations = filteredFriends
    .map(friend => {
      const conversation = conversations.find(c => c.friendId === friend.id);
      return {
        ...friend,
        lastMessage: conversation?.lastMessage,
        lastMessageTime: conversation?.lastMessageTime,
        unreadCount: conversation?.unreadCount || 0,
        isLastMessageFromMe: conversation?.isLastMessageFromMe || false
      };
    })
    .sort((a, b) => {
      if (!a.lastMessageTime && !b.lastMessageTime) return 0;
      if (!a.lastMessageTime) return 1;
      if (!b.lastMessageTime) return -1;
      return (
        new Date(b.lastMessageTime).getTime() -
        new Date(a.lastMessageTime).getTime()
      );
    });

  return (
    <>
      {/* Botón flotante del chat */}
      <motion.button
        onClick={() => setIsOpen(!isOpen)}
        className="fixed bottom-6 right-6 w-14 h-14 bg-[#1db954] hover:bg-[#1ed760] rounded-full shadow-lg flex items-center justify-center z-40 transition-all duration-200 hover:scale-110"
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.95 }}
      >
        <MessageCircle size={24} className="text-white" />
        {totalUnread > 0 && (
          <motion.span
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-6 h-6 flex items-center justify-center font-bold"
          >
            {totalUnread > 99 ? '99+' : totalUnread}
          </motion.span>
        )}
      </motion.button>

      {/* Panel de chat */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            className="fixed bottom-24 right-6 w-96 h-[600px] bg-[#1a1a1a] rounded-xl shadow-2xl border border-white/10 overflow-hidden z-40 flex flex-col"
          >
            {/* Header */}
            <div className="bg-[#111] p-4 border-b border-white/10">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center space-x-3">
                  <h2 className="text-xl font-bold text-white">Chat</h2>
                  <div className="flex items-center space-x-1">
                    <Circle
                      size={8}
                      className={`${isConnected ? 'text-green-500' : 'text-red-500'} fill-current`}
                    />
                    <span className="text-xs text-gray-400">
                      {isConnected ? 'Conectado' : 'Desconectado'}
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => setIsOpen(false)}
                  className="text-gray-400 hover:text-white transition-colors"
                >
                  <X size={20} />
                </button>
              </div>

              {/* Barra de búsqueda */}
              <div className="relative">
                <Search
                  size={18}
                  className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
                />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  placeholder="Buscar amigos..."
                  className="w-full pl-10 pr-4 py-2 bg-[#222] border border-[#333] rounded-lg text-white text-sm focus:outline-none focus:border-[#1db954] transition-all"
                />
              </div>
            </div>

            {/* Lista de amigos o ventana de chat */}
            <div className="flex-1 overflow-hidden">
              {selectedFriend ? (
                <ChatWindow
                  friend={selectedFriend}
                  currentUserId={currentUserId}
                  onClose={handleCloseChat}
                  onNewMessage={() => handleNewMessage(selectedFriend.id)}
                />
              ) : (
                <div className="h-full overflow-y-auto">
                  {connectionError && (
                    <div className="p-4 bg-red-500/10 text-red-400 text-sm text-center">
                      {connectionError}
                    </div>
                  )}

                  {friendsWithConversations.length === 0 ? (
                    <div className="text-center py-16 text-gray-400">
                      <MessageCircle size={48} className="mx-auto mb-4 opacity-50" />
                      <p>No tienes amigos para chatear</p>
                      <p className="text-sm mt-2">Añade amigos para empezar</p>
                    </div>
                  ) : (
                    <div className="divide-y divide-white/10">
                      {friendsWithConversations.map(friend => (
                        <motion.button
                          key={friend.id}
                          onClick={() => handleSelectFriend(friend)}
                          className="w-full p-4 hover:bg-white/5 transition-colors text-left group"
                          whileHover={{ x: 4 }}
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-3 flex-1 min-w-0">
                              <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[#1db954] to-[#1ed760] flex items-center justify-center flex-shrink-0">
                                <span className="text-white font-bold">
                                  {friend.nombre.charAt(0).toUpperCase()}
                                </span>
                              </div>
                              <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between">
                                  <h3 className="font-semibold text-white truncate">
                                    {friend.nombre}
                                  </h3>
                                  {friend.lastMessageTime && (
                                    <span className="text-xs text-gray-500 ml-2 flex-shrink-0">
                                      {new Date(friend.lastMessageTime).toLocaleTimeString('es-ES', {
                                        hour: '2-digit',
                                        minute: '2-digit'
                                      })}
                                    </span>
                                  )}
                                </div>
                                {friend.lastMessage && (
                                  <p className="text-sm text-gray-400 truncate">
                                    {friend.isLastMessageFromMe && (
                                      <span className="text-gray-500">Tú: </span>
                                    )}
                                    {friend.lastMessage}
                                  </p>
                                )}
                              </div>
                            </div>
                            {friend.unreadCount > 0 && (
                              <span className="ml-2 bg-[#1db954] text-white text-xs rounded-full px-2 py-1 font-semibold">
                                {friend.unreadCount}
                              </span>
                            )}
                          </div>
                        </motion.button>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};

export default Chat;