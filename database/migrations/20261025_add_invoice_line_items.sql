CREATE TABLE IF NOT EXISTS bill_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  bill_id BIGINT NOT NULL,
  product_id BIGINT NULL,
  product_name VARCHAR(120) NOT NULL,
  product_sku VARCHAR(40) NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  line_total DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_bill_items_bill (bill_id),
  CONSTRAINT fk_bill_items_bill FOREIGN KEY (bill_id) REFERENCES bills(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_bill_items_product FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
