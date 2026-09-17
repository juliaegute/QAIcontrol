const express = require("express");
const wrap = require("express-async-error-wrapper");

const router = express.Router();

router.get("/criar", wrap(async (req, res) => {
	res.render("empresa/criar");
}));

router.get("/editar", wrap(async (req, res) => {
	res.render("empresa/editar");
}));

router.get("/listar", wrap(async (req, res) => {
	res.render("empresa/listar");
}));

module.exports = router;
