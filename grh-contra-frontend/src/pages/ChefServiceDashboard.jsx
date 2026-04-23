import React, { useEffect, useState } from 'react';
import { getTousEmployes, supprimerOperant } from '../services/employeService';
import { getAllContrats, creerContrat } from '../services/contratService';
import { logout, getNom } from '../services/authService';
import axios from 'axios';

const API_URL = 'http://localhost:3004';
const getHeaders = () => ({ headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });

const Icon = ({ name, size = 16 }) => {
  const icons = {
    users: <><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></>,
    file: <><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></>,
    plus: <><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></>,
    trash: <><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></>,
    check: <><polyline points="20 6 9 17 4 12"/></>,
    x: <><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></>,
    alert: <><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></>,
    logout: <><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></>,
    briefcase: <><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></>,
    search: <><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></>,
    layers: <><polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/></>,
  };
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">{icons[name]}</svg>;
};

const StatutBadge = ({ statut }) => <span className={`status-badge status-${statut}`}>{statut?.replace('_', ' ')}</span>;
const TypeBadge = ({ type }) => {
  const cls = { CDI: 'type-cdi', CDD: 'type-cdd', Stage: 'type-stage' };
  return <span className={`type-badge ${cls[type] || ''}`}>{type}</span>;
};

const Modal = ({ title, subtitle, onClose, children, footer }) => (
  <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
    <div className="modal">
      <div className="modal-header">
        <div><h2>{title}</h2>{subtitle && <p>{subtitle}</p>}</div>
        <button className="btn btn-ghost btn-sm" onClick={onClose}><Icon name="x" /></button>
      </div>
      <div className="modal-body">{children}</div>
      {footer && <div className="modal-footer">{footer}</div>}
    </div>
  </div>
);

const roleLabel = (role) => {
  if (role === 'DIRECTEUR') return 'Directeur';
  if (role === 'CHEF_SERVICE') return 'Chef de service';
  return 'Opérant';
};

