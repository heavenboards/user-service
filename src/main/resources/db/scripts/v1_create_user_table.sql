CREATE TABLE IF NOT EXISTS user_entity
(
    id uuid PRIMARY KEY,
    username varchar(64) NOT NULL UNIQUE,
    password varchar(2048) NOT NULL,
    role varchar(32) NOT NULL,
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    account_non_expired bool NOT NULL DEFAULT TRUE,
    account_non_locked bool NOT NULL DEFAULT TRUE,
    credentials_non_expired bool NOT NULL DEFAULT TRUE,
    enabled bool NOT NULL DEFAULT TRUE,
    created_at timestamp WITH TIME ZONE,
    updated_at timestamp WITH TIME ZONE
);
