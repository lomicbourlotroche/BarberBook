import React, { useState, useEffect } from 'react';

// ANALOGIE : App.jsx est le "Manager" qui décide quelle page montrer au client.
function App() {
  const [step, setStep] = useState(0); // 0=Home, 1=Form, 2=Services, 3=Agenda, 4=Cancel
  const [appointments, setAppointments] = useState([]);
  const [formData, setFormData] = useState({
    clientName: '',
    clientFirstName: '',
    phoneNumber: '',
    serviceName: '',
    date: '2024-02-01',
    timeSlot: '',
    price: 25.0
  });

  const slots = ["09:00", "10:00", "11:00", "14:00", "15:00", "16:00"];
  const services = [
    { name: "Coupe Classique", price: 20 },
    { name: "Barbe Traditionnelle", price: 15 },
    { name: "Pack Royal (Coupe + Barbe)", price: 30 }
  ];

  // ANALOGIE : Fetch est le "Téléphone" qui appelle le Backend.
  useEffect(() => {
    fetch('http://localhost:8080/api/rendezvous')
      .then(res => res.json())
      .then(data => setAppointments(data));
  }, []);

  const handleBooking = () => {
    fetch('http://localhost:8080/api/rendezvous', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    }).then(() => {
      alert("⚠️ Rappel : Toute suppression doit se faire 48h avant le RDV !");
      setStep(0);
    });
  };

  const handleCancel = (phone) => {
    fetch(`http://localhost:8080/api/rendezvous/${phone}`, {
      method: 'DELETE'
    }).then(() => {
      alert("RDV Supprimé !");
      setStep(0);
    });
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>💈 BarberBook</h1>

      {step === 0 && (
        <section>
          <h2>Bienvenue au Salon</h2>
          <p>Voici nos créneaux aujourd'hui :</p>
          <div style={{ display: 'flex', gap: '10px' }}>
            {slots.map(s => (
              <button key={s} onClick={() => setStep(1)} style={{ padding: '10px' }}>{s}</button>
            ))}
          </div>
          <br />
          <button onClick={() => setStep(1)} style={{ background: 'gold', padding: '15px' }}>Prendre un RDV</button>
          <hr />
          <button onClick={() => setStep(4)}>Annuler un RDV existant</button>
        </section>
      )}

      {step === 1 && (
        <section>
          <h2>Étape 1 : Vos Informations</h2>
          <input placeholder="Nom" onChange={e => setFormData({...formData, clientName: e.target.value})} /><br/>
          <input placeholder="Prénom" onChange={e => setFormData({...formData, clientFirstName: e.target.value})} /><br/>
          <input placeholder="Téléphone" onChange={e => setFormData({...formData, phoneNumber: e.target.value})} /><br/>
          <button onClick={() => setStep(2)}>Suivant</button>
        </section>
      )}

      {step === 2 && (
        <section>
          <h2>Étape 2 : Votre Formule</h2>
          {services.map(s => (
            <button key={s.name} onClick={() => {
              setFormData({...formData, serviceName: s.name, price: s.price});
              setStep(3);
            }}>
              {s.name} - {s.price}€
            </button>
          ))}
        </section>
      )}

      {step === 3 && (
        <section>
          <h2>Étape 3 : Choisissez le Créneau</h2>
          {slots.map(s => (
            <button key={s} onClick={() => {
              setFormData({...formData, timeSlot: s});
              handleBooking();
            }}>{s}</button>
          ))}
        </section>
      )}

      {step === 4 && (
        <section>
          <h2>Annulation</h2>
          <p>Entrez votre téléphone pour supprimer votre RDV :</p>
          <input placeholder="Téléphone" id="phoneCancel" />
          <button onClick={() => handleCancel(document.getElementById('phoneCancel').value)}>Valider la suppression</button>
        </section>
      )}
    </div>
  );
}

export default App;
