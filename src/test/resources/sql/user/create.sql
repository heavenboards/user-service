INSERT INTO user_entity (id, username, password, role, first_name, last_name, account_non_expired, account_non_locked,
                         credentials_non_expired, enabled, created_at, updated_at)
VALUES ('e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795', 'registeredUser',
        '$2a$10$JqrceZDuA3g/h7dLuHbrD.GRoJKZdjmVcNvJunSbOsk1yxGOPtIie', 'USER', 'Ivan', 'Ivanov', TRUE, TRUE,
        TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
