var express = require('express');
var app = express();
//var passport = require('passport');
var bodyParser = require('body-parser');
// app.use(passport.initialize());
// app.use(passport.session());

//app.use('/auth', require('./routes/auth'));// 127.0.0.1:3000/auth
app.use(bodyParser.json()); // for parsing application/json
app.use('/', require('./routes/index'));

app.get('*', function (req, res) {
	res.redirect("/");
});

var port = process.env.PORT || 3000;
var ip = process.env.IP || undefined;

app.listen(port, ip, () => {
	console.log(`Server started on port ${port}`);
});

module.exports = app;
