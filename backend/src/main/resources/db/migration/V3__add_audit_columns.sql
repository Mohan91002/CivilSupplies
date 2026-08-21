-- V3: Reconcile the schema with BaseEntity's audit columns.
--
-- Every entity extends BaseEntity, which maps created_at (NOT NULL, non-updatable)
-- and updated_at. V1 never defined updated_at on any table, omitted created_at from
-- categories entirely, and named the newsletter column subscribed_at. Because the
-- prod profile runs ddl-auto: none, nothing validated this -- Hibernate simply emitted
-- INSERT/UPDATE statements referencing columns that do not exist, so every write
-- would fail at runtime.

-- categories had no audit columns at all
ALTER TABLE categories ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE categories ADD COLUMN updated_at TIMESTAMP;

-- newsletter_subscribers used a bespoke name for the same value BaseEntity calls
-- created_at; nothing mapped subscribed_at, so renaming loses no data.
ALTER TABLE newsletter_subscribers RENAME COLUMN subscribed_at TO created_at;
ALTER TABLE newsletter_subscribers ADD COLUMN updated_at TIMESTAMP;

-- the remaining tables already had created_at, but none had updated_at
ALTER TABLE products ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE enquiries ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE quotes ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE admin_users ADD COLUMN updated_at TIMESTAMP;
