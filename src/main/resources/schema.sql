create table if not exists contacts
(
    id          serial PRIMARY KEY,
    name        varchar(50)  not null,
    region      varchar(255) not null,
    locality    varchar(255),
    description text
);
create table if not exists contacts_info_emails
(
    id    serial primary key,
    email varchar(255) not null
);
create table if not exists contacts_info_numbers
(
    id     serial primary key,
    number varchar(12) not null
);
create table if not exists contacts_photos
(
    id             serial not null unique references contacts on delete cascade,
    file_extension text   not null
);

create table if not exists contacts_emails
(
    contact_id serial not null
        references contacts ON DELETE CASCADE,
    emails_id  serial not null
        unique
        references contacts_info_emails ON DELETE CASCADE
);
create table if not exists contacts_numbers
(
    contact_id serial not null
        references contacts ON DELETE CASCADE,
    numbers_id serial not null
        unique
        references contacts_info_numbers ON DELETE CASCADE
);