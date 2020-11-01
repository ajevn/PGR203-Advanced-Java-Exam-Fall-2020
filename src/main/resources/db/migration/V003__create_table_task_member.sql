create table member_task (
    member_id int,
    task_id int,
    FOREIGN KEY (member_id) REFERENCES project_members (id),
    FOREIGN KEY (task_id) REFERENCES project_tasks (id),
    PRIMARY KEY (member_id, task_id)
)