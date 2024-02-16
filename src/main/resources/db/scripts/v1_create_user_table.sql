CREATE TABLE IF NOT EXISTS "user"
(
    id uuid PRIMARY KEY,
    username varchar(64) NOT NULL UNIQUE,
    password varchar(2048) NOT NULL,
    role varchar(32) NOT NULL,
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    deleted bool NOT NULL DEFAULT FALSE,
    created_at timestamp WITH TIME ZONE,
    updated_at timestamp WITH TIME ZONE
);
