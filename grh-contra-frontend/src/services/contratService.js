import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const getAllContrats = () => axios.get(`${API_URL}/contrats`);

export const activerContrat = (id) => axios.put(`${API_URL}/contrats/${id}/activer`);

export const annulerContrat = (id, motif) =>
  axios.put(`${API_URL}/contrats/${id}/annuler`, { motif });

// ✅ NOUVEAU
export const terminerContrat = (id) =>
  axios.put(`${API_URL}/contrats/${id}/terminer`);