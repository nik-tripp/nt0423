-- Tool rental table definitions
-- SQL Flavor: SQLite

-- Types of tools that can be rented out
CREATE TABLE IF NOT EXISTS tool_type (
   type text PRIMARY KEY,
   daily_charge real,
   -- The following three will function as booleans, but those don't have their own datatype in SQLite, so it will
   --   actually store them as 1 and 0.
   weekday_charge,
   weekend_charge,
   holiday_charge,
)


-- Individual tools available for rental
CREATE TABLE IF NOT EXISTS tool (
   -- Discrete PK is not necessary at this stage, use SQLite's autoinc ROWID
   -- Tool code would be a good primary key, other than the fact that it could theoretically be changed, and
   --   cascading that reference through all rental agreements could be expensive in a live service
   code text NOT NULL UNIQUE,
   type text NOT NULL,
   brand text NOT NULL,
   FOREIGN KEY (type) REFERENCES tool_type (type)
);


-- Tool rental agreements
CREATE TABLE IF NOT EXISTS rental_agreement (
   -- discrete PK is not necessary, use SQLite's autoinc ROWID
   tool_id integer,
   rental_days integer,
   check_out_date text, -- FORMAT yyyy-mm-dd (SQLITE has no date/time classes
   discount_percent integer,
   FOREIGN KEY (tool_id) REFERENCES tool (rowid)
)
