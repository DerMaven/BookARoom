-- ---------- hotels ----------
INSERT INTO hotels (id, name, description, country, city, address, currency) VALUES
                                                                                 (1, 'Rixos Astana',        'Luxury 5-star hotel in the city center with spa and panoramic views.', 'Kazakhstan', 'Astana', 'Kabanbay Batyr Ave 47', 'KZT'),
                                                                                 (2, 'Hilton Almaty',       'Modern business hotel near Dostyk Avenue with conference halls.',      'Kazakhstan', 'Almaty', 'Satpaev St 29',          'KZT'),
                                                                                 (3, 'Hotel Adlon Berlin',  'Historic hotel next to the Brandenburg Gate.',                         'Germany',    'Berlin', 'Unter den Linden 77',    'EUR'),
                                                                                 (4, 'Plaza Central NYC',   'Comfortable hotel in Midtown Manhattan, steps from Times Square.',     'USA',        'New York', '8th Ave 123',          'USD'),
                                                                                 (5, 'Budget Inn Paris',    'Cozy budget hotel in the Latin Quarter.',                              'France',     'Paris',  'Rue Mouffetard 15',      'EUR');

-- ---------- highlights ----------
INSERT INTO highlights (id, description, icon, hotel_id) VALUES
                                                             (1,  'Free Wi-Fi in all areas',        'https://cdn.example.com/icons/wifi.svg',      1),
                                                             (2,  'Spa and wellness center',        'https://cdn.example.com/icons/spa.svg',       1),
                                                             (3,  'Indoor swimming pool',           'https://cdn.example.com/icons/pool.svg',      1),
                                                             (4,  'Free Wi-Fi in all areas',        'https://cdn.example.com/icons/wifi.svg',      2),
                                                             (5,  'Conference rooms',               'https://cdn.example.com/icons/meeting.svg',   2),
                                                             (6,  'Airport shuttle',                'https://cdn.example.com/icons/shuttle.svg',   2),
                                                             (7,  'Michelin-star restaurant',       'https://cdn.example.com/icons/restaurant.svg',3),
                                                             (8,  'Fitness center',                 'https://cdn.example.com/icons/gym.svg',       3),
                                                             (9,  'Rooftop bar',                    'https://cdn.example.com/icons/bar.svg',       4),
                                                             (10, '24/7 front desk',                'https://cdn.example.com/icons/desk.svg',      4),
                                                             (11, 'Free breakfast included',        'https://cdn.example.com/icons/breakfast.svg', 5),
                                                             (12, 'Pet friendly',                   'https://cdn.example.com/icons/pet.svg',       5);

-- ---------- rooms (4 per hotel) ----------
INSERT INTO rooms (id, room_number, floor, max_occupancy, room_type, status, is_available, hotel_id) VALUES
-- Hotel 1
(1,  '101',  1,  1, 'STANDARD',     'AVAILABLE',   true,  1),
(2,  '202',  2,  2, 'STANDARD',     'AVAILABLE',   true,  1),
(3,  '305',  3,  2, 'DELUXE',       'OCCUPIED',    false, 1),
(4,  '501',  5,  4, 'PRESIDENTIAL', 'AVAILABLE',   true,  1),
-- Hotel 2
(5,  '110',  1,  1, 'STANDARD',     'AVAILABLE',   true,  2),
(6,  '215',  2,  2, 'STANDARD',     'RESERVED',    false, 2),
(7,  '318',  3,  2, 'DELUXE',       'AVAILABLE',   true,  2),
(8,  '410',  4,  4, 'SUITE',        'MAINTENANCE', false, 2),
-- Hotel 3
(9,  '12',   1,  2, 'STANDARD',     'AVAILABLE',   true,  3),
(10, '24',   2,  2, 'DELUXE',       'AVAILABLE',   true,  3),
(11, '31',   3,  3, 'SUITE',        'OCCUPIED',    false, 3),
(12, '40',   4,  1, 'STANDARD',     'AVAILABLE',   true,  3),
-- Hotel 4
(13, '1001', 10, 2, 'STANDARD',     'AVAILABLE',   true,  4),
(14, '1102', 11, 2, 'DELUXE',       'AVAILABLE',   true,  4),
(15, '1205', 12, 4, 'PRESIDENTIAL', 'RESERVED',    false, 4),
(16, '1301', 13, 1, 'STANDARD',     'OCCUPIED',    false, 4),
-- Hotel 5
(17, 'A1',   1,  1, 'STANDARD',     'AVAILABLE',   true,  5),
(18, 'A2',   1,  2, 'STANDARD',     'AVAILABLE',   true,  5),
(19, 'B1',   2,  2, 'DELUXE',       'OCCUPIED',    false, 5),
(20, 'B2',   2,  3, 'STANDARD',     'AVAILABLE',   true,  5);

-- ---------- rate_plans ----------
INSERT INTO rate_plans (id, menu_plan, price, beds, currency, min_nights_stay, max_nights_stay, is_active, hotel_id) VALUES
                                                                                                                         (1,  'ROOM_ONLY',         45000.00, 1, 'KZT', 1, 14, true,  1),
                                                                                                                         (2,  'BED_AND_BREAKFAST', 55000.00, 2, 'KZT', 1, 30, true,  1),
                                                                                                                         (3,  'ALL_INCLUSIVE',     95000.00, 2, 'KZT', 2, 21, true,  1),
                                                                                                                         (4,  'BED_AND_BREAKFAST', 38000.00, 2, 'KZT', 1, 30, true,  2),
                                                                                                                         (5,  'HALF_BOARD',        52000.00, 2, 'KZT', 2, 14, false, 2),
                                                                                                                         (6,  'BED_AND_BREAKFAST', 210.00,   2, 'EUR', 1, 10, true,  3),
                                                                                                                         (7,  'FULL_BOARD',        340.00,   2, 'EUR', 3, 14, true,  3),
                                                                                                                         (8,  'ROOM_ONLY',         180.00,   2, 'USD', 1, 28, true,  4),
                                                                                                                         (9,  'BED_AND_BREAKFAST', 200.00,   4, 'USD', 2, 28, true,  4),
                                                                                                                         (10, 'BED_AND_BREAKFAST', 85.00,    1, 'EUR', 1, 7,  true,  5);

-- ---------- daily_inventories ----------
-- NOTE: @OneToOne on hotel_id => UNIQUE constraint, so one inventory per hotel.
INSERT INTO daily_inventories (id, inventory_date, total_stock, booking_stock, hotel_id) VALUES
                                                                                             (1, '2026-11-20', 4, 1, 1),
                                                                                             (2, '2026-11-21', 4, 0, 2),
                                                                                             (3, '2026-11-22', 4, 2, 3),
                                                                                             (4, '2026-11-23', 4, 3, 4),
                                                                                             (5, '2026-11-24', 4, 1, 5);

-- ---------- daily_inventory_rooms (ManyToMany join table) ----------
INSERT INTO daily_inventory_rooms (daily_inventory_id, room_id) VALUES
                                                                    (1, 1), (1, 2), (1, 3), (1, 4),
                                                                    (2, 5), (2, 6), (2, 7), (2, 8),
                                                                    (3, 9), (3, 10), (3, 11), (3, 12),
                                                                    (4, 13), (4, 14), (4, 15), (4, 16),
                                                                    (5, 17), (5, 18), (5, 19), (5, 20);