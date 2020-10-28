create table project_members (
    id serial PRIMARY KEY,
    member_firstname varchar(50),
    member_lastname varchar(50),
    member_email varchar(50),
    task_id int references project_tasks(id);
);