import { useState, useEffect, useCallback } from 'react';
import {
  FileText, Plus, RefreshCw, Search, LayoutDashboard,
  AlertCircle, PlayCircle, Users, CheckCircle, Clock,
  XCircle,
} from 'lucide-react';
import { contratApi } from './api/contratApi';
import ContratForm from './components/ContratForm';
import ConfirmModal from './components/ConfirmModal';
import AnnulerModal from './components/AnnulerModal';
import { ToastProvider, toast } from './components/Toast';

function formatDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
}

function StatusBadge({ statut }) {
  const labels = { BROUILLON: 'Brouillon', EN_COURS: 'En cours', TERMINE: 'Terminé', ANNULE: 'Annulé' };
  return <span className={`status-badge status-${statut}`}>{labels[statut] ?? statut}</span>;
}

function TypeBadge({ type }) {
  const cls = { CDI: 'type-cdi', CDD: 'type-cdd', Stage: 'type-stage' };
  return <span className={`type-badge ${cls[type] ?? ''}`}>{type}</span>;
}

function StatCard({ label, value, color, icon: Icon }) {
  return (
    <div className="stat-card">
      <div className="stat-accent" style={{ background: color }} />
      <div className="stat-label">{label}</div>
      <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between' }}>
        <div className="stat-value">{value}</div>
        <Icon size={22} color={color} opacity={0.6} />
      </div>
    </div>
  );
}

