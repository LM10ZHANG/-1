-- MySQL 8.x schema draft (Backend B - master data)
-- Notes:
-- 1) All tables include audit fields: created_by/created_at/updated_by/updated_at/deleted_flag
-- 2) This file is a “初版建表草稿”，课程联调时可直接用或迁移到 Flyway/Liquibase。

CREATE TABLE customer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_code VARCHAR(50) NOT NULL UNIQUE,
  customer_name VARCHAR(120) NOT NULL,
  customer_level VARCHAR(20),
  customer_type VARCHAR(20),
  industry VARCHAR(50),
  source VARCHAR(50),
  province VARCHAR(50),
  city VARCHAR(50),
  address VARCHAR(255),
  owner_user_id BIGINT,
  credit_limit DECIMAL(18,2),
  current_ar_amount DECIMAL(18,2),
  follow_status VARCHAR(20),
  status TINYINT DEFAULT 1,
  remark VARCHAR(255),
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_customer_name (customer_name),
  INDEX idx_customer_owner (owner_user_id),
  INDEX idx_customer_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_contact (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  name VARCHAR(50),
  mobile VARCHAR(20),
  email VARCHAR(100),
  position VARCHAR(50),
  wechat VARCHAR(50),
  is_primary TINYINT DEFAULT 0,
  remark VARCHAR(255),
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_customer_contact_customer_id (customer_id),
  INDEX idx_customer_contact_mobile (mobile),
  INDEX idx_customer_contact_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_followup (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  follow_user_id BIGINT,
  follow_type VARCHAR(20),
  content TEXT,
  next_follow_time DATETIME,
  follow_result VARCHAR(50),
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_customer_followup_customer_id (customer_id),
  INDEX idx_customer_followup_follow_user_id (follow_user_id),
  INDEX idx_customer_followup_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_code VARCHAR(50) UNIQUE,
  category_name VARCHAR(120) NOT NULL,
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_product_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_spu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  spu_code VARCHAR(50) NOT NULL UNIQUE,
  spu_name VARCHAR(120) NOT NULL,
  category_id BIGINT,
  brand_name VARCHAR(50),
  unit_name VARCHAR(20),
  status TINYINT DEFAULT 1,
  description TEXT,
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_product_spu_category_id (category_id),
  INDEX idx_product_spu_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product_sku (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  spu_id BIGINT NOT NULL,
  sku_code VARCHAR(50) NOT NULL UNIQUE,
  sku_name VARCHAR(120) NOT NULL,
  spec_json TEXT,
  barcode VARCHAR(50),
  sale_price DECIMAL(18,2),
  cost_price DECIMAL(18,2),
  tax_rate DECIMAL(5,2),
  stock_warn_qty INT,
  status TINYINT DEFAULT 1,
  created_by BIGINT,
  created_at DATETIME,
  updated_by BIGINT,
  updated_at DATETIME,
  deleted_flag TINYINT DEFAULT 0,
  INDEX idx_product_sku_spu_id (spu_id),
  INDEX idx_product_sku_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

