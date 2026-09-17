//****************************************************************
// Logo ao abrir o projeto podem aparecer alguns erros nesse
// código, bem como nas dependências do Solution Explorer!
// É normal que isso ocorra, até que o processo de carregamento
// (exibido no canto inferior esquerdo) tenha avançado!
//****************************************************************

//****************************************************************
// Para utilizar o view engine ejs, adicionamos a linha
// "ejs": "^2.6.1", às dependências do arquivo package.json
// Isso significa que queremos a versão 2.6.1 do ejs, ou uma
// superior, mas compatível!
// https://docs.npmjs.com/files/package.json
//
// Depois de alterado o arquivo, é preciso clicar com o botão
// direito sobre a dependência, no Solution Explorer, e clicar
// na opção "Install Missing npm Package(s)"
//****************************************************************

const express = require("express");
const path = require("path");
const wrap = require("express-async-error-wrapper");
const cookieParser = require("cookie-parser"); // https://stackoverflow.com/a/16209531/3569421

require("dotenv").config({ encoding: "utf8" });

// Configura o cache, para armazenar as 200 últimas páginas
// já processadas, por ordem de uso
const ejs = require("ejs");

const LRU = require("lru-cache");
const cache = new LRU({
	max: 200
});

// Ver comentários no pacote https://github.com/tech-espm/labs-teem :)
if (!("remove" in cache)) {
	if (("delete" in cache))
		cache.remove = cache.delete;
	else if (("del" in cache))
		cache.remove = cache.delete;
}
if (!("reset" in cache)) {
	if (("clear" in cache))
		cache.reset = cache.clear;
}
ejs.cache = cache;

const app = express();

// Não queremos o header X-Powered-By
app.disable("x-powered-by");
// Não queremos o header ETag nas views
app.disable("etag");

// Explica para o express qual será o diretório de onde serviremos os
// arquivos estáticos (js, css, imagens etc...)
app.use("/public", express.static(path.join(__dirname, "public"), {
	cacheControl: true,
	etag: false,
	maxAge: "30d"
}));

app.use(cookieParser());

app.use(express.json({ limit: 10485760, inflate: true, strict: false })); // http://expressjs.com/en/api.html#express.json
app.use(express.urlencoded({ extended: true })); // http://expressjs.com/en/api.html#express.urlencoded

// Configura o diretório de onde tirar as views
app.set("views", path.join(__dirname, "views"));

// Define o view engine como o ejs e importa o express-ejs-layouts
// https://www.npmjs.com/package/express-ejs-layouts, pois o ejs não
// suporta layouts nativamente: https://www.npmjs.com/package/ejs#layouts
app.set("view engine", "ejs");
app.use(require("express-ejs-layouts"));

// Nosso middleware para evitar cache das páginas e api
// (deixa depois do static, pois os arquivos static devem usar cache e coisas)
app.use((req, res, next) => {
	res.header("Cache-Control", "private, no-cache, no-store, must-revalidate");
	res.header("Expires", "-1");
	res.header("Pragma", "no-cache");
	next();
});

// Especifica quais módulos serão responsáveis por servir cada rota,
// a partir dos endereços requisitados pelo cliente
//
// A string no primeiro parâmetro representa o começo do caminho
// requisitado. Vamos ver alguns exemplos de caminhos, e como eles
// seriam tratados pelo Express
//
// Caminho completo   Caminho do tratador   Tratador e resultado
// /                  /                     index (OK)
// /usuario           /                     usuario (OK)
// /usuario/novo      /novo                 usuario (OK)
// /usuario/alterar   /alterar              usuario (Erro, não temos /alterar em usuario)
// /outra             /outra                index (OK)
// /usuarioteste      /usuarioteste         index (Erro, não temos /usuarioteste em index)
//
// https://expressjs.com/en/guide/routing.html

// Cadastros simples
app.use("/", require("./routes/index"));
app.use("/empresa", require("./routes/empresa"));
app.use("/api/empresa", require("./routes/api/empresa"));

// Depois de registrados todos os caminhos das rotas e seus
// tratadores, registramos os tratadores que serão chamados
// caso nenhum dos tratadores anteriores tenha devolvido alguma
// resposta
//
// O Express diferencia um tratador regular (como esse aqui) de um tratador
// de erros, como os dois abaixo, pela quantidade de parâmetros!!!
//
// Isso é possível, porque em JavaScript, f.length retorna a quantidade
// de parâmetros declarados na função f!!!
//
// Registra os tratadores de erro
app.use((req, res, next) => {
	res.status(404);

	res.json("Não encontrado");
});

app.use((err, req, res, next) => {
	res.status(err.status || 500);

	res.render("erro", { mensagem: err.message });
});

const ip = process.env.IP || "127.0.0.1";
const porta = parseInt(process.env.PORT) || 3000;

const server = app.listen(porta, ip, () => {
	console.log(`Servidor Express executando em ${ip}:${porta}`);
});
