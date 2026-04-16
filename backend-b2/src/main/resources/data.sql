-- Seed demo data for local H2 (dev profile)
INSERT INTO product_category (category_code, category_name, created_by, created_at, updated_by, updated_at, deleted_flag)
VALUES ('CAT-001', '默认分类', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 0);

INSERT INTO product_category (category_code, category_name, created_by, created_at, updated_by, updated_at, deleted_flag)
VALUES ('CAT-002', '电子产品', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 0);

