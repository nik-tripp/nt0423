-- Populate the schema with basic data

INSERT INTO tool_type (
   type,
   daily_charge,
   weekday_charge,
   weekend_charge,
   holiday_charge,
)
VALUES
('Ladder', 1.99, true, true, false),
('Chainsaw', 1.49, true, false, true),
('Jackhammer', 2.99, true, false, false),

INSERT INTO tool (
   code,
   type,
   brand
)
VALUES
('CHNS', 'Chainsaw', 'Stihl'),
('LADW', 'Ladder', 'Werner'),
('JAKD', 'Jackhammer', 'DeWalt'),
('JAKR', 'Jackhammer', 'Ridgid')
