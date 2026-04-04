import React, { useState, useEffect } from 'react';
import { getCommentsByIncident, createComment } from './api';
import { Send } from 'lucide-react';

const CommentList = ({ incidentId }) => {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");

    const refreshComments = () => {
        if (incidentId) {
            getCommentsByIncident(incidentId)
                .then(res => setComments(res.data))
                .catch(err => console.error("Erreur chargement:", err));
        }
    };

    useEffect(() => {
        refreshComments();
    }, [incidentId]);

    const handlePostComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim() || !incidentId) return;

        try {
            await createComment({
                message: newComment,           // ← correspond exactement au modèle backend
                incidentId: incidentId,        // ← UUID
                // utilisateurId sera géré côté backend plus tard
            });
            setNewComment("");
            refreshComments();
        } catch (err) {
            console.error("Erreur post commentaire:", err.response?.data || err);
            alert("Erreur lors de l'envoi du commentaire");
        }
    };

    return (
        <div style={{ marginTop: '20px', padding: '15px', borderTop: '2px solid #eee', backgroundColor: '#fff' }}>
            <h4 style={{ marginBottom: '15px' }}>Échanges & Commentaires</h4>
            
            {/* Liste des commentaires */}
            <div style={{ maxHeight: '300px', overflowY: 'auto', marginBottom: '15px' }}>
                {comments.length === 0 ? (
                    <p style={{ color: '#666', fontStyle: 'italic' }}>Aucun échange pour le moment.</p>
                ) : (
                    comments.map(c => (
                        <div key={c.id} style={{ background: '#f3f4f6', margin: '8px 0', padding: '10px', borderRadius: '8px' }}>
                            <p style={{ margin: '0 0 5px 0' }}>{c.message}</p>
                            <small style={{ color: '#999' }}>{new Date(c.dateCreation).toLocaleString()}</small>
                        </div>
                    ))
                )}
            </div>

            {/* Formulaire d'ajout */}
            <form onSubmit={handlePostComment} style={{ display: 'flex', gap: '10px' }}>
                <input 
                    style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    placeholder="Écrire un commentaire..."
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                />
                <button type="submit" style={{ backgroundColor: '#2563eb', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '4px', cursor: 'pointer' }}>
                    <Send size={16} />
                </button>
            </form>
        </div>
    );
};

export default CommentList;