import { useState, useEffect } from 'react';
import { api } from '../services/api';

function ContratList() {
  const [contrats, setContrats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadContrats();
  }, []);

  const loadContrats = async () => {
    try {
      setLoading(true);
      const data = await api.getContrats();
      setContrats(data);
      setError(null);
    } catch (err) {
      setError('Erreur lors du chargement des contrats');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleActiver = async (id) => {
    try {
      await api.activerContrat(id);
      loadContrats(); // Recharger la liste après activation
    } catch (err) {
      setError('Erreur lors de l\'activation du contrat');
      console.error(err);
    }
  };

  const getStatutBadge = (statut) => {
    const colors = {
      'BROUILLON': 'bg-yellow-100 text-yellow-800',
      'EN_COURS': 'bg-green-100 text-green-800',
      'TERMINE': 'bg-gray-100 text-gray-800',
      'ANNULE': 'bg-red-100 text-red-800'
    };
    return colors[statut] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center p-8">
        <div className="text-lg">Chargement...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
        {error}
        <button 
          onClick={loadContrats}
          className="ml-4 bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
        >
          Réessayer
        </button>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Gestion des Contrats</h1>
      
      {contrats.length === 0 ? (
        <div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded">
          Aucun contrat trouvé
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white border border-gray-300">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Employé ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Date de début
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {contrats.map((contrat) => (
                <tr key={contrat.id}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {contrat.id}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {contrat.employeId}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(contrat.dateDebut).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatutBadge(contrat.statut)}`}>
                      {contrat.statut}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    {contrat.statut === 'BROUILLON' && (
                      <button
                        onClick={() => handleActiver(contrat.id)}
                        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 mr-2"
                      >
                        Activer
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default ContratList;
