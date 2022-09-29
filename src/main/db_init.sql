-- This will be adapted into the db migration script that will run upon every redeploy... once I figure out how
CREATE TABLE customers (
customer_id UUID PRIMARY KEY,
customer_name char(50) NOT NULL,
email char(100) NOT NULL,
password char(255) NOT NULL,
contact_no char(15) NOT NULL
);

CREATE TABLE addresses (
address_id UUID PRIMARY KEY,
customer_id UUID NOT NULL,
address1 char(150) NOT NULL,
address2 char(150),
postal_code char(10) NOT NULL,
country char(50) NOT NULL,
FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
-- Make sure deletions to customers cascade down to addresses
);

CREATE TABLE orders (
order_id UUID PRIMARY KEY,
customer_id UUID NOT NULL,
order_date timestamp(3) NOT NULL,
total_price numeric NOT NULL,
no_of_items int NOT NULL,
FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE shipments (
-- this table will remain empty, just add because prompt said to add
-- if we are to build out functionality for this, using UUID to track shipping method, further table for tracking shipping method relations will be needed
shipment_id UUID PRIMARY KEY,
order_id UUID NOT NULL,
shipment_date timestamp(3) NOT NULL,
method UUID NOT NULL,
FOREIGN KEY (order_id) REFERENCES orders(order_id)
);