export default function App() {
  const [contrats, setContrats]           = useState([]);
  const [loading, setLoading]             = useState(true);
  const [error, setError]                 = useState(null);
  const [apiOnline, setApiOnline]         = useState(null);
  const [showForm, setShowForm]           = useState(false);
  const [confirmContrat, setConfirmContrat] = useState(null);   // pour activer
  const [annulerContrat, setAnnulerContrat] = useState(null);   // pour annuler
  const [activating, setActivating]       = useState(false);
  const [annulling, setAnnulling]         = useState(false);
  const [search, setSearch]               = useState('');
  const [filterStatut, setFilterStatut]   = useState('');
  const [filterType, setFilterType]       = useState('');

  const loadContrats = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await contratApi.getAll();
      setContrats(data || []);
      setApiOnline(true);
    } catch (err) {
      setError(err.message);
      setApiOnline(false);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadContrats(); }, [loadContrats]);

  // Créer
  async function handleCreate(payload) {
    try {
      const created = await contratApi.create(payload);
      setContrats((prev) => [created, ...prev]);
      toast.success('Contrat créé avec succès (statut : BROUILLON)');
      setShowForm(false);
    } catch (err) {
      toast.error(`Erreur : ${err.message}`);
      throw err;
    }
  }

  // Activer
  async function handleActivate() {
    if (!confirmContrat) return;
    setActivating(true);
    try {
      const updated = await contratApi.activer(confirmContrat.id);
      setContrats((prev) =>
        prev.map((c) => {
          if (c.id === updated.id) return updated;
          if (c.employeId === updated.employeId && c.statut === 'EN_COURS' && c.id !== updated.id)
            return { ...c, statut: 'TERMINE' };
          return c;
        })
      );
      toast.success(`Contrat #${updated.id} activé avec succès`);
      setConfirmContrat(null);
    } catch (err) {
      toast.error(`Erreur : ${err.message}`);
    } finally {
      setActivating(false);
    }
  }

  // Annuler
  async function handleAnnuler(motif) {
    if (!annulerContrat) return;
    setAnnulling(true);
    try {
      const updated = await contratApi.annuler(annulerContrat.id, motif);
      setContrats((prev) => prev.map((c) => (c.id === updated.id ? updated : c)));
      toast.success(`Contrat #${updated.id} annulé`);
      setAnnulerContrat(null);
    } catch (err) {
      toast.error(`Erreur : ${err.message}`);
    } finally {
      setAnnulling(false);
    }
  }

  const stats = {
    total:     contrats.length,
    brouillon: contrats.filter((c) => c.statut === 'BROUILLON').length,
    enCours:   contrats.filter((c) => c.statut === 'EN_COURS').length,
    clotures:  contrats.filter((c) => c.statut === 'TERMINE' || c.statut === 'ANNULE').length,
  };

  const filtered = contrats.filter((c) => {
    const q = search.toLowerCase();
    const matchSearch =
      !q ||
      String(c.id).includes(q) ||
      String(c.employeId).includes(q) ||
      (c.typeContrat || '').toLowerCase().includes(q);
    const matchStatut = !filterStatut || c.statut === filterStatut;
    const matchType   = !filterType   || c.typeContrat === filterType;
    return matchSearch && matchStatut && matchType;
  });

  // Un contrat est annulable s'il est BROUILLON ou EN_COURS
  function isAnnulable(c) {
    return c.statut === 'BROUILLON' || c.statut === 'EN_COURS';
  }

  return (
    <>
      <ToastProvider />
      <div className="app-shell">
        {/* Sidebar */}
        <aside className="sidebar">
          <div className="sidebar-logo">
            <div className="logo-badge">
              <div className="logo-icon"><FileText size={18} /></div>
              <div className="logo-text">
                <span className="brand">GRH Suite</span>
                <span className="module">Module M4</span>
              </div>
            </div>
          </div>
          <nav className="sidebar-nav">
            <span className="nav-label">Principal</span>
            <button className="nav-item active">
              <LayoutDashboard size={17} /><span>Contrats</span>
            </button>
          </nav>
          <div className="sidebar-footer">
            <div className="api-status">
              <div className={`status-dot ${apiOnline === true ? 'online' : apiOnline === false ? 'offline' : ''}`} />
              <span>
                {apiOnline === true ? ':3004 connecté' : apiOnline === false ? 'API hors ligne' : 'Connexion…'}
              </span>
            </div>
          </div>
        </aside>

        {/* Main */}
        <div className="main-content">
          <header className="topbar">
            <div className="topbar-left">
              <h1>Gestion des Contrats</h1>
              <p>Créez, activez et clôturez les contrats de travail</p>
            </div>
            <div className="topbar-right">
              <button className="btn btn-secondary btn-sm" onClick={loadContrats} disabled={loading}>
                <RefreshCw size={14} />
                Actualiser
              </button>
              <button className="btn btn-primary" onClick={() => setShowForm(true)}>
                <Plus size={16} />
                Nouveau contrat
              </button>
            </div>
          </header>

          <div className="page-body">
            {/* Stats */}
            <div className="stats-bar">
              <StatCard label="Total"      value={stats.total}     color="var(--text2)" icon={FileText} />
              <StatCard label="Brouillons" value={stats.brouillon} color="var(--text3)" icon={Clock} />
              <StatCard label="En cours"   value={stats.enCours}   color="var(--green)" icon={CheckCircle} />
              <StatCard label="Clôturés"   value={stats.clotures}  color="var(--blue)"  icon={XCircle} />
            </div>

            {error && (
              <div className="error-banner">
                <AlertCircle size={16} />
                {error} — Vérifiez que le backend tourne sur le port 3004.
              </div>
            )}

            {/* Toolbar */}
            <div className="toolbar">
              <div className="search-input-wrap">
                <Search size={15} />
                <input
                  className="search-input"
                  placeholder="Rechercher par ID, employé, type…"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
              <select className="filter-select" value={filterStatut} onChange={(e) => setFilterStatut(e.target.value)}>
                <option value="">Tous les statuts</option>
                <option value="BROUILLON">Brouillon</option>
                <option value="EN_COURS">En cours</option>
                <option value="TERMINE">Terminé</option>
                <option value="ANNULE">Annulé</option>
              </select>
              <select className="filter-select" value={filterType} onChange={(e) => setFilterType(e.target.value)}>
                <option value="">Tous les types</option>
                <option value="CDI">CDI</option>
                <option value="CDD">CDD</option>
                <option value="Stage">Stage</option>
              </select>
            </div>

            {/* Table */}
            <div className="section-header">
              <span className="section-title">Liste des contrats</span>
              <span className="record-count">{filtered.length} résultat{filtered.length !== 1 ? 's' : ''}</span>
            </div>

            <div className="table-wrap">
              {loading ? (
                <div className="loading-overlay">
                  <div className="spinner" />
                  Chargement des contrats…
                </div>
              ) : filtered.length === 0 ? (
                <div className="empty-state">
                  <FileText size={40} />
                  <h3>{search || filterStatut || filterType ? 'Aucun résultat trouvé' : 'Aucun contrat enregistré'}</h3>
                  <p>{search || filterStatut || filterType ? 'Modifiez vos filtres' : 'Cliquez sur « Nouveau contrat » pour commencer'}</p>
                </div>
              ) : (
                <table className="contrat-table">
                  <thead>
                    <tr>
                      <th>#ID</th>
                      <th>Employé</th>
                      <th>Type</th>
                      <th>Statut</th>
                      <th>Date début</th>
                      <th>Date fin</th>
                      <th>Créé le</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((c) => (
                      <tr key={c.id}>
                        <td><span className="cell-id">#{c.id}</span></td>
                        <td>
                          <span className="cell-employe">
                            <Users size={12} style={{ display: 'inline', marginRight: 6, verticalAlign: 'middle' }} />
                            Employé #{c.employeId}
                          </span>
                        </td>
                        <td className="cell-type"><TypeBadge type={c.typeContrat} /></td>
                        <td><StatusBadge statut={c.statut} /></td>
                        <td className="cell-date">{formatDate(c.dateDebut)}</td>
                        <td className="cell-date">{formatDate(c.dateFin)}</td>
                        <td className="cell-date">
                          {c.dateCreation ? formatDate(c.dateCreation.split('T')[0]) : '—'}
                        </td>
                        <td>
                          <div className="cell-actions">
                            {c.statut === 'BROUILLON' && (
                              <button
                                className="btn btn-success btn-sm"
                                onClick={() => setConfirmContrat(c)}
                                title="Activer le contrat"
                              >
                                <PlayCircle size={13} />
                                Activer
                              </button>
                            )}
                            {isAnnulable(c) && (
                              <button
                                className="btn btn-danger btn-sm"
                                onClick={() => setAnnulerContrat(c)}
                                title="Annuler le contrat"
                              >
                                <XCircle size={13} />
                                Annuler
                              </button>
                            )}
                            {!isAnnulable(c) && (
                              <span style={{ fontSize: 12, color: 'var(--text3)', fontStyle: 'italic' }}>
                                {c.statut === 'TERMINE' ? 'Terminé auto.' : 'Annulé'}
                              </span>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>
      </div>

      {showForm && <ContratForm onClose={() => setShowForm(false)} onCreated={handleCreate} />}

      {confirmContrat && (
        <ConfirmModal
          contrat={confirmContrat}
          onClose={() => setConfirmContrat(null)}
          onConfirm={handleActivate}
          loading={activating}
        />
      )}

      {annulerContrat && (
        <AnnulerModal
          contrat={annulerContrat}
          onClose={() => setAnnulerContrat(null)}
          onConfirm={handleAnnuler}
          loading={annulling}
        />
      )}

      <style>{`.spin { animation: spin 1s linear infinite; }`}</style>
    </>
  );
}