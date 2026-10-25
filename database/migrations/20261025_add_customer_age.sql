-- Optional customer age field. Safe for existing customer rows.
-- Hibernate ddl-auto=update also adds this column automatically in the current configuration.
ALTER TABLE customers ADD COLUMN age INT NULL;
