-- First ensure admin role exists
INSERT INTO roles (id, name)
SELECT 3, 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE id = 3
);

-- Then create admin user if it doesn't exist
-- Insert admin user with BCrypt hashed password 'admin123'
INSERT INTO users (id, username, password, role_id)
SELECT 1, 'admin', '$2a$10$cHmdrVihh7KKe7Mmt6BziemXToe/e3YfHjLIHr1utVj7IFSuAZIqy', 3
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);