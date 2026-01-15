-- =====================================================
-- SCHEMA INITIALIZATION FOR E-COMMERCE MODULE
-- =====================================================

BEGIN;

CREATE TABLE IF NOT EXISTS address (
    address_id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(50),
    province VARCHAR(100),
    amphor VARCHAR(100),
    district VARCHAR(100),
    zip_code VARCHAR(10),
    addr_num VARCHAR(100),
    detail VARCHAR(255),
    received_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS profile (
    profile_id BIGSERIAL PRIMARY KEY,
    profile_name VARCHAR(100),
    profile_sname VARCHAR(100),
    profile_role INT, -- 1=customer, 2=admin
    address_id BIGINT UNIQUE, --FK 1:1, but admin can have no addr.
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_profile_address
        FOREIGN KEY (address_id)
        REFERENCES address(address_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cart (
    cart_id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT UNIQUE NOT NULL, --FK 1:1
    created_at DATE DEFAULT CURRENT_DATE,
    note VARCHAR(255),
    CONSTRAINT fk_cart_profile
        FOREIGN KEY (profile_id)
        REFERENCES profile(profile_id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS category (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL,
    category_img VARCHAR(255),
    category_product_path VARCHAR(255),
    category_priority BIGINT
);

CREATE TABLE IF NOT EXISTS product (
    product_id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL, --FK 1:M
    product_name VARCHAR(255) NOT NULL,
    product_detail VARCHAR(255),
    product_img VARCHAR(255),
    product_price INT NOT NULL CHECK (product_price >= 0),
    product_stock INT NOT NULL DEFAULT 0 CHECK (product_stock >= 0),
    is_active INT DEFAULT 1, -- 0=inactive, 1=active
    created_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES category(category_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cart_item (
    cart_item_id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL, --FK 1:M
    product_id BIGINT NOT NULL, --FK 1:1
    qty INT NOT NULL CHECK (qty > 0),
    line_total INT NOT NULL CHECK (line_total >= 0),
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cartitem_cart
        FOREIGN KEY (cart_id)
        REFERENCES cart(cart_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS promotion_product (
    promotion_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL, --FK 1:1
    promotion_img VARCHAR(255),
    CONSTRAINT fk_promotion_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS discount_code (
    discount_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    value INT CHECK (value >= 0), --not %
    is_active INT DEFAULT 1, -- 0=inactive, 1=active
    qty INT DEFAULT 0 CHECK (qty >= 0),
    promotion_id BIGINT, --FK 1:1
    CONSTRAINT fk_dc_rec
        FOREIGN KEY (promotion_id)
        REFERENCES promotion_product(promotion_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    order_id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL, --FK 1:M
    address_id BIGINT NOT NULL, --FK 1:M
    status INT DEFAULT 1, -- 1=pending, 2=fulfilled
    subtotal INT NOT NULL CHECK (subtotal >= 0),
    delivery_fee INT DEFAULT 0 CHECK (delivery_fee >= 0),
    grand_total INT NOT NULL CHECK (grand_total >= 0),
    created_at TIMESTAMP DEFAULT NOW(),
    fulfilled_at TIMESTAMP,
    fulfilled_by VARCHAR(100),
    CONSTRAINT fk_orders_profile
        FOREIGN KEY (profile_id)
        REFERENCES profile(profile_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id)
        REFERENCES address(address_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS inventory_transaction (
    transaction_id BIGSERIAL PRIMARY KEY,
    qty_change INT NOT NULL,
    transaction_type INT NOT NULL, -- 1=IN, 2=SOLD, 3=OUT
    created_at TIMESTAMP DEFAULT NOW(),
    product_id BIGINT NOT NULL, --FK 1:M
    order_id BIGINT, --FK 1:M
    CONSTRAINT fk_invt_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_invt_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_sold_has_order
        CHECK (transaction_type != 2 OR order_id IS NOT NULL),
    CONSTRAINT chk_valid_transaction_type
        CHECK (transaction_type BETWEEN 1 AND 3)
);


CREATE TABLE IF NOT EXISTS order_item (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL, --FK 1:M
    product_id_snapshot BIGINT,
    product_name VARCHAR(255) NOT NULL,
    product_detail VARCHAR(255),
    product_price INT NOT NULL CHECK (product_price >= 0),
    qty INT NOT NULL CHECK (qty > 0),
    line_total INT NOT NULL CHECK (line_total >= 0),
    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE
);





-- =====================================================
-- 3. SEED DATA (Idempotent with ON CONFLICT) - AI generated
-- =====================================================

-- Categories (Master Data)
INSERT INTO category (category_name, category_img, category_product_path, category_priority)
VALUES
    ('pizza', 'pizza.png', '/category/pizza', 1),
    ('appetizer', 'appetizer.png', '/category/appetizer', 2),
    ('drink', 'drink.png', '/category/drink', 3)
    ON CONFLICT (category_name) DO UPDATE SET
    category_img = EXCLUDED.category_img,
                                       category_product_path = EXCLUDED.category_product_path,
                                       category_priority = EXCLUDED.category_priority;


-- Profile
INSERT INTO profile (profile_name, profile_sname, profile_role, username, password, created_at)
VALUES
    ('Jirayu', 'Jaidee', 1, 'alice', 'alice123', NOW() - INTERVAL '14 days'),
    ('Warin', 'Sakulchai', 1, 'bob', 'bob123', NOW() - INTERVAL '10 days'),
    ('Pimchanok', 'Navachai', 2, 'carol', 'carol123', NOW() - INTERVAL '7 days')
    ON CONFLICT (username) DO NOTHING;

-- ADDRESS
INSERT INTO address (phone, province, amphor, district, zip_code, addr_num, detail, received_name)
VALUES
    ('0812345678', 'Bangkok',     'Bang Kapi',     'Hua Mak',     '10240', '99/1', 'Near the university',  'Jirayu'),
    ('0891112222', 'Nonthaburi',  'Mueang',        'Talat Khwan', '11000', '12/45','Soi Wat Khae Nok',    'Warin');

-- Link addresses to profiles
UPDATE profile p
SET address_id = a.address_id
    FROM address a
WHERE p.username = 'alice'
  AND a.phone = '0812345678'
  AND p.address_id IS NULL;

UPDATE profile p
SET address_id = a.address_id
    FROM address a
WHERE p.username = 'bob'
  AND a.phone = '0891112222'
  AND p.address_id IS NULL;


-- PRODUCTS (Pizza)
INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'BaconHam Cheese',
    'Tomato sauce, mozzarella, basil',
    'BaconHam_Cheese.png',
    249,
    10,
    1,
    NOW() - INTERVAL '12 days',
    'system'
FROM category c
WHERE c.category_name = 'pizza'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'BBQ Smoked',
    'Loaded pepperoni with stretchy cheese',
    'BBQ_Smoked.png',
    289,
    4,
    1,
    NOW() - INTERVAL '11 days',
    'system'
FROM category c
WHERE c.category_name = 'pizza'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Double Pepperoni',
    'Ham, pineapple, Loaded pepperoni',
    'Double_Pepperoni.png',
    269,
    20,
    1,
    NOW() - INTERVAL '10 days',
    'system'
FROM category c
WHERE c.category_name = 'pizza'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Meat Deluxe',
    'Tomato sauce, mozzarella',
    'Meat_deluxe.png',
    279,
    2,
    1,
    NOW() - INTERVAL '10 days',
    'system'
FROM category c
WHERE c.category_name = 'pizza'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Spicy Chicken',
    'Spicy chicken with pineapple',
    'Spicy_Chicken.png',
    299,
    1,
    1,
    NOW() - INTERVAL '10 days',
    'system'
FROM category c
WHERE c.category_name = 'pizza'
ON CONFLICT DO NOTHING;


-- PRODUCTS (Appetizers)
INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Garlic Bread',
    'Baked bread with garlic butter',
    'garlic_bread.png',
    79,
    2,
    1,
    NOW() - INTERVAL '9 days',
    'system'
FROM category c
WHERE c.category_name = 'appetizer'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Chicken Wings',
    'Crispy wings, mildly spicy',
    'chicken_wings.png',
    119,
    6,
    1,
    NOW() - INTERVAL '8 days',
    'system'
FROM category c
WHERE c.category_name = 'appetizer'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Caesar Salad',
    'Fresh lettuce, Caesar dressing, bacon bits',
    'caesar_salad.png',
    129,
    6,
    1,
    NOW() - INTERVAL '7 days',
    'system'
FROM category c
WHERE c.category_name = 'appetizer'
ON CONFLICT DO NOTHING;


-- PRODUCTS (Drinks)
INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Cola',
    'Carbonated soft drink 330ml',
    'cola.png',
    35,
    9,
    1,
    NOW() - INTERVAL '6 days',
    'system'
FROM category c
WHERE c.category_name = 'drink'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Lemon Tea',
    'Sweet lemon iced tea 500ml',
    'lemon_tea.png',
    45,
    5,
    1,
    NOW() - INTERVAL '5 days',
    'system'
FROM category c
WHERE c.category_name = 'drink'
ON CONFLICT DO NOTHING;

INSERT INTO product (category_id, product_name, product_detail, product_img, product_price, product_stock, is_active, created_at, created_by)
SELECT
    c.category_id,
    'Water',
    'Drinking water 600ml',
    'water.png',
    20,
    0,
    1,
    NOW() - INTERVAL '4 days',
    'system'
FROM category c
WHERE c.category_name = 'drink'
ON CONFLICT DO NOTHING;


-- CARTS
INSERT INTO cart (profile_id, created_at, note)
SELECT p.profile_id, NOW() - INTERVAL '3 days', 'Alice cart #1'
FROM profile p
WHERE p.username = 'alice'
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO cart (profile_id, created_at, note)
SELECT p.profile_id, NOW() - INTERVAL '2 days', 'Bob cart #1'
FROM profile p
WHERE p.username = 'bob'
ON CONFLICT (profile_id) DO NOTHING;


-- CART ITEMS

-- Alice: 1x BaconHam Cheese, 2x Cola
INSERT INTO cart_item (cart_id, product_id, qty, line_total)
SELECT c.cart_id, pr.product_id, 1, pr.product_price * 1
FROM cart c
         JOIN profile pf ON pf.username = 'alice' AND c.profile_id = pf.profile_id
         JOIN product pr ON pr.product_name = 'BaconHam Cheese'
    ON CONFLICT (cart_id, product_id) DO UPDATE SET
    qty = EXCLUDED.qty,
                                             line_total = EXCLUDED.line_total;

INSERT INTO cart_item (cart_id, product_id, qty, line_total)
SELECT c.cart_id, pr.product_id, 2, pr.product_price * 2
FROM cart c
         JOIN profile pf ON pf.username = 'alice' AND c.profile_id = pf.profile_id
         JOIN product pr ON pr.product_name = 'Cola'
    ON CONFLICT (cart_id, product_id) DO UPDATE SET
    qty = EXCLUDED.qty,
                                             line_total = EXCLUDED.line_total;

-- Bob: 1x BBQ Smoked, 1x Garlic Bread
INSERT INTO cart_item (cart_id, product_id, qty, line_total)
SELECT c.cart_id, pr.product_id, 1, pr.product_price * 1
FROM cart c
         JOIN profile pf ON pf.username = 'bob' AND c.profile_id = pf.profile_id
         JOIN product pr ON pr.product_name = 'BBQ Smoked'
    ON CONFLICT (cart_id, product_id) DO UPDATE SET
    qty = EXCLUDED.qty,
                                             line_total = EXCLUDED.line_total;

INSERT INTO cart_item (cart_id, product_id, qty, line_total)
SELECT c.cart_id, pr.product_id, 1, pr.product_price * 1
FROM cart c
         JOIN profile pf ON pf.username = 'bob' AND c.profile_id = pf.profile_id
         JOIN product pr ON pr.product_name = 'Garlic Bread'
    ON CONFLICT (cart_id, product_id) DO UPDATE SET
    qty = EXCLUDED.qty,
                                             line_total = EXCLUDED.line_total;


-- ORDERS

-- Alice order (pending)
INSERT INTO orders (profile_id, address_id, status, subtotal, delivery_fee, grand_total, created_at)
SELECT
    p.profile_id,
    p.address_id,
    0,
    249 + 2*35,
    30,
    (249 + 2*35) + 30,
    NOW() - INTERVAL '2 days'
FROM profile p
WHERE p.username = 'alice'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.profile_id = p.profile_id
  AND o.created_at > NOW() - INTERVAL '3 days'
    );

-- Bob order (fulfilled)
INSERT INTO orders (profile_id, address_id, status, subtotal, delivery_fee, grand_total, created_at, fulfilled_at, fulfilled_by)
SELECT
    p.profile_id,
    p.address_id,
    1,
    289 + 79,
    30,
    (289 + 79) + 30,
    NOW() - INTERVAL '1 day',
    NOW(),
    'ops_user'
FROM profile p
WHERE p.username = 'bob'
  AND NOT EXISTS (
    SELECT 1 FROM orders o
    WHERE o.profile_id = p.profile_id
  AND o.created_at > NOW() - INTERVAL '2 days'
    );


-- ORDER ITEMS

-- Alice order items
INSERT INTO order_item (order_id, product_id_snapshot, product_name, product_detail, product_price, qty, line_total)
SELECT
    o.order_id,
    pr.product_id,
    'BaconHam Cheese',
    'Tomato sauce, mozzarella, basil',
    249,
    1,
    249
FROM orders o
         JOIN profile p ON p.username = 'alice' AND o.profile_id = p.profile_id
         JOIN product pr ON pr.product_name = 'BaconHam Cheese'
WHERE o.created_at > NOW() - INTERVAL '3 days'
  AND NOT EXISTS (
    SELECT 1 FROM order_item oi
    WHERE oi.order_id = o.order_id
  AND oi.product_name = 'BaconHam Cheese'
    )
ORDER BY o.created_at DESC
    LIMIT 1;

INSERT INTO order_item (order_id, product_id_snapshot, product_name, product_detail, product_price, qty, line_total)
SELECT
    o.order_id,
    pr.product_id,
    'Cola',
    'Carbonated soft drink 330ml',
    35,
    2,
    70
FROM orders o
         JOIN profile p ON p.username = 'alice' AND o.profile_id = p.profile_id
         JOIN product pr ON pr.product_name = 'Cola'
WHERE o.created_at > NOW() - INTERVAL '3 days'
  AND NOT EXISTS (
    SELECT 1 FROM order_item oi
    WHERE oi.order_id = o.order_id
  AND oi.product_name = 'Cola'
    )
ORDER BY o.created_at DESC
    LIMIT 1;

-- Bob order items
INSERT INTO order_item (order_id, product_id_snapshot, product_name, product_detail, product_price, qty, line_total)
SELECT
    o.order_id,
    pr.product_id,
    'BBQ Smoked',
    'Loaded pepperoni with stretchy cheese',
    289,
    1,
    289
FROM orders o
         JOIN profile p ON p.username = 'bob' AND o.profile_id = p.profile_id
         JOIN product pr ON pr.product_name = 'BBQ Smoked'
WHERE o.created_at > NOW() - INTERVAL '2 days'
  AND NOT EXISTS (
    SELECT 1 FROM order_item oi
    WHERE oi.order_id = o.order_id
  AND oi.product_name = 'BBQ Smoked'
    )
ORDER BY o.created_at DESC
    LIMIT 1;

INSERT INTO order_item (order_id, product_id_snapshot, product_name, product_detail, product_price, qty, line_total)
SELECT
    o.order_id,
    pr.product_id,
    'Garlic Bread',
    'Baked bread with garlic butter',
    79,
    1,
    79
FROM orders o
         JOIN profile p ON p.username = 'bob' AND o.profile_id = p.profile_id
         JOIN product pr ON pr.product_name = 'Garlic Bread'
WHERE o.created_at > NOW() - INTERVAL '2 days'
  AND NOT EXISTS (
    SELECT 1 FROM order_item oi
    WHERE oi.order_id = o.order_id
  AND oi.product_name = 'Garlic Bread'
    )
ORDER BY o.created_at DESC
    LIMIT 1;


-- PROMOTION PRODUCTS
INSERT INTO promotion_product (product_id, promotion_img)
SELECT pr.product_id, 'BaconHam_Cheese.png'
FROM product pr
WHERE pr.product_name = 'BaconHam Cheese'
    ON CONFLICT (product_id) DO UPDATE SET
    promotion_img = EXCLUDED.promotion_img;

INSERT INTO promotion_product (product_id, promotion_img)
SELECT pr.product_id, 'BBQ_Smoked.png'
FROM product pr
WHERE pr.product_name = 'BBQ Smoked'
    ON CONFLICT (product_id) DO UPDATE SET
    promotion_img = EXCLUDED.promotion_img;

INSERT INTO promotion_product (product_id, promotion_img)
SELECT pr.product_id, 'lemon_tea.png'
FROM product pr
WHERE pr.product_name = 'Lemon Tea'
    ON CONFLICT (product_id) DO UPDATE SET
    promotion_img = EXCLUDED.promotion_img;


-- DISCOUNT CODES
INSERT INTO discount_code (code, value, is_active, qty, promotion_id)
SELECT 'BACON20', 20, 1, 50, rp.promotion_id
FROM promotion_product rp
         JOIN product p ON p.product_id = rp.product_id
WHERE p.product_name = 'BaconHam Cheese'
    ON CONFLICT (code) DO UPDATE SET
                              value = EXCLUDED.value,
                              is_active = EXCLUDED.is_active,
                              qty = EXCLUDED.qty;

INSERT INTO discount_code (code, value, is_active, qty, promotion_id)
SELECT 'BBQ25', 25, 1, 40, rp.promotion_id
FROM promotion_product rp
         JOIN product p ON p.product_id = rp.product_id
WHERE p.product_name = 'BBQ Smoked'
    ON CONFLICT (code) DO UPDATE SET
                              value = EXCLUDED.value,
                              is_active = EXCLUDED.is_active,
                              qty = EXCLUDED.qty;

INSERT INTO discount_code (code, value, is_active, qty, promotion_id)
SELECT 'BBQFLASH', 30, 0, 0, rp.promotion_id
FROM promotion_product rp
         JOIN product p ON p.product_id = rp.product_id
WHERE p.product_name = 'BBQ Smoked'
    ON CONFLICT (code) DO UPDATE SET
                              value = EXCLUDED.value,
                              is_active = EXCLUDED.is_active,
                              qty = EXCLUDED.qty;

INSERT INTO discount_code (code, value, is_active, qty, promotion_id)
SELECT 'LEMON5', 5, 1, 5, rp.promotion_id
FROM promotion_product rp
         JOIN product p ON p.product_id = rp.product_id
WHERE p.product_name = 'Lemon Tea'
    ON CONFLICT (code) DO UPDATE SET
                              value = EXCLUDED.value,
                              is_active = EXCLUDED.is_active,
                              qty = EXCLUDED.qty;


-- =====================================================
-- 15. TEST DATA - INVENTORY TRANSACTIONS
-- =====================================================
-- Note: Inventory transactions must match product_stock
-- Formula: SUM(qty_change WHERE type=1) - SUM(qty_change WHERE type=2) - SUM(qty_change WHERE type=3) = product_stock
-- Type 1 = IN (receive stock), Type 2 = SOLD (via order), Type 3 = OUT (adjustment/damage)

-- BaconHam Cheese (stock = 10)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 15, 1, '2025-01-01'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'BaconHam Cheese'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-01'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id, order_id)
SELECT 3, 2, '2025-01-03'::timestamp, pr.product_id,
       (SELECT o.order_id FROM orders o JOIN profile p ON o.profile_id = p.profile_id WHERE p.username = 'alice' ORDER BY o.created_at DESC LIMIT 1)
FROM product pr
WHERE pr.product_name = 'BaconHam Cheese'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
  AND it.created_at = '2025-01-03'::timestamp
  AND it.transaction_type = 2
    );

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 2, 3, '2025-01-05'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'BaconHam Cheese'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-05'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 15 - 3 - 2 = 10 ✅

-- BBQ Smoked (stock = 4)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 10, 1, '2025-01-01'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'BBQ Smoked'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-01'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id, order_id)
SELECT 6, 2, '2025-01-05'::timestamp, pr.product_id,
       (SELECT o.order_id FROM orders o JOIN profile p ON o.profile_id = p.profile_id WHERE p.username = 'bob' ORDER BY o.created_at DESC LIMIT 1)
FROM product pr
WHERE pr.product_name = 'BBQ Smoked'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
  AND it.created_at = '2025-01-05'::timestamp
  AND it.transaction_type = 2
    );
-- Remaining: 10 - 6 = 4 ✅

-- Double Pepperoni (stock = 20)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 25, 1, '2025-01-02'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Double Pepperoni'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-02'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 5, 3, '2025-01-06'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Double Pepperoni'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-06'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 25 - 5 = 20 ✅

-- Meat Deluxe (stock = 2)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 5, 1, '2025-01-01'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Meat Deluxe'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-01'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 3, 3, '2025-01-07'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Meat Deluxe'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-07'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 5 - 3 = 2 ✅

-- Spicy Chicken (stock = 1)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 4, 1, '2025-01-03'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Spicy Chicken'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-03'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 3, 3, '2025-01-04'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Spicy Chicken'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-04'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 4 - 3 = 1 ✅

-- Garlic Bread (stock = 2)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 6, 1, '2025-01-03'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Garlic Bread'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-03'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id, order_id)
SELECT 4, 2, '2025-01-06'::timestamp, pr.product_id,
       (SELECT o.order_id FROM orders o JOIN profile p ON o.profile_id = p.profile_id WHERE p.username = 'bob' ORDER BY o.created_at DESC LIMIT 1)
FROM product pr
WHERE pr.product_name = 'Garlic Bread'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
  AND it.created_at = '2025-01-06'::timestamp
  AND it.transaction_type = 2
    );
-- Remaining: 6 - 4 = 2 ✅

-- Chicken Wings (stock = 6)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 10, 1, '2025-01-02'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Chicken Wings'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-02'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 4, 3, '2025-01-05'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Chicken Wings'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-05'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 10 - 4 = 6 ✅

-- Caesar Salad (stock = 6)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 12, 1, '2025-01-01'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Caesar Salad'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-01'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 6, 3, '2025-01-03'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Caesar Salad'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-03'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 12 - 6 = 6 ✅

-- Cola (stock = 9)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 10, 1, '2025-01-04'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Cola'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-04'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id, order_id)
SELECT 1, 2, '2025-01-06'::timestamp, pr.product_id,
       (SELECT o.order_id FROM orders o JOIN profile p ON o.profile_id = p.profile_id WHERE p.username = 'alice' ORDER BY o.created_at DESC LIMIT 1)
FROM product pr
WHERE pr.product_name = 'Cola'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
  AND it.created_at = '2025-01-06'::timestamp
  AND it.transaction_type = 2
    );
-- Remaining: 10 - 1 = 9 ✅

-- Lemon Tea (stock = 5)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 12, 1, '2025-01-03'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Lemon Tea'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-03'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 7, 3, '2025-01-08'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Lemon Tea'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-08'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 12 - 7 = 5 ✅

-- Water (stock = 0)
INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 7, 1, '2025-01-01'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Water'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-01'::timestamp
        AND it.transaction_type = 1
);

INSERT INTO inventory_transaction (qty_change, transaction_type, created_at, product_id)
SELECT 7, 3, '2025-01-09'::timestamp, pr.product_id
FROM product pr
WHERE pr.product_name = 'Water'
  AND NOT EXISTS (
    SELECT 1 FROM inventory_transaction it
    WHERE it.product_id = pr.product_id
      AND it.created_at = '2025-01-09'::timestamp
        AND it.transaction_type = 3
);
-- Remaining: 7 - 7 = 0 ✅

COMMIT;


-- =====================================================
-- END OF INITIALIZATION
-- =====================================================