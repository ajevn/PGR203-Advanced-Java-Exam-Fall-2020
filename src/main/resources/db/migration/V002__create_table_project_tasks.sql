create table project_tasks (
    id serial PRIMARY KEY,
    task_name varchar(50),
    task_description varchar(100),
    task_status varchar(50),
    member_id int references project_members(id)
);