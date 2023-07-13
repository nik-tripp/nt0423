-- Tool rental table definitions
-- SQL Flavor: SQLite

-- Types of tools that can be rented out
CREATE TABLE IF NOT EXISTS tool_type (
    type text PRIMARY KEY,
    daily_charge real NOT NULL,
    -- The following three will function as booleans, but those don't have their own datatype in SQLite, so it will
    --   actually store them as 1 and 0.
    weekday_charge NOT NULL,
    weekend_charge NOT NULL,
    holiday_charge NOT NULL
);


-- Individual tools available for rental
CREATE TABLE IF NOT EXISTS tool (
    code text PRIMARY KEY,
    type text NOT NULL,
    brand text NOT NULL,
    FOREIGN KEY (type) REFERENCES tool_type (type)
);


-- Tool rental agreements
CREATE TABLE IF NOT EXISTS rental_agreement (
    -- discrete PK is not necessary, use SQLite's autoinc ROWID
    tool_code text NOT NULL,
    tool_type text NOT NULL,
    tool_brand text NOT NULL,
    rental_days integer NOT NULL,
    check_out_date text NOT NULL, -- FORMAT yyyy-mm-dd (SQLITE has no date/time classes)
    due_date text NOT NULL, -- FORMAT yyyy-mm-dd (SQLITE has no date/time classes)
    charge_days integer NOT NULL,
    daily_charge real NOT NULL,
    discount_percent integer NOT NULL
);
