CREATE TABLE IF NOT EXISTS invitation_entity
(
    id uuid PRIMARY KEY,
    invited_user_id uuid NOT NULL,
    invitation_sender uuid NOT NULL,
    project_id uuid NOT NULL,
    FOREIGN KEY (invited_user_id)
        REFERENCES user_entity (id),
    FOREIGN KEY (invitation_sender)
        REFERENCES user_entity (id)
);
