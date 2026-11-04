package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private HibernateUtil() {}

    public static SessionFactory createSessionFactory() {
        String password = System.getenv("LAB11_DB_PASSWORD");
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("Задайте LAB11_DB_PASSWORD");
        }
        return new Configuration()
                .setProperty("jakarta.persistence.jdbc.password", password)
                .addAnnotatedClass(Department.class)
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }
}
