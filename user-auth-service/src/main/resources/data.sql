INSERT INTO roles (id, name)
VALUES
    (gen_random_uuid(), 'ADMIN'),
    (gen_random_uuid(), 'BIDDER'),
    (gen_random_uuid(), 'AUCTION_OFFICER')
ON CONFLICT (name) DO NOTHING;
