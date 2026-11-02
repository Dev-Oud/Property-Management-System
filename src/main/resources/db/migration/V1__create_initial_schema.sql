-- Fresh-install schema. Existing deployments are baselined at version 1;
-- later migrations are then applied without recreating their existing tables.
CREATE TABLE user_tb1 (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    user_type VARCHAR(255),
    wallet_balance DOUBLE PRECISION NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES user_tb1(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    rejection_reason VARCHAR(500),
    owner_id BIGINT NOT NULL REFERENCES user_tb1(id),
    created_at TIMESTAMP
);

CREATE TABLE company_documents (
    id BIGSERIAL PRIMARY KEY,
    document_type VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    uploaded_by BIGINT NOT NULL REFERENCES user_tb1(id),
    uploaded_at TIMESTAMP NOT NULL
);
