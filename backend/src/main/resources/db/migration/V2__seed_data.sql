-- V2: Seed data — categories, products, and one admin user

-- ============ Categories ============
INSERT INTO categories (name, slug, image_url, sort_order) VALUES
('Cement',           'cement',           '/assets/images/categories/cement.webp',      1),
('TMT Steel',        'tmt-steel',        '/assets/images/categories/tmt-steel.webp',   2),
('Bricks & Blocks',  'bricks-blocks',    '/assets/images/categories/bricks.webp',      3),
('Aggregates & Sand','aggregates-sand',  '/assets/images/categories/aggregates.webp',  4),
('Safety & Tools',   'safety-tools',     '/assets/images/categories/safety-tools.webp',5);

-- ============ Products ============
-- Cement
INSERT INTO products (name, slug, category_id, brand, unit, description, image_url, is_active) VALUES
('UltraTech OPC 53 Grade',       'ultratech-opc-53',        1, 'UltraTech',   'Bag (50 kg)',  'High-strength OPC 53 grade cement ideal for RCC work, precast elements, and high-rise construction. Conforms to IS 12269.', '/assets/images/products/ultratech-opc-53.webp', TRUE),
('ACC Gold PPC',                  'acc-gold-ppc',            1, 'ACC',         'Bag (50 kg)',  'Portland Pozzolana Cement with superior finish and durability. Excellent for plastering, masonry, and general construction.', '/assets/images/products/acc-gold-ppc.webp', TRUE),
('Ambuja Plus Cement',            'ambuja-plus',             1, 'Ambuja',      'Bag (50 kg)',  'Premium blended cement with moisture-resistance technology. Suitable for all types of construction.', '/assets/images/products/ambuja-plus.webp', TRUE);

-- TMT Steel
INSERT INTO products (name, slug, category_id, brand, unit, description, image_url, is_active) VALUES
('TATA Tiscon 500D TMT Bars',    'tata-tiscon-500d',        2, 'TATA Steel',  'Metric Ton',   'Fe 500D grade TMT bars with superior ductility, earthquake resistance, and corrosion protection. BIS certified.', '/assets/images/products/tata-tiscon-500d.webp', TRUE),
('JSW NeoSteel 550D',            'jsw-neosteel-550d',       2, 'JSW Steel',   'Metric Ton',   'High-strength Fe 550D TMT rebars with CRM technology for enhanced bendability and weldability.', '/assets/images/products/jsw-neosteel-550d.webp', TRUE),
('Vizag Steel TMT Fe 500',       'vizag-steel-fe500',       2, 'RINL Vizag',  'Metric Ton',   'Government-manufactured TMT bars known for consistent quality. Popular across Andhra Pradesh and Telangana.', '/assets/images/products/vizag-steel-fe500.webp', TRUE);

-- Bricks & Blocks
INSERT INTO products (name, slug, category_id, brand, unit, description, image_url, is_active) VALUES
('AAC Blocks (600x200x200mm)',   'aac-blocks-600',          3, 'Magicrete',   'Piece',        'Autoclaved Aerated Concrete blocks — lightweight, fire-resistant, excellent thermal insulation. Reduces dead load by 30%.', '/assets/images/products/aac-blocks.webp', TRUE),
('Fly Ash Bricks (9x4x3 in)',   'fly-ash-bricks-9x4',      3, 'Local',       'Per 1000',     'Eco-friendly compressed fly ash bricks with uniform shape, strength up to 75 kg/cm². GRIHA compliant.', '/assets/images/products/fly-ash-bricks.webp', TRUE),
('Solid Concrete Blocks (16in)', 'solid-concrete-blocks-16', 3, 'Local',       'Piece',        'Heavy-duty solid concrete blocks for load-bearing walls and compound walls. 16 × 8 × 6 inches.', '/assets/images/products/solid-concrete-blocks.webp', TRUE);

-- Aggregates & Sand
INSERT INTO products (name, slug, category_id, brand, unit, description, image_url, is_active) VALUES
('M-Sand (Manufactured Sand)',   'm-sand-manufactured',      4, 'Local',       'Cubic Meter',  'Machine-crushed, cube-shaped, washed manufactured sand. Ideal replacement for river sand in concrete and plastering.', '/assets/images/products/m-sand.webp', TRUE),
('20mm Metal Aggregate',         '20mm-metal-aggregate',     4, 'Local',       'Cubic Meter',  'Crushed stone aggregate — 20 mm nominal size for RCC and RMC work. Well-graded, ISI norms.', '/assets/images/products/20mm-aggregate.webp', TRUE),
('River Sand (Fine Grade)',      'river-sand-fine',           4, 'Local',       'Cubic Meter',  'Naturally sourced fine-grade river sand for plastering and brickwork. Subject to availability and govt. permits.', '/assets/images/products/river-sand.webp', TRUE);

-- Safety & Tools
INSERT INTO products (name, slug, category_id, brand, unit, description, image_url, is_active) VALUES
('3M Safety Helmet (H-700)',     '3m-safety-helmet-h700',    5, '3M',          'Piece',        'Ratchet-type industrial safety helmet with ventilation. Meets IS 2925 standard. UV-stabilized HDPE shell.', '/assets/images/products/3m-helmet.webp', TRUE),
('Bosch 4-inch Angle Grinder',  'bosch-angle-grinder-4in',  5, 'Bosch',       'Piece',        '720W professional angle grinder for cutting, grinding, and polishing. Anti-vibration side handle included.', '/assets/images/products/bosch-grinder.webp', TRUE),
('Taparia 5-Pc Masonry Drill Set','taparia-masonry-drill-5', 5, 'Taparia',     'Set',          'HSS masonry drill bits (5mm–12mm) with carbide tips. Fits all standard rotary and hammer drills.', '/assets/images/products/taparia-drill-set.webp', TRUE);

-- ============ Admin user ============
-- Password: Admin@1234 (BCrypt hash)
INSERT INTO admin_users (email, password_hash, full_name, roles, active) VALUES
('admin@civilsupplies.in', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sudheer Bellam', 'ROLE_ADMIN', TRUE);
