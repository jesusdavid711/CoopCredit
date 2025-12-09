-- Insert admin user (password: admin123)
INSERT INTO users (username, password, email, role, active)
VALUES ('admin', '$2a$10$rYV8lN0Z8yP9LMZhZ1K5E.3fN3Z5vZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu', 'admin@coopcredit.com', 'ROLE_ADMIN', true);

-- Insert analyst user (password: analyst123)
INSERT INTO users (username, password, email, role, active)
VALUES ('analyst', '$2a$10$rYV8lN0Z8yP9LMZhZ1K5E.3fN3Z5vZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu', 'analyst@coopcredit.com', 'ROLE_ANALISTA', true);

-- Insert affiliate user (password: affiliate123)
INSERT INTO users (username, password, email, role, active)
VALUES ('affiliate', '$2a$10$rYV8lN0Z8yP9LMZhZ1K5E.3fN3Z5vZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu', 'affiliate@coopcredit.com', 'ROLE_AFILIADO', true);

-- Insert sample affiliates
INSERT INTO affiliates (document_number, name, salary, affiliation_date, status)
VALUES 
    ('1017654321', 'Juan Pérez', 3000000.00, '2023-01-15', 'ACTIVE'),
    ('9876543210', 'María García', 4500000.00, '2022-06-20', 'ACTIVE'),
    ('1234567890', 'Carlos López', 2500000.00, '2024-11-01', 'ACTIVE');
