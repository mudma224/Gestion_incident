import React, { useState, useRef, useEffect } from 'react';
import { Send, Bot, TicketCheck } from 'lucide-react';
import { sendMessage } from '../api/chat';
import { useKeycloak } from '@react-keycloak/web';

export default function Chat() {
  const { keycloak } = useKeycloak();
  const username = keycloak.tokenParsed?.preferred_username || 'User';
  const initials = username.slice(0, 2).toUpperCase();

  const [messages, setMessages] = useState([{
    sender: 'BOT',
    text: "Bonjour. Je suis votre assistant helpdesk. Décrivez votre problème et je rechercherai des solutions existantes avant d'ouvrir un ticket.",
    time: new Date(),
  }]);
  const [input, setInput] = useState('');
  const [conversationId, setConversationId] = useState(null);
  const [typing, setTyping] = useState(false);
  const [incidentCreated, setIncidentCreated] = useState(null);
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, typing]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text) return;
    setMessages(p => [...p, { sender: 'USER', text, time: new Date() }]);
    setInput('');
    setTyping(true);
    try {
      const res = await sendMessage({ conversationId, message: text });
      const data = res.data;
      setConversationId(data.conversationId);
      setMessages(p => [...p, { sender: 'BOT', text: data.botMessage, time: new Date() }]);
      if (data.createdIncidentId) setIncidentCreated(data.createdIncidentId);
    } catch {
      setMessages(p => [...p, { sender: 'BOT', text: "Une erreur s'est produite. Veuillez réessayer.", time: new Date() }]);
    } finally { setTyping(false); }
  };

  const fmt = d => new Date(d).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });

  return (
    <div className="chat-wrap">
      <div className="chat-panel">
        <div className="chat-header">
          <div className="chat-header-avatar"><Bot size={16} /></div>
          <div className="chat-header-info">
            <div className="chat-header-name">Assistant Helpdesk</div>
            <div className="chat-header-status">
              <div className="chat-online-dot" /> En ligne
              {conversationId && <span style={{ color: 'var(--text-muted)', marginLeft: 8 }}>
                · Session #{conversationId}
              </span>}
            </div>
          </div>
        </div>

        <div className="chat-messages">
          {messages.map((msg, i) => (
            <div key={i} className={`msg-row ${msg.sender.toLowerCase()}`}>
              <div className={`msg-avatar ${msg.sender.toLowerCase()}`}>
                {msg.sender === 'BOT' ? <Bot size={12} /> : initials}
              </div>
              <div>
                <div className={`msg-bubble ${msg.sender.toLowerCase()}`}>
                  {msg.text}
                  {msg.sender === 'BOT' && incidentCreated && i === messages.length - 1 && (
                    <div className="incident-created-banner">
                      <TicketCheck size={13} />
                      Ticket #{incidentCreated} ouvert — Un technicien vous contactera bientôt.
                    </div>
                  )}
                </div>
                <div className="msg-time">{fmt(msg.time)}</div>
              </div>
            </div>
          ))}

          {typing && (
            <div className="msg-row bot">
              <div className="msg-avatar bot"><Bot size={12} /></div>
              <div className="msg-typing">
                <div className="typing-dot" />
                <div className="typing-dot" />
                <div className="typing-dot" />
              </div>
            </div>
          )}
          <div ref={bottomRef} />
        </div>

        <div className="chat-input-area">
          <input
            className="chat-input"
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && !e.shiftKey && (e.preventDefault(), handleSend())}
            placeholder="Décrivez votre problème..."
            disabled={typing}
          />
          <button className="chat-send-btn" onClick={handleSend}
            disabled={typing || !input.trim()}>
            <Send size={14} />
          </button>
        </div>
      </div>
    </div>
  );
}