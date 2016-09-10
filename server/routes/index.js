var express = require('express');
var router = express.Router();

var multer = require('multer'); // v1.0.5
var upload = multer(); // for parsing multipart/form-data

router.post('/', upload.array(), (req, res, next) => {
  console.log(req.headers['user-agent'], " - Command received: ", req.body);
  res.status(200).send({
    serverResponse: "Command received"
  });
});

router.post('/confirm', upload.array(), (req, res, next) => {
  console.log(req.headers['user-agent'], " - Confirm received: ", req.body);
  res.sendStatus(200);
})

module.exports = router;
