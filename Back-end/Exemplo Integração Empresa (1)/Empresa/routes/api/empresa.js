const express = require("express");
const wrap = require("express-async-error-wrapper");
const axios = require("axios");

const router = express.Router();

const url_springboot = process.env.url_springboot;

router.get("/listar", wrap(async (req, res) => {
	try {
		const response = await axios.get(url_springboot + "/api/empresas");

		res.json(response.data);
	} catch (ex) {
		res.status(500).json("Exceção na comunicação com o servidor remoto: " + ex.message);
	}
}));

router.get("/obter", wrap(async (req, res) => {
	const id = parseInt(req.query["id"]);

	try {
		const response = await axios.get(url_springboot + "/api/empresas/" + id);

		res.json(response.data);
	} catch (ex) {
		res.status(500).json("Exceção na comunicação com o servidor remoto: " + ex.message);
	}
}));

router.post("/criar", wrap(async (req, res) => {
	const empresa = req.body;

	try {
		const response = await axios.post(url_springboot + "/api/empresas", empresa);

		res.json(response.data);
	} catch (ex) {
		res.status(500).json("Exceção na comunicação com o servidor remoto: " + ex.message);
	}
}));

router.put("/editar", wrap(async (req, res) => {
	const empresa = req.body;

	try {
		const response = await axios.put(url_springboot + "/api/empresas", empresa);

		res.json(response.data);
	} catch (ex) {
		res.status(500).json("Exceção na comunicação com o servidor remoto: " + ex.message);
	}
}));

router.delete("/excluir", wrap(async (req, res) => {
	const id = parseInt(req.query["id"]);

	try {
		const response = await axios.get(url_springboot + "/api/empresas/" + id);

		res.json(response.data);
	} catch (ex) {
		res.status(500).json("Exceção na comunicação com o servidor remoto: " + ex.message);
	}
}));

module.exports = router;
