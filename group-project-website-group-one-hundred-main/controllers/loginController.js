const bcrypt = require('bcryptjs');
const pool = require('../database');

exports.login = (req, res) => {
    const { username, password } = req.body;

    pool.query('SELECT * FROM users WHERE username = ?', [username], (error, results) => {
        if (error) {
            console.error('Error:', error);
            return res.status(500).json({ success: false, message: 'Server error' });
        } else if (results.length === 0) {
            return res.status(401).json({ success: false, message: 'Authentication failed' });
        } else {
            const user = results[0];
            bcrypt.compare(password, user.password, (err, isMatch) => {
                if (err) {
                    console.error('Error:', err);
                    return res.status(500).json({ success: false, message: 'Server error' });
                } else if (isMatch) {
                    return res.status(200).json({ success: true, message: 'Login successful' });
                } else {
                    return res.status(401).json({ success: false, message: 'Authentication failed' });
                }
            });
        }
    });
};
