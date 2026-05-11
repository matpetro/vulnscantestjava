-- Seed data for development / testing
INSERT INTO assets (hostname, ip_address, asset_type, environment, os_name, os_version, created_at)
VALUES
    ('web-prod-01',  '10.0.1.10',  'server',      'production',  'Ubuntu', '20.04.5', NOW()),
    ('web-prod-02',  '10.0.1.11',  'server',      'production',  'Ubuntu', '20.04.5', NOW()),
    ('db-prod-01',   '10.0.2.10',  'server',      'production',  'RHEL',   '8.5',     NOW()),
    ('ci-runner-01', '10.0.3.10',  'server',      'staging',     'Ubuntu', '22.04.1', NOW()),
    ('dev-laptop-42','192.168.1.50','workstation', 'development', 'macOS',  '13.2',    NOW());
