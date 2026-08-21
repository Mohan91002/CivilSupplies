-- V1: Initial schema for Civil Supplies application

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    slug        VARCHAR(120)  NOT NULL UNIQUE,
    image_url   VARCHAR(500),
    sort_order  INT           NOT NULL DEFAULT 0
);

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200)  NOT NULL,
    slug        VARCHAR(220)  NOT NULL UNIQUE,
    category_id BIGINT        NOT NULL REFERENCES categories(id),
    brand       VARCHAR(100),
    unit        VARCHAR(50),
    description TEXT,
    image_url   VARCHAR(500),
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_slug     ON products(slug);

CREATE TABLE enquiries (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150)  NOT NULL,
    phone        VARCHAR(30)   NOT NULL,
    email        VARCHAR(200)  NOT NULL,
    city         VARCHAR(100),
    project_type VARCHAR(60),
    materials    VARCHAR(500),
    quantity     VARCHAR(200),
    message      TEXT,
    status       VARCHAR(20)   NOT NULL DEFAULT 'NEW',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quotes (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150)  NOT NULL,
    phone           VARCHAR(30)   NOT NULL,
    email           VARCHAR(200)  NOT NULL,
    project_details TEXT,
    site_location   VARCHAR(300),
    boq_filename    VARCHAR(300),
    boq_file_url    VARCHAR(500),
    timeline        VARCHAR(100),
    status          VARCHAR(20)   NOT NULL DEFAULT 'NEW',
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin_users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(200)  NOT NULL UNIQUE,
    password_hash VARCHAR(200)  NOT NULL,
    full_name     VARCHAR(150),
    roles         VARCHAR(200)  NOT NULL DEFAULT 'ROLE_VIEWER',
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE newsletter_subscribers (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(200)  NOT NULL UNIQUE,
    subscribed_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
