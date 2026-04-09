CREATE TABLE drug (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    generic_name VARCHAR(128),
    spec VARCHAR(128),
    unit VARCHAR(32) NOT NULL,
    manufacturer VARCHAR(128),
    barcode VARCHAR(64) NOT NULL UNIQUE,
    barcode_type VARCHAR(32),
    low_stock_threshold INT NOT NULL DEFAULT 0,
    active BIT NOT NULL DEFAULT b'1',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE drug_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(64) NOT NULL,
    production_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    purchase_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_batch_drug FOREIGN KEY (drug_id) REFERENCES drug(id),
    CONSTRAINT uk_batch UNIQUE (drug_id, batch_no)
);

CREATE TABLE inventory_txn (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    type VARCHAR(16) NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    reference_no VARCHAR(64) NOT NULL,
    remark VARCHAR(128),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_txn_drug FOREIGN KEY (drug_id) REFERENCES drug(id),
    CONSTRAINT fk_txn_batch FOREIGN KEY (batch_id) REFERENCES drug_batch(id)
);

CREATE TABLE inbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    supplier_name VARCHAR(128) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE inbound_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    scan_code VARCHAR(64) NOT NULL,
    batch_no VARCHAR(64) NOT NULL,
    production_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    purchase_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_in_item_order FOREIGN KEY (order_id) REFERENCES inbound_order(id),
    CONSTRAINT fk_in_item_drug FOREIGN KEY (drug_id) REFERENCES drug(id)
);

CREATE TABLE outbound_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    receiver VARCHAR(128) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE outbound_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    scan_code VARCHAR(64) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_out_item_order FOREIGN KEY (order_id) REFERENCES outbound_order(id),
    CONSTRAINT fk_out_item_drug FOREIGN KEY (drug_id) REFERENCES drug(id)
);

CREATE INDEX idx_drug_name ON drug(name);
CREATE INDEX idx_batch_drug_expiry ON drug_batch(drug_id, expiry_date);
CREATE INDEX idx_txn_batch_time ON inventory_txn(batch_id, created_at);
