DROP TABLE IF EXISTS user_role_tb CASCADE;
DROP TABLE IF EXISTS role_tb CASCADE;
DROP TABLE IF EXISTS room_type_tb CASCADE;
DROP TABLE IF EXISTS room_tb CASCADE;
DROP TABLE IF EXISTS hotel_tb CASCADE;
DROP TABLE IF EXISTS message_tb CASCADE;
DROP TABLE IF EXISTS tracking_tb CASCADE;
DROP TABLE IF EXISTS user_tb CASCADE;

CREATE TABLE role_tb(
    id BIGSERIAL,
    role_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT unq_role_name UNIQUE (role_name)
);

INSERT INTO role_tb VALUES (DEFAULT, 'ROLE_ADMIN');
INSERT INTO role_tb VALUES (DEFAULT, 'ROLE_CONTENT_MANAGER');
INSERT INTO role_tb VALUES (DEFAULT, 'ROLE_SALES_MANAGER');
INSERT INTO role_tb VALUES (DEFAULT, 'ROLE_USER');

CREATE TABLE user_tb(
    id UUID,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT unq_username UNIQUE (username)
);

INSERT INTO user_tb VALUES ('91dc56da-29df-11ec-9621-0242ac130002', 'Mike', 'Smith', 'mike.smith@gmail.com', '$2a$10$d1BVDIOnUmtJs7dKVLL8duQWRwRe/tUyQevXsJzrsymY/t37bia8u', true);
INSERT INTO user_tb VALUES ('8879e6d8-29e0-11ec-9621-0242ac130002', 'Sarah', 'Jones', 'sarah.jones@gmail.com', '$2a$10$fsM9Qn.mrqnw8wWEdeeGnuuCumFAmxX.3KoqtxvY6pKvPYJz.8.0y', true);
INSERT INTO user_tb VALUES ('5f1623c6-2c70-4abc-b360-ac959a42d246', 'Tom', 'Anderson', 'tom.anderson@gmail.com', '$2a$10$cYcEIAWVE5d9aPOEnuFOxelVNo.ytkxOVWNDOl9OhSfljra1AwkMS', true);
INSERT INTO user_tb VALUES ('2e97f000-29e1-11ec-9621-0242ac130002', 'Bob', 'Sales', 'bob.sales@gmail.com', '$2a$10$SxX.v94fmc8iino70b78JukYUVnBHM1OIj9I4hN41TdLYtxRNNwbm', true);
INSERT INTO user_tb VALUES ('f311c06a-29e0-11ec-9621-0242ac130002', 'John', 'Doe', 'john.doe@gmail.com', '$2a$10$2NWG8zfJp6kdDFgsp2XRC.CMd9n/1QMfjEejDdCb65P1RFe8cEDNm', true);

CREATE TABLE user_role_tb (
    user_id UUID,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_tb(id),
    FOREIGN KEY (role_id) REFERENCES role_tb(id)
);

INSERT INTO user_role_tb VALUES ('91dc56da-29df-11ec-9621-0242ac130002', 1);
INSERT INTO user_role_tb VALUES ('8879e6d8-29e0-11ec-9621-0242ac130002', 2);
INSERT INTO user_role_tb VALUES ('5f1623c6-2c70-4abc-b360-ac959a42d246', 2);
INSERT INTO user_role_tb VALUES ('2e97f000-29e1-11ec-9621-0242ac130002', 3);
INSERT INTO user_role_tb VALUES ('f311c06a-29e0-11ec-9621-0242ac130002', 4);

CREATE TABLE tracking_tb(
    id UUID,
    correlation_id UUID NOT NULL,
    user_id UUID NOT NULL,
    num_messages INT NOT NULL,
    date_time TIMESTAMP NOT NULL,

    PRIMARY KEY(id),
    FOREIGN KEY(user_id) REFERENCES user_tb(id),
    CONSTRAINT unq_correlation_id UNIQUE(correlation_id),
    CONSTRAINT chk_num_messages CHECK(num_messages >= 0)
);

CREATE TABLE hotel_tb(
    id BIGSERIAL,
    hotel_id VARCHAR(255) NOT NULL,
    hotel_name VARCHAR(255) NOT NULL,
    hotel_email VARCHAR(255) NOT NULL,
    hotel_phone VARCHAR(255) NOT NULL,
    hotel_address VARCHAR(255) NOT NULL,
    hotel_img_file VARCHAR(255) NOT NULL,
    activated UUID NOT NULL,
    deactivated UUID,

    PRIMARY KEY(id),
    FOREIGN KEY(activated) REFERENCES tracking_tb(id),
    FOREIGN KEY(deactivated) REFERENCES tracking_tb(id)
);

CREATE TABLE room_type_tb(
    id BIGSERIAL,
    room_type_id VARCHAR(255) NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_name VARCHAR(255) NOT NULL,
    room_type_price DECIMAL NOT NULL,
    has_breakfast BOOLEAN NOT NULL DEFAULT FALSE,
    has_refund BOOLEAN NOT NULL DEFAULT FALSE,
    max_guests INT NOT NULL,
    room_type_img_file VARCHAR(255) NOT NULL,
    activated UUID NOT NULL,
    deactivated UUID,

    PRIMARY KEY(id),
    FOREIGN KEY(activated) REFERENCES tracking_tb(id),
    FOREIGN KEY(deactivated) REFERENCES tracking_tb(id),
    CONSTRAINT chk_room_price CHECK(room_type_price >= 0),
    CONSTRAINT chk_max_guests CHECK(max_guests >= 0)
);

CREATE TABLE room_tb(
    id BIGSERIAL,
    room_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    activated UUID NOT NULL,
    deactivated UUID,

    PRIMARY KEY(id),
    FOREIGN KEY(activated) REFERENCES tracking_tb(id),
    FOREIGN KEY(deactivated) REFERENCES tracking_tb(id)
);

CREATE TABLE message_tb(
    id BIGSERIAL,
    correlation_id UUID NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    is_successful BOOLEAN NOT NULL DEFAULT FALSE,
    date_time TIMESTAMP NOT NULL,

    PRIMARY KEY(id)
);
