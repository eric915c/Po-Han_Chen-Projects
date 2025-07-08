const bcrypt = require('bcryptjs');
const pool = require('../database');

exports.signup = (req, res) => {
    const { username, email, password, address, city, state, zip } = req.body;

    // Check if username already exists
    pool.query('SELECT * FROM users WHERE username = ?', [username], (error, results) => {
        if (error) {
            console.error('Error:', error);
            return res.status(500).json({ success: false, message: 'Server error' });
        }
        if (results.length > 0) {
            return res.status(400).json({ success: false, message: 'Username already exists' });
        }

        // Hash password
        bcrypt.hash(password, 10, (err, hash) => {
            if (err) {
                console.error('Error:', err);
                return res.status(500).json({ success: false, message: 'Server error' });
            }

            // Insert user into database
            pool.query('INSERT INTO users (username, email, password, address, city, state, zip) VALUES (?, ?, ?, ?, ?, ?, ?)',
                [username, email, hash, address, city, state, zip], (error, results) => {
                    if (error) {
                        console.error('Error:', error);
                        return res.status(500).json({ success: false, message: 'Server error' });
                    }
                    res.status(200).json({ success: true, message: 'User registered' });
                });
        });
    });
};
