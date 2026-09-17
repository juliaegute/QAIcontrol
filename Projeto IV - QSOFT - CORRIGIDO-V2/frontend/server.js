const express = require('express');

const app = express();
const PORT = process.env.PORT || 3000;
const API = process.env.API_BASE_URL || 'http://localhost:8080';

app.use(express.json());

// BFF: endpoint orientado à tela. Agrega dados de empresas e consumos.
app.get('/bff/dashboard', async (_req, res) => {
  try {
    const [empresasResponse, usagesResponse] = await Promise.all([
      fetch(`${API}/api/empresa`),
      fetch(`${API}/api/usages`)
    ]);

    if (!empresasResponse.ok || !usagesResponse.ok) {
      return res.status(502).json({ error: 'Falha ao consultar o backend Java' });
    }

    const empresas = await empresasResponse.json();
    const usages = await usagesResponse.json();

    const totalTokens = usages.reduce((sum, usage) => sum + Number(usage.tokens || 0), 0);

    res.json({
      totalEmpresas: empresas.length,
      totalConsumos: usages.length,
      totalTokens,
      empresas,
      usages
    });
  } catch (err) {
    res.status(502).json({ error: 'Backend indisponível', detail: err.message });
  }
});

// Proxy do BFF para operações CRUD/REST simples do front.
app.use('/api', async (req, res) => {
  const targetUrl = `${API}${req.originalUrl}`;

  try {
    console.log(`[BFF] ${req.method} ${req.originalUrl} -> ${targetUrl}`);

    const options = {
      method: req.method,
      headers: { 'Accept': req.headers.accept || 'application/json' }
    };

    if (!['GET', 'HEAD'].includes(req.method)) {
      options.headers['Content-Type'] = 'application/json';
      options.body = JSON.stringify(req.body ?? {});
    }

    const response = await fetch(targetUrl, options);
    const contentType = response.headers.get('content-type');
    const body = await response.text();

    res.status(response.status);
    if (contentType) res.set('Content-Type', contentType);
    res.send(body);
  } catch (err) {
    res.status(502).json({ error: 'Backend indisponível', detail: err.message });
  }
});

app.use(express.static('public'));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Front/BFF em http://localhost:${PORT}`);
  console.log(`Java backend: ${API}`);
});
