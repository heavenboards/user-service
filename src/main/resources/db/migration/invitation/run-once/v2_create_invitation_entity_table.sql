CREATE TABLE IF NOT EXISTS invitation_entity
(
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    project_id uuid NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES user_entity (id)
);
