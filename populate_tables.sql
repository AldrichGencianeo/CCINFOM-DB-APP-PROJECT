USE dbapp;

-- ===============================
-- CUSTOMER TABLE
-- ===============================
INSERT INTO customers (customerID, firstName, lastName, email, phoneNumber, balance)
VALUES
(1000, 'Aldrich', 'Gencianeo', 'aldrich_gencianeo@dlsu.edu.ph', '09171234567', 8500.00),
(1001, 'Eoghan', 'Follero', 'eoghan_follero@dlsu.edu.ph', '09182345678', 9000.00),
(1002, 'Keila', 'Geslani', 'keila_geslani@dlsu.edu.ph', '09193456789', 10500.00),
(1003, 'Lara', 'Santos', 'lara_santos@gmail.com', '09204567890', 8000.00),
(1004, 'Miguel', 'Tan', 'miguel_tan@gmail.com', '09215678901', 11000.00),
(1005, 'Napoleon', null, null, null, 10000.00),
(1006, 'Muhammad', null, null, null, 5500.00),
(1007, 'Abraham', 'Lincoln', 'a_lincoln@gmail.com', null, 14500.00),
(1008, 'George', 'Washington', 'g_washington@gmail.com', null, 5000.00),
(1009, 'Aristotle', null, null, null, 14000.00);

-- ===============================
-- EVENT TABLE
-- ===============================
INSERT INTO events (eventID, eventname, eventtype, bookingfee)
VALUES
(2000, 'Harmony Nights', 'Concert', 9000.00),
(2001, 'Phantom of the Opera', 'Theater', 7000.00),
(2002, 'RockFest 2025', 'Concert', 1500.00),
(2003, 'Broadway Reimagined', 'Theater', 3000.00),
(2004, 'Symphony of Lights', 'Concert', 5000.00),
(2005, 'Elvis Comeback Special', 'Concert', 9500.00),
(2006, "You Can't Take It With You", 'Theater', 2500.00),
(2007, 'Cream Farewell Tour', 'Concert', 9000.00),
(2008, 'Arsenic and Old Lace', 'Theater', 4000.00),
(2009, 'Led Zeppelin World Tour', 'Concert', 7500.00);

-- ===============================
-- VENUE & SECTION TABLE
-- ===============================
INSERT INTO section (sectionID, sectionname, capacity, price)
VALUES
(3000, 'VIP', 100, 950.00),
(3001, 'Regular', 400, 300.00),
(3002, 'VIP', 150, 950.00),
(3003, 'Balcony', 300, 150.00),
(3004, 'VIP', 200, 950.00),
(3005, 'Upper Box', 600, 300.00);

-- ===============================
-- MERCHANDISE TABLE
-- ===============================
INSERT INTO merchandise (merchandiseID, merchandisename, category, price, stock)
VALUES
(4000, 'Official Event T-Shirt', 'Clothing', 750.00, 150),
(4001, 'Event Poster', 'Collectible', 250.00, 200),
(4002, 'VIP Lanyard Pass', 'Accessory', 500.00, 100),
(4003, 'Signed Album', 'Music', 1200.00, 50),
(4004, 'Light Stick', 'Accessory', 900.00, 80),
(4005, 'Event Tote Bag', 'Clothing', 400.00, 120);

-- ===============================
-- SCHEDULES TABLE
-- ===============================
INSERT INTO schedules (scheduleID, eventID, scheduleDate, startTime, endTime)
VALUES
(5000, 2000, '2025-12-15', '19:00:00', '22:00:00'),
(5001, 2000, '2025-12-16', '19:00:00', '22:00:00'),
(5002, 2001, '2025-12-20', '20:00:00', '23:00:00'),
(5003, 2002, '2025-11-25', '18:00:00', '23:00:00'),
(5004, 2003, '2025-12-10', '19:30:00', '22:30:00');

-- ===============================
-- SCHEDULE_SECTION TABLE
-- ===============================
INSERT INTO schedule_section (scheduleID, sectionID, availableSlots)
VALUES
(5000, 3000, 100),
(5000, 3001, 400),
(5001, 3000, 100),
(5001, 3001, 400),
(5002, 3002, 150),
(5002, 3003, 300),
(5003, 3004, 200),
(5004, 3005, 600);

-- ===============================
-- EVENT_MERCH TABLE
-- ===============================
INSERT INTO event_merch (eventID, merchandiseID, merchtype)
VALUES
-- Harmony Nights (Concert) - Event 2000
(2000, 4000, 'Package'),   -- T-Shirt as Package
(2000, 4001, 'Addon'),     -- Poster as Addon
(2000, 4003, 'Package'),   -- Signed Album as Package
(2000, 4004, 'Addon'),     -- Light Stick as Addon

-- Phantom of the Opera (Theater) - Event 2001
(2001, 4000, 'Package'),   -- T-Shirt as Package
(2001, 4001, 'Addon'),     -- Poster as Addon
(2001, 4005, 'Addon'),     -- Tote Bag as Addon

-- RockFest 2025 (Concert) - Event 2002
(2002, 4000, 'Package'),   -- T-Shirt as Package
(2002, 4003, 'Package'),   -- Signed Album as Package
(2002, 4004, 'Addon'),     -- Light Stick as Addon

-- Broadway Reimagined (Theater) - Event 2003
(2003, 4001, 'Addon'),     -- Poster as Addon
(2003, 4002, 'Package'),   -- VIP Lanyard as Package
(2003, 4005, 'Addon'),     -- Tote Bag as Addon

-- Symphony of Lights (Concert) - Event 2004
(2004, 4000, 'Package'),   -- T-Shirt as Package
(2004, 4004, 'Addon'),     -- Light Stick as Addon

-- Elvis Comeback Special (Concert) - Event 2005
(2005, 4000, 'Package'),   -- T-Shirt as Package
(2005, 4003, 'Package'),   -- Signed Album as Package

-- You Can't Take It With You (Theater) - Event 2006
(2006, 4001, 'Addon'),     -- Poster as Addon
(2006, 4005, 'Addon'),     -- Tote Bag as Addon

-- Cream Farewell Tour (Concert) - Event 2007
(2007, 4000, 'Package'),   -- T-Shirt as Package
(2007, 4003, 'Package'),   -- Signed Album as Package

-- Arsenic and Old Lace (Theater) - Event 2008
(2008, 4001, 'Addon'),     -- Poster as Addon
(2008, 4002, 'Package'),   -- VIP Lanyard as Package

-- Led Zeppelin World Tour (Concert) - Event 2009
(2009, 4000, 'Package'),   -- T-Shirt as Package
(2009, 4003, 'Package'),   -- Signed Album as Package
(2009, 4004, 'Addon');     -- Light Stick as Addon

-- ===============================
-- VERIFICATION QUERY
-- ===============================
SELECT 'customers' as TableName, COUNT(*) as RowCount FROM customers
UNION ALL SELECT 'events', COUNT(*) FROM events
UNION ALL SELECT 'section', COUNT(*) FROM section
UNION ALL SELECT 'merchandise', COUNT(*) FROM merchandise
UNION ALL SELECT 'schedules', COUNT(*) FROM schedules
UNION ALL SELECT 'schedule_section', COUNT(*) FROM schedule_section
UNION ALL SELECT 'event_merch', COUNT(*) FROM event_merch;