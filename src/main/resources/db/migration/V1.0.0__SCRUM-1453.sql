
CREATE TABLE subdomain
(
  id   SERIAL PRIMARY KEY,
  code VARCHAR(4) UNIQUE NOT NULL,
  name VARCHAR(100)  NOT NULL,
  description VARCHAR(200)
);

