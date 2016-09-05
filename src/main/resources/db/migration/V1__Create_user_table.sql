create table Users (
    id SERIAL PRIMARY KEY,
    userName varchar(50) not null,
    password varchar(200)

);
create table Places (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES Users(id),
    name varchar(50)
);
create table People (
    id SERIAL PRIMARY KEY,
	first_name varchar(50),
	last_name varchar(50),
    user_id INT REFERENCES Users(id),
    place_id INT REFERENCES Places(id)
);
create table Notes (
    id SERIAL PRIMARY KEY,
	text varchar(500),
	type varchar(100),
	person_id INT REFERENCES People(id)
);
create table Links (
    id SERIAL PRIMARY KEY,
	name varchar(50),
	url varchar(100),
	person_id INT REFERENCES People(id)
);
