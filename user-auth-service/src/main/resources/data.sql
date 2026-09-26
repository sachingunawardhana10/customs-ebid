INSERT INTO roles (id, name)
VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'BIDDER')
    ON CONFLICT (name) DO NOTHING;