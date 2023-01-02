CREATE DATABASE db_sales;
USE db_sales;
CREATE TABLE category(
	id INT AUTO_INCREMENT NOT NULL,
    description VARCHAR(100) NOT NULL,
	CONSTRAINT pk_category
		PRIMARY KEY(id)
);

CREATE TABLE supplier(
	id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(300) NOT NULL,
    email VARCHAR(300) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    CONSTRAINT pk_supplier
		PRIMARY KEY(id)
);

CREATE TABLE national_supplier(
	id_supplier INT NOT NULL REFERENCES supplier(id),
    cnpj VARCHAR(100) NOT NULL,
    CONSTRAINT pk_national_supplier 
		PRIMARY KEY (id_supplier),
    CONSTRAINT fk_national_supplier_supplier 
		FOREIGN KEY (id_supplier) 
        REFERENCES supplier(id) 
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE international_supplier(
	id_supplier INT NOT NULL REFERENCES supplier(id),
    nif VARCHAR(100) NOT NULL,
    country VARCHAR(300) NOT NULL,
    CONSTRAINT pk_international_supplier 
		PRIMARY KEY (id_supplier),
    CONSTRAINT fk_international_supplier_supplier 
		FOREIGN KEY (id_supplier) 
        REFERENCES supplier(id) 
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE product(
	id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(300) DEFAULT NULL,
    price DECIMAL(10,2),
    id_category INT NOT NULL,
	id_supplier INT NOT NULL,
    CONSTRAINT pk_product
		PRIMARY KEY(id),
	CONSTRAINT fk_product_category
		FOREIGN KEY(id_category)
		REFERENCES category(id),
	CONSTRAINT fk_product_supplier
      FOREIGN KEY(id_supplier)
      REFERENCES supplier(id)      
);

CREATE TABLE stock(
	id_product INT NOT NULL REFERENCES product(id),
    quantity INT NOT NULL DEFAULT 0,
    min_quantity INT DEFAULT 0,
    max_quantity INT DEFAULT 0,
    situation ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') NOT NULL DEFAULT 'INACTIVE',
    CONSTRAINT pk_stock 
		PRIMARY KEY(id_product),
	CONSTRAINT fk_stock_product
		FOREIGN KEY(id_product)
        REFERENCES product(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE client(
	id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(300) NOT NULL,
    cpf VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(300) NOT NULL,
    birth_date DATE,
    CONSTRAINT pk_client
		PRIMARY KEY(id)    
);

CREATE TABLE sale(
	id INT AUTO_INCREMENT NOT NULL,
    date DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    paid BOOLEAN NOT NULL,
	discount_fee DOUBLE,
	company VARCHAR(200) NOT NULL,
    status ENUM('COMPLETED', 'CANCECLED', 'OPEN') NOT NULL DEFAULT 'OPEN',
    id_client INT NOT NULL,
    CONSTRAINT pk_sale
		PRIMARY KEY(id),
	CONSTRAINT fk_sale_client
		FOREIGN KEY(id_client)
		REFERENCES client(id)
);

CREATE TABLE sale_item(
   id INT AUTO_INCREMENT NOT NULL,
   quantity INT NOT NULL,
   total DECIMAL(10,2) NOT NULL,
   id_product INT NOT NULL,
   id_sale INT NOT NULL,
   CONSTRAINT pk_sale_item
      PRIMARY KEY(id),
   CONSTRAINT fk_sale_item_product
      FOREIGN KEY(id_product)
      REFERENCES product(id),
   CONSTRAINT fk_sale_item_sale
      FOREIGN KEY(id_sale)
      REFERENCES sale(id)
      ON DELETE CASCADE
);  

INSERT category(description) VALUES("Vestuário"), ("Alimentação");
INSERT supplier(name, email, phone) VALUES("Bilu", "bilu@gmail.com", "+55 48 3232-3333"), ("Futura", "futura@gmail.com", "+45 588 5552-55698");
INSERT national_supplier(id_supplier, cnpj) VALUES(1, "11111111111111");
INSERT international_supplier(id_supplier, nif, country) VALUES(2, "22222222222222", "India");
INSERT product(name, description, price, id_category, id_supplier) VALUES("Moletom", "Moletom cinza com estampa", '59.90', 1, 2), ("Pipoca doce", "Pipoca doce 55 gramas", '3.50', 2, 1), ("Vestido", "Vestido branco com estampa florida", '140.10', 1, 2);
INSERT stock(id_product, quantity, min_quantity, max_quantity, situation) VALUES(1, 30, 2, 100, 'ACTIVE'), (2, 300, 100, 500, 'ACTIVE'), (3, 100, 50, 200, 'ACTIVE');
INSERT client(name, cpf, phone, address, birth_date) VALUES('Liza Fernandes', '111.111.111-11', '(48) 99999-9991', 'Rua 1, número 1 - Centro, Florianópolis/SC', '1991-01-01');
INSERT sale(date, total, paid, discount_fee, company, status, id_client) VALUES('2022-12-06', '200.00', false, '0.00', 'Futura', 'COMPLETED', 1);
INSERT sale_item(quantity, total, id_product, id_sale) VALUES(1, '59.90', 1, 1), (1, '140.10', 3, 1);