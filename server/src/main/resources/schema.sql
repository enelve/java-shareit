CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(60) UNIQUE NOT NULL,
    name_user varchar(60) NOT NULL);

CREATE TABLE IF NOT EXISTS item (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name_item varchar(60),
    description varchar(60),
    available BOOLEAN,
    user_id BIGINT,
    request_id BIGINT,
    booking_id BIGINT);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    booking_status varchar(60));

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(60),
    item_id BIGINT,
    author_name varchar(60),
    created TIMESTAMP WITHOUT TIME ZONE);

CREATE TABLE IF NOT EXISTS request (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(256),
    user_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE);