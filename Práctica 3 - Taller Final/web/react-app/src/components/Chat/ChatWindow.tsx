import React, { useState, useEffect, useRef } from 'react';
import { ArrowLeft, Send, Check, CheckCheck } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import WebSocketService, { ChatMessage } from '../../services/WebSocketService';

interface Friend {
  id: number;
  nombre: string;
  email: string;
}

interface ChatWindowProps {
  friend: Friend;
  currentUserId: number;
  onClose: () => void;
  onNewMessage: () => void;
}

const ChatWindow: React.FC<ChatWindowProps> = ({ friend, currentUserId, onClose, onNewMessage }) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSending, setSending] = useState(false);
  const [isTyping, setIsTyping] = useState(false);
  const [error, setError] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const typingTimeoutRef = useRef<NodeJS.Timeout>();
  const lastTypingRef = useRef<number>(0);

  useEffect(() => {
    loadMessages();
    
    // Registrar handlers para este chat
    WebSocketService.registerHandler(friend.id, {
      onMessage: handleNewMessage,
      onTyping: handleTyping,
      onRead: handleRead,
      onError: handleError
    });

    // Marcar mensajes como leídos
    WebSocketService.markAsRead(friend.id);

    return () => {
      WebSocketService.unregisterHandler(friend.id);
      if (typingTimeoutRef.current) {
        clearTimeout(typingTimeoutRef.current);
      }
    };
  }, [friend.id]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const loadMessages = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(`/api/chat/messages/${friend.id}?page=0&size=50`, {
        credentials: 'include'
      });
      const data = await response.json();
      // Los mensajes vienen en orden descendente, así que los invertimos
      setMessages(data.content.reverse() || []);
    } catch (error) {
      console.error('Error loading messages:', error);
      setError('Error al cargar mensajes');
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewMessage = (message: ChatMessage) => {
    setMessages(prev => [...prev, message]);
    onNewMessage();
    
    // Si el mensaje es del amigo, marcarlo como leído
    if (message.senderId === friend.id) {
      WebSocketService.markAsRead(friend.id);
    }
  };

  const handleTyping = (userId: number) => {
    if (userId === friend.id) {
      setIsTyping(true);
      if (typingTimeoutRef.current) {
        clearTimeout(typingTimeoutRef.current);
      }
      typingTimeoutRef.current = setTimeout(() => {
        setIsTyping(false);
      }, 3000);
    }
  };

  const handleRead = (userId: number) => {
    if (userId === friend.id) {
      // Marcar todos los mensajes enviados como leídos
      setMessages(prev => prev.map(msg => 
        msg.senderId === currentUserId ? { ...msg, isRead: true } : msg
      ));
    }
  };

  const handleError = (error: string) => {
    setError(error);
    setTimeout(() => setError(''), 5000);
  };

  const handleSend = async () => {
    if (!newMessage.trim() || isSending) return;

    const messageContent = newMessage.trim();
    setNewMessage('');
    setSending(true);
    setError('');

    try {
      WebSocketService.sendMessage(friend.id, messageContent);
    } catch (error) {
      setError('Error al enviar mensaje');
      setNewMessage(messageContent); // Restaurar el mensaje
      console.error('Error sending message:', error);
    } finally {
      setSending(false);
    }
  };

  const handleTypingInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMessage(e.target.value);
    
    // Enviar notificación de typing
    const now = Date.now();
    if (now - lastTypingRef.current > 2000) {
      WebSocketService.sendTypingNotification(friend.id);
      lastTypingRef.current = now;
    }
  };

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Hoy';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Ayer';
    } else {
      return date.toLocaleDateString('es-ES', {
        day: 'numeric',
        month: 'long',
        year: date.getFullYear() !== today.getFullYear() ? 'numeric' : undefined
      });
    }
  };

  const shouldShowDate = (index: number) => {
    if (index === 0) return true;
    const currentDate = new Date(messages[index].timestamp).toDateString();
    const previousDate = new Date(messages[index - 1].timestamp).toDateString();
    return currentDate !== previousDate;
  };

  return (
    <div className="flex flex-col h-full">
      {/* Header del chat */}
      <div className="bg-[#111] p-4 border-b border-white/10 flex items-center space-x-3">
        <button
          onClick={onClose}
          className="text-gray-400 hover:text-white transition-colors"
        >
          <ArrowLeft size={20} />
        </button>
        <div className="flex-1">
          <h3 className="font-semibold text-white">{friend.nombre}</h3>
          {isTyping && (
            <motion.p
              initial={{ opacity: 0, y: -5 }}
              animate={{ opacity: 1, y: 0 }}
              className="text-sm text-[#1db954]"
            >
              Escribiendo...
            </motion.p>
          )}
        </div>
      </div>

      {/* Mensajes */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {isLoading ? (
          <div className="flex justify-center py-8">
            <div className="w-8 h-8 border-2 border-[#1db954] border-t-transparent rounded-full animate-spin"></div>
          </div>
        ) : messages.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <p>No hay mensajes aún</p>
            <p className="text-sm mt-2">¡Sé el primero en escribir!</p>
          </div>
        ) : (
          <>
            {messages.map((message, index) => (
              <React.Fragment key={message.id}>
                {shouldShowDate(index) && (
                  <div className="flex justify-center my-4">
                    <span className="bg-[#222] px-3 py-1 rounded-full text-xs text-gray-400">
                      {formatDate(message.timestamp)}
                    </span>
                  </div>
                )}
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className={`flex ${message.senderId === currentUserId ? 'justify-end' : 'justify-start'}`}
                >
                  <div
                    className={`max-w-[70%] px-4 py-2 rounded-2xl ${
                      message.senderId === currentUserId
                        ? 'bg-[#1db954] text-white'
                        : 'bg-[#333] text-white'
                    }`}
                  >
                    <p className="break-words">{message.content}</p>
                    <div className={`flex items-center justify-end space-x-1 mt-1 ${
                      message.senderId === currentUserId ? 'text-white/70' : 'text-gray-500'
                    }`}>
                      <span className="text-xs">{formatTime(message.timestamp)}</span>
                      {message.senderId === currentUserId && (
                        message.isRead ? (
                          <CheckCheck size={14} className="text-white/70" />
                        ) : (
                          <Check size={14} className="text-white/70" />
                        )
                      )}
                    </div>
                  </div>
                </motion.div>
              </React.Fragment>
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* Error */}
      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 10 }}
            className="px-4 py-2 bg-red-500/10 text-red-400 text-sm text-center"
          >
            {error}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Input de mensaje */}
      <div className="p-4 border-t border-white/10">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSend();
          }}
          className="flex items-center space-x-2"
        >
          <input
            type="text"
            value={newMessage}
            onChange={handleTypingInput}
            placeholder="Escribe un mensaje..."
            className="flex-1 px-4 py-2 bg-[#222] border border-[#333] rounded-full text-white text-sm focus:outline-none focus:border-[#1db954] transition-all"
            disabled={isSending}
          />
          <motion.button
            type="submit"
            disabled={!newMessage.trim() || isSending}
            className="p-2 bg-[#1db954] hover:bg-[#1ed760] rounded-full text-white disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            {isSending ? (
              <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
            ) : (
              <Send size={20} />
            )}
          </motion.button>
        </form>
      </div>
    </div>
  );
};

export default ChatWindow;