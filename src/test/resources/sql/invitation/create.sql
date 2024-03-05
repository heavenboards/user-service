INSERT INTO user_entity (id, username, password, role, first_name, last_name, account_non_expired, account_non_locked,
                         credentials_non_expired, enabled, created_at, updated_at)
VALUES ('e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795', 'invitedUser',
        '$2a$10$JqrceZDuA3g/h7dLuHbrD.GRoJKZdjmVcNvJunSbOsk1yxGOPtIie', 'USER', 'Ivan', 'Ivanov', TRUE, TRUE,
        TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('2baed0f0-49b2-43fa-bec2-c3b4af8b2918', 'invitationSender',
        '$2a$10$JqrceZDuA3g/h7dLuHbrD.GRoJKZdjmVcNvJunSbOsk1yxGOPtIie', 'USER', 'Petr', 'Petrov', TRUE, TRUE,
        TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO invitation_entity (id, invited_user_id, invitation_sender, project_id)
VALUES ('625c0921-e767-4269-a98c-d9ff571bbb8c', 'e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795',
        '2baed0f0-49b2-43fa-bec2-c3b4af8b2918', 'bf9a55de-a3b4-4a7b-8435-8fdb73759cb7'), -- Петр пригласил Ивана
    ('b2308466-ee4b-4137-8cd7-6a8226b53525', 'e8e20bfc-0d9c-49e7-b30f-1a6abf3b2795',
     '2baed0f0-49b2-43fa-bec2-c3b4af8b2918', '42f73e88-dd3e-46e2-b1e5-33cc990eb84a'); -- Петр пригласил Ивана