const ChefServiceDashboard = () => {
  const [employes, setEmployes] = useState([]);      // TOUS les employés (pour formulaire contrat)
  const [contrats, setContrats] = useState([]);
  const [categories, setCategories] = useState([]);
  const [echelons, setEchelons] = useState([]);
  const [echelonsFiltres, setEchelonsFiltres] = useState([]); // échelons filtrés par catégorie
  const [onglet, setOnglet] = useState('employes');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [modalContrat, setModalContrat] = useState(false);
  const [form, setForm] = useState({
    employeId: '',
    typeContrat: 'CDD',
    dateDebut: '',
    dateFin: '',
    categorieId: '',
    echelonId: '',
  });

  const charger = async () => {
    try {
      const [emp, c, cat, ech] = await Promise.all([
        getTousEmployes(),
        getAllContrats(),
        axios.get(`${API_URL}/categories`, getHeaders()),
        axios.get(`${API_URL}/echelons`, getHeaders()),
      ]);
      setEmployes(emp.data);
      setContrats(c.data);
      setCategories(cat.data);
      setEchelons(ech.data);
    } catch {
      setError('Erreur de chargement des données');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { charger(); }, []);

  // Filtrer les échelons quand la catégorie change
  useEffect(() => {
    if (form.categorieId) {
      const filtres = echelons.filter(e => e.categorie?.id === parseInt(form.categorieId));
      setEchelonsFiltres(filtres);
      // Réinitialiser l'échelon si la catégorie change
      setForm(prev => ({ ...prev, echelonId: '' }));
    } else {
      setEchelonsFiltres(echelons);
    }
  }, [form.categorieId, echelons]);

  const ouvrirModalContrat = () => {
    setForm({ employeId: '', typeContrat: 'CDD', dateDebut: '', dateFin: '', categorieId: '', echelonId: '' });
    setModalContrat(true);
  };

  const handleCreerContrat = async () => {
    if (!form.employeId || !form.dateDebut || !form.categorieId || !form.echelonId) {
      setError('Employé, date de début, catégorie et échelon sont obligatoires');
      return;
    }
    try {
      await creerContrat({
        operant: { id: parseInt(form.employeId) },
        typeContrat: form.typeContrat,
        dateDebut: form.dateDebut,
        dateFin: form.dateFin || null,
        categorie: { id: parseInt(form.categorieId) },
        echelon: { id: parseInt(form.echelonId) },
      });
      setModalContrat(false);
      setError('');
      charger();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Erreur lors de la création du contrat');
    }
  };

  const filteredEmployes = employes.filter(e =>
    e.nom.toLowerCase().includes(search.toLowerCase()) ||
    e.login?.toLowerCase().includes(search.toLowerCase())
  );

  const stats = [
    { label: 'EMPLOYÉS', value: employes.length, color: '#3ecf8e' },
    { label: 'CONTRATS', value: contrats.length, color: '#4f8ef7' },
    { label: 'EN COURS', value: contrats.filter(c => c.statut === 'EN_COURS').length, color: '#e8a84c' },
    { label: 'EN ATTENTE', value: contrats.filter(c => c.statut === 'BROUILLON').length, color: '#8b90a8' },
  ];

  const roleStyle = (role) => ({
    padding: '3px 10px', borderRadius: '5px', fontSize: '11.5px',
    fontFamily: 'IBM Plex Mono', fontWeight: 500,
    background: role === 'DIRECTEUR' ? 'rgba(232,168,76,0.15)' : role === 'CHEF_SERVICE' ? 'rgba(79,142,247,0.15)' : 'rgba(62,207,142,0.15)',
    color: role === 'DIRECTEUR' ? '#e8a84c' : role === 'CHEF_SERVICE' ? '#4f8ef7' : '#3ecf8e',
  });

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-badge">
            <div className="logo-icon" style={{ background: 'linear-gradient(135deg, #4f8ef7, #3b6fd4)' }}>
              <Icon name="briefcase" size={18} />
            </div>
            <div className="logo-text">
              <span className="brand">GRH Contrats</span>
              <span className="module">M4 · Contrats</span>
            </div>
          </div>
        </div>
        <nav className="sidebar-nav">
          <div className="nav-label">Navigation</div>
          <button className={`nav-item ${onglet === 'employes' ? 'active' : ''}`} onClick={() => { setOnglet('employes'); setSearch(''); }}>
            <Icon name="users" size={16} /><span>Employés</span>
          </button>
          <button className={`nav-item ${onglet === 'contrats' ? 'active' : ''}`} onClick={() => { setOnglet('contrats'); setSearch(''); }}>
            <Icon name="file" size={16} /><span>Contrats</span>
          </button>
        </nav>
        <div className="sidebar-footer">
          <div style={{ marginBottom: '12px', padding: '10px 12px', background: 'rgba(79,142,247,0.08)', borderRadius: '8px', border: '1px solid rgba(79,142,247,0.15)' }}>
            <div style={{ fontSize: '11px', color: '#555d78', fontFamily: 'IBM Plex Mono', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '4px' }}>Connecté</div>
            <div style={{ fontSize: '13px', color: '#4f8ef7', fontWeight: 600 }}>{getNom()}</div>
            <div style={{ fontSize: '11px', color: '#555d78' }}>Chef de service</div>
          </div>
          <button className="btn btn-danger btn-sm" style={{ width: '100%', justifyContent: 'center' }} onClick={logout}>
            <Icon name="logout" size={14} /> Déconnexion
          </button>
        </div>
      </aside>

      <main className="main-content">
        <div className="topbar">
          <div className="topbar-left">
            <h1>{onglet === 'employes' ? 'Liste des Employés' : 'Gestion des Contrats'}</h1>
            <p>{onglet === 'employes' ? 'Tous les employés enregistrés dans le système' : 'Créez et suivez les contrats'}</p>
          </div>
          <div className="topbar-right">
            {onglet === 'contrats' && (
              <button className="btn btn-primary btn-sm" onClick={ouvrirModalContrat}>
                <Icon name="plus" size={14} /> Nouveau contrat
              </button>
            )}
          </div>
        </div>

        <div className="page-body">
          {error && (
            <div className="error-banner">
              <Icon name="alert" size={15} /> {error}
              <button style={{ marginLeft: 'auto', background: 'none', border: 'none', color: '#f76f6f', cursor: 'pointer' }} onClick={() => setError('')}><Icon name="x" size={13} /></button>
            </div>
          )}

          <div className="stats-bar">
            {stats.map(s => (
              <div key={s.label} className="stat-card">
                <div className="stat-accent" style={{ background: s.color }} />
                <div className="stat-label">{s.label}</div>
                <div className="stat-value" style={{ color: s.color }}>{s.value}</div>
              </div>
            ))}
          </div>

          {loading ? <div className="loading-overlay"><div className="spinner" /> Chargement...</div> : (
            <>
              {/* LISTE DES EMPLOYÉS */}
              {onglet === 'employes' && (
                <>
                  <div className="toolbar">
                    <div className="search-input-wrap">
                      <Icon name="search" size={15} />
                      <input className="search-input" placeholder="Rechercher par nom ou login..." value={search} onChange={e => setSearch(e.target.value)} />
                    </div>
                    <span className="record-count">{filteredEmployes.length} employé(s)</span>
                  </div>
                  <div className="table-wrap">
                    <table className="contrat-table">
                      <thead><tr><th>#ID</th><th>Nom</th><th>Âge</th><th>Famille</th><th>Rôle</th><th>Login</th></tr></thead>
                      <tbody>
                        {filteredEmployes.length === 0
                          ? <tr><td colSpan={6}><div className="empty-state"><Icon name="users" size={32} /><h3>Aucun employé</h3></div></td></tr>
                          : filteredEmployes.map(e => (
                            <tr key={e.id}>
                              <td className="cell-id">#{e.id}</td>
                              <td className="cell-employe">{e.nom}</td>
                              <td>{e.age}</td>
                              <td>{e.famille || '—'}</td>
                              <td><span style={roleStyle(e.role)}>{roleLabel(e.role)}</span></td>
                              <td style={{ fontFamily: 'IBM Plex Mono', fontSize: '12.5px' }}>{e.login}</td>
                            </tr>
                          ))}
                      </tbody>
                    </table>
                  </div>
                </>
              )}

              {/* LISTE DES CONTRATS */}
              {onglet === 'contrats' && (
                <div className="table-wrap">
                  <table className="contrat-table">
                    <thead><tr><th>#ID</th><th>Employé</th><th>Type</th><th>Catégorie</th><th>Échelon</th><th>Date Début</th><th>Date Fin</th><th>Statut</th></tr></thead>
                    <tbody>
                      {contrats.length === 0
                        ? <tr><td colSpan={8}><div className="empty-state"><Icon name="file" size={32} /><h3>Aucun contrat</h3><p>Créez le premier contrat.</p></div></td></tr>
                        : contrats.map(c => (
                          <tr key={c.id}>
                            <td className="cell-id">#{c.id}</td>
                            <td className="cell-employe">{c.operant?.nom}</td>
                            <td><TypeBadge type={c.typeContrat} /></td>
                            <td style={{ fontSize: '12.5px', color: '#8b5cf6' }}>{c.categorie?.libelle || '—'}</td>
                            <td style={{ fontFamily: 'IBM Plex Mono', fontSize: '12px', color: '#4f8ef7' }}>{c.echelon?.code || '—'}</td>
                            <td className="cell-date">{c.dateDebut}</td>
                            <td className="cell-date">{c.dateFin || '—'}</td>
                            <td><StatutBadge statut={c.statut} /></td>
                          </tr>
                        ))}
                    </tbody>
                  </table>
                </div>
              )}
            </>
          )}
        </div>
      </main>

      {/* MODAL NOUVEAU CONTRAT */}
      {modalContrat && (
        <Modal
          title="Nouveau contrat"
          subtitle="Sélectionnez un employé, une catégorie et un échelon"
          onClose={() => setModalContrat(false)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setModalContrat(false)}>Annuler</button>
              <button className="btn btn-primary" onClick={handleCreerContrat}><Icon name="check" size={14} /> Créer le contrat</button>
            </>
          }
        >
          <div className="form-grid">
            {/* SÉLECTION EMPLOYÉ (tous rôles confondus) */}
            <div className="form-group full-width">
              <label className="form-label">Employé <span className="required">*</span></label>
              <select
                className="form-select"
                value={form.employeId}
                onChange={e => setForm({ ...form, employeId: e.target.value })}
              >
                <option value="">-- Sélectionner un employé --</option>
                {employes.map(e => (
                  <option key={e.id} value={e.id}>
                    {e.nom} — {roleLabel(e.role)} (#{e.id})
                  </option>
                ))}
              </select>
              {employes.length === 0 && (
                <span className="form-hint" style={{ color: '#f76f6f' }}>Aucun employé trouvé. Veuillez d'abord créer des employés.</span>
              )}
            </div>

            {/* TYPE DE CONTRAT */}
            <div className="form-group">
              <label className="form-label">Type de contrat</label>
              <select className="form-select" value={form.typeContrat} onChange={e => setForm({ ...form, typeContrat: e.target.value })}>
                <option value="CDD">CDD</option>
                <option value="CDI">CDI</option>
                <option value="Stage">Stage</option>
              </select>
            </div>

            {/* SÉLECTION CATÉGORIE */}
            <div className="form-group">
              <label className="form-label">Catégorie <span className="required">*</span></label>
              <select
                className="form-select"
                value={form.categorieId}
                onChange={e => setForm({ ...form, categorieId: e.target.value, echelonId: '' })}
              >
                <option value="">-- Sélectionner une catégorie --</option>
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.libelle} ({c.code})</option>
                ))}
              </select>
              {categories.length === 0 && (
                <span className="form-hint" style={{ color: '#f76f6f' }}>Aucune catégorie disponible. Le directeur doit d'abord créer des catégories.</span>
              )}
            </div>

            {/* SÉLECTION ÉCHELON (filtré par catégorie) */}
            <div className="form-group">
              <label className="form-label">Échelon <span className="required">*</span></label>
              <select
                className="form-select"
                value={form.echelonId}
                onChange={e => setForm({ ...form, echelonId: e.target.value })}
                disabled={!form.categorieId}
              >
                <option value="">
                  {form.categorieId ? '-- Sélectionner un échelon --' : '-- Choisir d\'abord une catégorie --'}
                </option>
                {echelonsFiltres.map(e => (
                  <option key={e.id} value={e.id}>
                    {e.code} — indice {e.indiceSalarial?.toLocaleString()}
                  </option>
                ))}
              </select>
              {form.categorieId && echelonsFiltres.length === 0 && (
                <span className="form-hint" style={{ color: '#f76f6f' }}>Aucun échelon pour cette catégorie. Le directeur doit d'abord en créer.</span>
              )}
            </div>

            {/* DATES */}
            <div className="form-group">
              <label className="form-label">Date de début <span className="required">*</span></label>
              <input type="date" className="form-input" value={form.dateDebut} onChange={e => setForm({ ...form, dateDebut: e.target.value })} />
            </div>
            <div className="form-group">
              <label className="form-label">Date de fin <span style={{ color: '#555d78', fontStyle: 'italic' }}>(optionnel)</span></label>
              <input type="date" className="form-input" value={form.dateFin} onChange={e => setForm({ ...form, dateFin: e.target.value })} />
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};

export default ChefServiceDashboard;