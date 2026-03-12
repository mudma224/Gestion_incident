import React, { useState } from 'react';
import { commentService } from '../service/commentService.js';

const CommentSection = ({ incidentId }) => {
  const [text, setText] = useState("");

  const handleSend = async () => {
    if (!text) return;
    try {
      // Appel au service avec l'UUID de l'incident
      await commentService.createComment(text, incidentId);
      alert("Commentaire enregistré dans la base !");
      setText(""); 
    } catch (err) {
      alert("Erreur : vérifie que le service port 8082 tourne.");
    }
  };

  return (
    <div style={{ marginTop: '20px', borderTop: '1px solid #ccc', padding: '10px' }}>
      <h4>Ajouter un commentaire</h4>
      <textarea 
        value={text} 
        onChange={(e) => setText(e.target.value)}
        placeholder="Votre commentaire ici..."
        style={{ width: '100%', height: '60px' }}
      />
      <button onClick={handleSend} style={{ marginTop: '10px', backgroundColor: '#4CAF50', color: 'white' }}>
        Publier le commentaire
      </button>
    </div>
  );
};

export default CommentSection;