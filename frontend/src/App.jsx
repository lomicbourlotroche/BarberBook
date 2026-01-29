import React, { useState, useEffect } from 'react';

// ANALOGIE : App.jsx est le "Manager" qui décide quelle page montrer au client.
function App() {
  const [step, setStep] = useState(0);
  const [services, setServices] = useState([]);
  const [slots, setSlots] = useState([]);
  const [formData, setFormData] = useState({
    clientName: '',
    clientFirstName: '',
    phoneNumber: '',
    serviceName: '',
    date: new Date().toISOString().split('T')[0],
    timeSlot: '',
    price: 0.0
  });

  // ANALOGIE : Fetch est le "Téléphone" qui appelle le Backend pour les infos du jour.
  useEffect(() => {
    fetch('http://localhost:8080/api/config/services')
      .then(res => res.json())
      .then(data => setServices(data));

    fetch('http://localhost:8080/api/config/slots')
      .then(res => res.json())
      .then(data => setSlots(data));
  }, []);

  const handleBooking = () => {
    fetch('http://localhost:8080/api/rendezvous', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    }).then(() => {
      alert("✅ Rendez-vous confirmé ! ⚠️ Rappel : Suppression obligatoire 48h avant.");
      setStep(0);
    });
  };

  const handleCancel = (phone) => {
    if (!phone) return alert("Veuillez entrer votre numéro.");
    fetch(`http://localhost:8080/api/rendezvous/${phone}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(data => {
        alert(data.message);
        if (data.status === 'success') setStep(0);
      });
  };

  const selectPreSlot = (time) => {
    setFormData({ ...formData, timeSlot: time });
    setStep(1);
  };

  return (
    <div className="container">
      <header>
        <h1>🌿 Nomad'Tif</h1>
        <p className="subtitle">Coiffure Nomade dans les Monts d'Arrée</p>
      </header>

      {step === 0 && (
        <section className="presentation">
          <div className="bio">
            <h2>Bienvenue dans ma bulle de bien-être</h2>
            <p>
              Moi c'est <strong>Solène</strong>. Avec <em>Nomad'Tif</em>, je sillonne les routes des Monts d'Arrée avec ma caravane aménagée en salon de coiffure.
              Je vous propose une expérience unique, naturelle et authentique, directement au cœur de nos villages.
            </p>
          </div>

          <div className="agenda-preview">
            <h3>Mes prochains passages</h3>
            <p className="hint">Cliquez sur un créneau pour réserver immédiatement</p>
            <div className="grid-slots">
              {slots.length > 0 ? slots.map(s => (
                <button key={s.id} className="slot-btn" onClick={() => selectPreSlot(s.time)}>{s.time}</button>
              )) : <p>Aucun créneau disponible pour le moment.</p>}
            </div>
          </div>

          <button className="primary-btn" onClick={() => setStep(1)}>Prendre un rendez-vous</button>
          <button className="secondary-btn" onClick={() => setStep(4)}>Annuler un RDV</button>
        </section>
      )}

      {step === 1 && (
        <section className="card">
          <h2>Étape 1 : Vos Informations</h2>
          <div className="form-group">
            <input placeholder="Votre Nom" onChange={e => setFormData({ ...formData, clientName: e.target.value })} />
            <input placeholder="Votre Prénom" onChange={e => setFormData({ ...formData, clientFirstName: e.target.value })} />
            <input placeholder="Numéro de Téléphone" onChange={e => setFormData({ ...formData, phoneNumber: e.target.value })} />
          </div>
          <button className="primary-btn" onClick={() => setStep(2)}>Choisir une prestation</button>
          <button className="text-btn" onClick={() => setStep(0)}>Retour</button>
        </section>
      )}

      {step === 2 && (
        <section className="card">
          <h2>Étape 2 : Votre Prestation</h2>
          <div className="prestation-list">
            {services.map(s => (
              <div key={s.id} className="prestation-item" onClick={() => {
                setFormData({ ...formData, serviceName: s.name, price: s.price });
                setStep(3);
              }}>
                <span>{s.name}</span>
                <span className="price">{s.price}€</span>
              </div>
            ))}
          </div>
          <button className="text-btn" onClick={() => setStep(1)}>Retour</button>
        </section>
      )}

      {step === 3 && (
        <section className="card">
          <h2>Étape 3 : Confirmer le Créneau</h2>
          <p>Vous avez choisi : <strong>{formData.serviceName}</strong></p>
          <div className="grid-slots">
            {slots.map(s => (
              <button
                key={s.id}
                className={`slot-btn ${formData.timeSlot === s.time ? 'selected' : ''}`}
                onClick={() => setFormData({ ...formData, timeSlot: s.time })}
              >
                {s.time}
              </button>
            ))}
          </div>
          <button className="primary-btn" disabled={!formData.timeSlot} onClick={handleBooking}>Confirmer le RDV</button>
          <button className="text-btn" onClick={() => setStep(2)}>Retour</button>
        </section>
      )}

      {step === 4 && (
        <section className="card">
          <h2>Annulation</h2>
          <p>Entrez votre numéro de téléphone pour annuler votre rendez-vous :</p>
          <input placeholder="Numéro de téléphone" id="phoneCancel" />
          <button className="danger-btn" onClick={() => handleCancel(document.getElementById('phoneCancel').value)}>Confirmer la suppression</button>
          <button className="text-btn" onClick={() => setStep(0)}>Retour</button>
        </section>
      )}
    </div>
  );
}

export default App;
