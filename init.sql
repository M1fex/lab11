BEGIN;
CREATE TABLE departments (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name CHAR(30) NOT NULL,
    phone CHAR(15)
);
CREATE TABLE employees (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    surname VARCHAR(20) NOT NULL,
    name VARCHAR(10) NOT NULL,
    position VARCHAR(20),
    department_id INTEGER NOT NULL REFERENCES departments(id)
);
CREATE TABLE tasks (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(50) NOT NULL,
    employee_id INTEGER NOT NULL REFERENCES employees(id)
);
INSERT INTO departments (name, phone) VALUES ('IT', '111111111'), ('HR', '222222222');
INSERT INTO employees (surname, name, position, department_id) VALUES
    ('Kovalenko', 'Ivan', 'Developer', 1),
    ('Apriatkin', 'Mykhailo', 'DevOps', 1),
    ('Kruvshenko', 'Viktoria', 'HR', 2);
INSERT INTO tasks (description, employee_id) VALUES ('Configure CI pipeline', 2);
COMMIT;
