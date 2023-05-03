ALTER TABLE room ADD COLUMN IF NOT EXISTS player_type varchar;

ALTER TABLE room ADD COLUMN created_at timestamp with time zone;

ALTER TABLE room ADD COLUMN changed_at timestamp with time zone;