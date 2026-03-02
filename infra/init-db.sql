CREATE USER productuser WITH PASSWORD 'productpass';
CREATE DATABASE productdb OWNER productuser;
GRANT ALL PRIVILEGES ON DATABASE productdb TO productuser;

CREATE USER orderuser WITH PASSWORD 'orderpass';
CREATE DATABASE orderdb OWNER orderuser;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO orderuser;

CREATE USER inventoryuser WITH PASSWORD 'inventorypass';
CREATE DATABASE inventorydb OWNER inventoryuser;
GRANT ALL PRIVILEGES ON DATABASE inventorydb TO inventoryuser;

CREATE USER commonuser WITH PASSWORD 'commonpass';
CREATE DATABASE commondb OWNER commonuser;
GRANT ALL PRIVILEGES ON DATABASE commondb TO commonuser;