package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (SessionFactory factory = HibernateUtil.createSessionFactory();
             Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.print("""
                        
                        1. Усі співробітники
                        2. Усі завдання
                        3. Співробітники відділу
                        4. Додати завдання
                        5. Завдання співробітника
                        6. Видалити співробітника
                        0. Вихід
                        > """);
                if (!input.hasNextLine()) return;
                try {
                    int choice = Integer.parseInt(input.nextLine().strip());
                    switch (choice) {
                        case 0 -> { return; }
                        case 1 -> showEmployees(factory);
                        case 2 -> showTasks(factory);
                        case 3 -> showEmployeesByDepartment(factory, readId(input, "Номер відділу"));
                        case 4 -> {
                            int employeeId = readId(input, "Номер співробітника");
                            System.out.println("Опис завдання:");
                            if (!input.hasNextLine()) return;
                            addTask(factory, employeeId, input.nextLine());
                        }
                        case 5 -> showTasksByEmployee(factory, readId(input, "Номер співробітника"));
                        case 6 -> deleteEmployee(factory, readId(input, "Номер співробітника"));
                        default -> System.out.println("Невідомий пункт меню");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Потрібно ввести ціле число");
                } catch (RuntimeException e) {
                    System.out.println("Операцію не виконано: " + e.getMessage());
                }
            }
        }
    }

    private static int readId(Scanner input, String prompt) {
        System.out.println(prompt + ":");
        return Integer.parseInt(input.nextLine().strip());
    }

    static void showEmployees(SessionFactory factory) {
        try (Session session = factory.openSession()) {
            session.createSelectionQuery("from Employee e order by e.id", Employee.class)
                    .getResultList().forEach(Main::printEmployee);
        }
    }

    static void showTasks(SessionFactory factory) {
        try (Session session = factory.openSession()) {
            session.createSelectionQuery("from Task t order by t.id", Task.class)
                    .getResultList().forEach(Main::printTask);
        }
    }

    static void showEmployeesByDepartment(SessionFactory factory, int departmentId) {
        try (Session session = factory.openSession()) {
            session.createSelectionQuery(
                    "from Employee e where e.department.id = :id order by e.id", Employee.class)
                    .setParameter("id", departmentId).getResultList().forEach(Main::printEmployee);
        }
    }

    static void showTasksByEmployee(SessionFactory factory, int employeeId) {
        try (Session session = factory.openSession()) {
            session.createSelectionQuery(
                    "from Task t where t.employee.id = :id order by t.id", Task.class)
                    .setParameter("id", employeeId).getResultList().forEach(Main::printTask);
        }
    }

    static void addTask(SessionFactory factory, int employeeId, String description) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Task task = new Task();
                task.setDescription(description);
                task.setEmployee(findEmployee(session, employeeId));
                session.persist(task);
                transaction.commit();
                System.out.println("Додано завдання з id: " + task.getId());
            } catch (RuntimeException e) {
                if (transaction.isActive()) transaction.rollback();
                throw e;
            }
        }
    }

    static void deleteEmployee(SessionFactory factory, int employeeId) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(findEmployee(session, employeeId));
                transaction.commit();
                System.out.println("Співробітника видалено");
            } catch (RuntimeException e) {
                if (transaction.isActive()) transaction.rollback();
                throw e;
            }
        }
    }

    private static Employee findEmployee(Session session, int employeeId) {
        Employee employee = session.find(Employee.class, employeeId);
        if (employee == null) throw new IllegalArgumentException("Співробітника не знайдено");
        return employee;
    }

    private static void printEmployee(Employee employee) {
        System.out.printf("%d | %s %s | %s%n", employee.getId(), employee.getSurname(),
                employee.getName(), employee.getDepartment().getName().stripTrailing());
    }

    private static void printTask(Task task) {
        System.out.printf("%d | %s | %s%n", task.getId(), task.getDescription(),
                task.getEmployee().getSurname());
    }
}
