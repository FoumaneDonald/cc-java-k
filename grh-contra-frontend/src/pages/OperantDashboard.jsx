import React, { useEffect, useState } from 'react';
import { getProfil, getChefsService, modifierProfil } from '../services/employeService';
import { getMonContrat } from '../services/contratService';
import { logout, getNom } from '../services/authService';

const Icon = ({ name, size = 16 }) => {
  const icons = {
    user: <><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></>,
    file: <><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></>,
    users: <><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></>,
    edit: <><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></>,
    check: <><polyline points="20 6 9 17 4 12"/></>,
    x: <><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></>,
    logout: <><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></>,
    hardhat: <><path d="M2 18a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1v-2a1 1 0 0 0-1-1H3a1 1 0 0 0-1 1v2z"/><path d="M10 10V5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v5"/><path d="M4 15v-3a8 8 0 0 1 16 0v3"/></>,
  };
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">{icons[name]}</svg>;
};

const StatutBadge = ({ statut }) => <span className={`status-badge status-${statut}`}>{statut?.replace('_', ' ')}</span>;
const TypeBadge = ({ type }) => {
  const cls = { CDI: 'type-cdi', CDD: 'type-cdd', Stage: 'type-stage' };
  return <span className={`type-badge ${cls[type] || ''}`}>{type}</span>;
};

