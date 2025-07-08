var message = 'CSC-317 startup template\n'
         + 'This template uses nodeJS, express, and express.static\n';

var port = 3000;
var path = require('path');
var express = require('express');
var app = express();
const fs = require('fs');

var StaticDirectory = path.join(__dirname, 'public');

app.use(express.static(StaticDirectory));
// Set up a route for the home page

app.get('/', (req, res) => {
    const currentDate = new Date().toLocaleString();

    fs.readFile(path.join(StaticDirectory, 'home.html'), 'utf8', (err, data) => {
      if (err) {
        res.set('Content-Type', 'text/plain');
        res.status(404).end('404 Not found');
        return;
      }

      const updatedData = data
        .replace('{{currentDate}}', currentDate);

      res.set('Content-Type','text/html');
      res.status(200);
      res.end(updatedData);
    });
});

app.listen(port, () => {
    console.log(`Listening on http://localhost:${port}/`);
});

console.log(message);
