USE dbApp;

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
INSERT INTO section (sectionID, sectionname, price, availableslots)
VALUES
(3000, 'VIP', 950.00, 100),
(3001, 'Regular', 300.00,  400),
(3002, 'VIP', 950.00, 150),
(3003, 'Balcony', 150.00, 300),
(3004, 'VIP', 950.00, 200),
(3005, 'Upper Box', 300.00, 600);

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

SELECT * FROM events;
SELECT * FROM customers;
SELECT * FROM section;
SELECT * FROM merchandise;