const OperantDashboard = () => {
  const [profil, setProfil] = useState(null);
  const [contrats, setContrats] = useState([]);
  const [chefs, setChefs] = useState([]);
  const [onglet, setOnglet] = useState('profil');
  const [form, setForm] = useState({ nom: '', famille: '' });
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);

  const charger = async () => {
    try {
      const [p, c, ch] = await Promise.all([getProfil(), getMonContrat(), getChefsService()]);
      setProfil(p.data); setContrats(c.data); setChefs(ch.data);
      setForm({ nom: p.data.nom, famille: p.data.famille || '' });
    } catch {}
    finally { setLoading(false); }
  };

  useEffect(() => { charger(); }, []);

  const handleModifier = async () => {
    await modifierProfil(form);
    setEditMode(false); charger();
  };

  const contratActif = contrats.find(c => c.statut === 'EN_COURS');

  const navItems = [
    { key: 'profil', label: 'Mon Profil', icon: 'user' },
    { key: 'contrat', label: 'Mon Contrat', icon: 'file' },
    { key: 'chefs', label: 'Chefs de service', icon: 'users' },
  ];

  const topbarTitles = {
    profil: { title: 'Mon Profil', sub: 'Consultez et modifiez vos informations' },
    contrat: { title: 'Mon Contrat', sub: 'Suivez le statut de votre contrat' },
    chefs: { title: 'Chefs de Service', sub: 'Liste des chefs de service disponibles' },
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-badge">
            <div className="logo-icon" style={{ background: 'linear-gradient(135deg, #3ecf8e, #2ba570)' }}>
              <Icon name="hardhat" size={18} />
            </div>
            <div className="logo-text">
              <span className="brand">GRH Contrats</span>
              <span className="module">M4 · Contrats</span>
            </div>
          </div>
        </div>
        <nav className="sidebar-nav">
          <div className="nav-label">Navigation</div>
          {navItems.map(item => (
            <button key={item.key} className={`nav-item ${onglet === item.key ? 'active' : ''}`} onClick={() => setOnglet(item.key)}>
              <Icon name={item.icon} size={16} /><span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div style={{ marginBottom: '12px', padding: '10px 12px', background: 'rgba(62,207,142,0.08)', borderRadius: '8px', border: '1px solid rgba(62,207,142,0.15)' }}>
            <div style={{ fontSize: '11px', color: '#555d78', fontFamily: 'IBM Plex Mono', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '4px' }}>Connecté</div>
            <div style={{ fontSize: '13px', color: '#3ecf8e', fontWeight: 600 }}>{getNom()}</div>
            <div style={{ fontSize: '11px', color: '#555d78' }}>Opérant</div>
          </div>
          {contratActif && (
            <div style={{ marginBottom: '12px', padding: '8px 12px', background: 'rgba(62,207,142,0.05)', borderRadius: '8px', border: '1px solid rgba(62,207,142,0.2)' }}>
              <div style={{ fontSize: '10px', color: '#555d78', fontFamily: 'IBM Plex Mono', textTransform: 'uppercase', marginBottom: '6px' }}>Contrat actif</div>
              <TypeBadge type={contratActif.typeContrat} />
            </div>
          )}
          <button className="btn btn-danger btn-sm" style={{ width: '100%', justifyContent: 'center' }} onClick={logout}>
            <Icon name="logout" size={14} /> Déconnexion
          </button>
        </div>
      </aside>

      <main className="main-content">
        <div className="topbar">
          <div className="topbar-left">
            <h1>{topbarTitles[onglet].title}</h1>
            <p>{topbarTitles[onglet].sub}</p>
          </div>
          <div className="topbar-right">
            {onglet === 'profil' && !editMode && (
              <button className="btn btn-secondary btn-sm" onClick={() => setEditMode(true)}>
                <Icon name="edit" size={14} /> Modifier le profil
              </button>
            )}
          </div>
        </div>

        <div className="page-body">
          {loading ? <div className="loading-overlay"><div className="spinner" /> Chargement...</div> : (
            <>
              {/* PROFIL */}
              {onglet === 'profil' && profil && (
                <div style={{ maxWidth: '640px' }}>
                  <div style={{ background: '#13161e', border: '1px solid #242838', borderRadius: '16px', overflow: 'hidden' }}>
                    <div style={{ background: 'linear-gradient(135deg, rgba(62,207,142,0.12), rgba(62,207,142,0.04))', borderBottom: '1px solid #242838', padding: '28px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                        <div style={{
                          width: '64px', height: '64px', borderRadius: '16px',
                          background: 'linear-gradient(135deg, #3ecf8e, #2ba570)',
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                          fontSize: '26px', fontWeight: 700, color: '#0d0f14', fontFamily: 'Sora',
                          flexShrink: 0,
                        }}>
                          {profil.nom?.charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <div style={{ fontFamily: 'Sora', fontWeight: 700, fontSize: '20px', color: '#e8eaf2' }}>{profil.nom}</div>
                          <div style={{ fontSize: '12px', color: '#555d78', fontFamily: 'IBM Plex Mono', marginTop: '4px' }}>ID #{profil.id} · {profil.login}</div>
                          <div style={{ marginTop: '8px' }}>
                            <span style={{ padding: '3px 10px', borderRadius: '5px', fontSize: '11.5px', fontFamily: 'IBM Plex Mono', background: 'rgba(62,207,142,0.15)', color: '#3ecf8e' }}>OPÉRANT</span>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div style={{ padding: '24px' }}>
                      {editMode ? (
                        <div className="form-grid">
                          <div className="form-group">
                            <label className="form-label">Nom</label>
                            <input className="form-input" value={form.nom} onChange={e => setForm({ ...form, nom: e.target.value })} />
                          </div>
                          <div className="form-group">
                            <label className="form-label">Famille</label>
                            <input className="form-input" value={form.famille} onChange={e => setForm({ ...form, famille: e.target.value })} />
                          </div>
                          <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '10px' }}>
                            <button className="btn btn-primary" onClick={handleModifier}><Icon name="check" size={14} /> Sauvegarder</button>
                            <button className="btn btn-secondary" onClick={() => setEditMode(false)}><Icon name="x" size={14} /> Annuler</button>
                          </div>
                        </div>
                      ) : (
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
                          {[
                            { label: 'NOM COMPLET', value: profil.nom },
                            { label: 'ÂGE', value: `${profil.age} ans` },
                            { label: 'FAMILLE', value: profil.famille || '—' },
                            { label: 'LOGIN', value: profil.login },
                          ].map(item => (
                            <div key={item.label}>
                              <div style={{ fontSize: '10px', fontFamily: 'IBM Plex Mono', color: '#555d78', letterSpacing: '0.8px', textTransform: 'uppercase', marginBottom: '6px' }}>{item.label}</div>
                              <div style={{ fontSize: '14px', color: '#e8eaf2', fontWeight: 500 }}>{item.value}</div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* CONTRAT */}
              {onglet === 'contrat' && (
                contrats.length === 0 ? (
                  <div className="table-wrap" style={{ maxWidth: '600px' }}>
                    <div className="empty-state">
                      <Icon name="file" size={32} /><h3>Aucun contrat</h3>
                      <p>Vous n'avez pas encore de contrat assigné.</p>
                    </div>
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '680px' }}>
                    {contrats.map(c => (
                      <div key={c.id} style={{ background: '#13161e', border: '1px solid #242838', borderRadius: '16px', overflow: 'hidden' }}>
                        <div style={{ padding: '18px 24px', borderBottom: '1px solid #242838', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <TypeBadge type={c.typeContrat} />
                            <span style={{ fontFamily: 'IBM Plex Mono', fontSize: '12px', color: '#555d78' }}>Contrat #{c.id}</span>
                          </div>
                          <StatutBadge statut={c.statut} />
                        </div>
                        <div style={{ padding: '20px 24px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
                          {[
                            { label: 'DATE DE DÉBUT', value: c.dateDebut },
                            { label: 'DATE DE FIN', value: c.dateFin || 'Non définie' },
                            { label: 'CATÉGORIE', value: c.categorie?.libelle || '—' },
                            { label: 'ÉCHELON', value: c.echelon?.code || '—' },
                            { label: 'INDICE SALARIAL', value: c.echelon?.indiceSalarial?.toLocaleString() || '—' },
                          ].map(item => (
                            <div key={item.label}>
                              <div style={{ fontSize: '10px', fontFamily: 'IBM Plex Mono', color: '#555d78', letterSpacing: '0.8px', textTransform: 'uppercase', marginBottom: '6px' }}>{item.label}</div>
                              <div style={{ fontSize: '14px', color: '#e8eaf2', fontWeight: 500 }}>{item.value}</div>
                            </div>
                          ))}
                        </div>
                      </div>
                    ))}
                  </div>
                )
              )}

              {/* CHEFS */}
              {onglet === 'chefs' && (
                <div className="table-wrap" style={{ maxWidth: '600px' }}>
                  <table className="contrat-table">
                    <thead><tr><th>#ID</th><th>Nom</th><th>Âge</th></tr></thead>
                    <tbody>
                      {chefs.length === 0
                        ? <tr><td colSpan={3}><div className="empty-state"><Icon name="users" size={32} /><h3>Aucun chef de service</h3></div></td></tr>
                        : chefs.map(ch => (
                          <tr key={ch.id}>
                            <td className="cell-id">#{ch.id}</td>
                            <td className="cell-employe">{ch.nom}</td>
                            <td>{ch.age}</td>
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
    </div>
  );
};

export default OperantDashboard;