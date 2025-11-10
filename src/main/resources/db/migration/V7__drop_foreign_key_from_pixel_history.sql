ALTER TABLE pixel_history
DROP CONSTRAINT IF EXISTS pixel_history_user_id_fkey;

ALTER TABLE pixel_history
    ALTER COLUMN user_id DROP NOT NULL;