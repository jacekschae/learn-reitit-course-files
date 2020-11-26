-- Drop tables
drop table if exists message;
drop table if exists conversation;

drop table if exists recipe_favorite;
drop table if exists ingredient;
drop table if exists step;
drop table if exists recipe;

drop table if exists account;

-- Create schema

create table account (
    uid text not null primary key,
    "name" text,
    picture text,
    unique(uid)
);

create table recipe (
    recipe_id text not null primary key,
    "public" boolean not null,
    prep_time int not null,
    "name" text not null,
    img text,
    favorite_count int check (favorite_count >= 0) default 0,
    uid text not null references account(uid) on delete cascade
);

create table step (
    step_id text not null primary key,
    sort int not null,
    description text not null,
    recipe_id text not null references recipe(recipe_id) on delete cascade
);

create table ingredient (
    ingredient_id text not null primary key,
    sort int not null,
    "name" text not null,
    amount int not null,
    measure text not null,
    recipe_id text not null references recipe(recipe_id) on delete cascade
);

create table conversation (
    conversation_id text not null,
    uid text not null,
    notifications int not null check (notifications >= 0) default 0,
    primary key (conversation_id, uid)
);

create table message (
    message_id text not null primary key,
    message_body text not null,
    uid text not null references account(uid) on delete cascade,
    conversation_id text not null,
    created_at timestamp not null default now()
);

create table recipe_favorite (
    id serial not null primary key,
    recipe_id text not null references recipe(recipe_id) on delete cascade,
    uid text not null references account(uid) on delete cascade
);