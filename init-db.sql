CREATE DATABASE IF NOT EXISTS shopflow_users;
CREATE DATABASE IF NOT EXISTS shopflow_products;
CREATE DATABASE IF NOT EXISTS shopflow_orders;

GRANT ALL PRIVILEGES ON shopflow_users.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON shopflow_products.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON shopflow_orders.* TO 'root'@'%';

FLUSH PRIVILEGES;