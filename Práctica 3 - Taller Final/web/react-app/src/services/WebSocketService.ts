import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface ChatMessage {
  id?:           number;
  senderId:      number;
  senderName:    string;
  recipientId:   number;
  recipientName: string;
  content:       string;
  timestamp:     string;
  isRead:        boolean;
  messageType:   string;
}

export interface MessageHandler {
  onMessage: (message: ChatMessage) => void;
  onTyping?: (userId: number) => void;
  onRead?: (userId: number) => void;
  onError?: (error: string) => void;
}

class WebSocketService {
  private client: Client | null = null;
  private userId: number | null = null;
  private messageHandlers: Map<number, MessageHandler> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;

  connect(userId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.userId = userId;
      
      this.client = new Client({
        webSocketFactory: () => new SockJS('/ws'),
        reconnectDelay: this.reconnectDelay,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        
        onConnect: () => {
          console.log('WebSocket Connected');
          this.reconnectAttempts = 0;
          
          // Suscribirse a mensajes privados
          this.client?.subscribe(`/user/${userId}/queue/messages`, (message: IMessage) => {
            const chatMessage: ChatMessage = JSON.parse(message.body);
            const otherUserId = chatMessage.senderId === userId ? chatMessage.recipientId : chatMessage.senderId;
            const handler = this.messageHandlers.get(otherUserId);
            handler?.onMessage(chatMessage);
          });
          
          // Suscribirse a notificaciones de typing
          this.client?.subscribe(`/user/${userId}/queue/typing`, (message: IMessage) => {
            const typingUserId = parseInt(message.body);
            const handler = this.messageHandlers.get(typingUserId);
            handler?.onTyping?.(typingUserId);
          });
          
          // Suscribirse a confirmaciones de lectura
          this.client?.subscribe(`/user/${userId}/queue/read`, (message: IMessage) => {
            const readByUserId = parseInt(message.body);
            const handler = this.messageHandlers.get(readByUserId);
            handler?.onRead?.(readByUserId);
          });
          
          // Suscribirse a errores
          this.client?.subscribe(`/user/${userId}/queue/errors`, (message: IMessage) => {
            console.error('WebSocket Error:', message.body);
            // Notificar a todos los handlers
            this.messageHandlers.forEach(handler => {
              handler.onError?.(message.body);
            });
          });
          
          resolve();
        },
        
        onStompError: (frame) => {
          console.error('Broker reported error: ' + frame.headers['message']);
          console.error('Additional details: ' + frame.body);
          reject(new Error('STOMP error'));
        },
        
        onWebSocketError: (event) => {
          console.error('WebSocket error', event);
          this.handleReconnect();
        },
        
        onDisconnect: () => {
          console.log('WebSocket Disconnected');
          this.handleReconnect();
        }
      });
      
      this.client.activate();
    });
  }
  
  private handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      setTimeout(() => {
        if (this.userId) {
          this.connect(this.userId);
        }
      }, this.reconnectDelay * this.reconnectAttempts);
    }
  }
  
  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.messageHandlers.clear();
  }
  
  sendMessage(recipientId: number, content: string) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination: '/app/chat.send',
        body: JSON.stringify({ recipientId, content })
      });
    } else {
      console.error('WebSocket is not connected');
      throw new Error('No hay conexión con el servidor');
    }
  }
  
  sendTypingNotification(recipientId: number) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination: '/app/chat.typing',
        body: recipientId.toString()
      });
    }
  }
  
  markAsRead(senderId: number) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination: '/app/chat.markAsRead',
        body: senderId.toString()
      });
    }
  }
  
  registerHandler(friendId: number, handler: MessageHandler) {
    this.messageHandlers.set(friendId, handler);
  }
  
  unregisterHandler(friendId: number) {
    this.messageHandlers.delete(friendId);
  }
  
  isConnected(): boolean {
    return this.client?.connected || false;
  }
}

export default new WebSocketService();