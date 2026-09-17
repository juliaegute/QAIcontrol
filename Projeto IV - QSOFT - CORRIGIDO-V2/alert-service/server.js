const express = require('express');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 3001;
app.use(express.json());

const alerts = [];

app.get('/health', (_req, res) => {
  res.json({ status: 'UP', service: 'qsoft-alert-service' });
});

app.post('/api/alerts/evaluate', (req, res) => {
  const { empresaId, tokens, model } = req.body;

  if (!empresaId || !Number.isFinite(Number(tokens)) || !model) {
    return res.status(400).json({
      level: 'ERROR',
      message: 'empresaId, tokens e model são obrigatórios'
    });
  }

  const tokenValue = Number(tokens);
  let level = 'INFO';
  let message = 'Consumo dentro do esperado';

  if (tokenValue >= 10000) {
    level = 'CRITICAL';
    message = `Consumo crítico: ${tokenValue} tokens no modelo ${model}`;
  } else if (tokenValue >= 5000) {
    level = 'WARNING';
    message = `Atenção: consumo elevado de ${tokenValue} tokens no modelo ${model}`;
  }

  const alert = {
    id: crypto.randomUUID(), empresaId, model, tokens: tokenValue,
    level, message, createdAt: new Date().toISOString()
  };

  alerts.unshift(alert);
  if (alerts.length > 50) alerts.pop();

  return res.json({ level, message });
});

app.get('/api/alerts', (_req, res) => res.json(alerts));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`qsoft-alert-service disponível na porta ${PORT}`);
});
