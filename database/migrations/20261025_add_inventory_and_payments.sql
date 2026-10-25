-- Apply after backing up the database. Hibernate ddl-auto=update also creates these tables,
-- but this migration is provided for environments that manage schema changes explicitly.
CREATE TABLE IF NOT EXISTS products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  sku VARCHAR(40) NOT NULL,
  category VARCHAR(60) NULL,
  brand VARCHAR(80) NULL,
  purchase_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  selling_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  stock_quantity INT NOT NULL DEFAULT 0,
  reorder_level INT NOT NULL DEFAULT 5,
  description VARCHAR(1000) NULL,
  active BIT(1) NOT NULL DEFAULT b'1',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_sku (sku)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bill_payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  bill_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  payment_mode VARCHAR(30) NULL,
  paid_at DATETIME NOT NULL,
  note VARCHAR(500) NULL,
  PRIMARY KEY (id),
  KEY idx_payment_bill_date (bill_id, paid_at),
  CONSTRAINT fk_bill_payments_bill FOREIGN KEY (bill_id) REFERENCES bills(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
