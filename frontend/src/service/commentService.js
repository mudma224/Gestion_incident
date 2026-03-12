import axios from 'axios';

const API_URL = "http://localhost:8082/api/comments";

export const commentService = {
  createComment: async (content, incidentId) => {
    const commentData = {
      content: content,
      // On utilise des formats UUID comme dans ton test Postman réussi
      incidentId: incidentId, 
      userId: "00000000-0000-0000-0000-000000000000", 
    };
    const response = await axios.post(API_URL, commentData);
    return response.data;
  }
};