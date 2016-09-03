create table users (
    id SERIAL PRIMARY KEY,
    userName varchar(100) not null,
    password varchar(100)

);
create table places (
    id SERIAL,
    user_id INT REFERENCES users(id)

);
