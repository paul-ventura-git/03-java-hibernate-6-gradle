package com.hibernate.gradle.demo;

import org.hibernate.cfg.Configuration;
import static java.lang.Boolean.TRUE;
import static java.lang.System.out;
import static org.hibernate.cfg.AvailableSettings.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
            
		var sessionFactory = new Configuration()
                .addAnnotatedClass(Book.class)
                // use H2 in-memory database
                .setProperty(URL, "jdbc:postgresql://localhost:5432/hibernate6gradle")
                .setProperty(USER, "postgres")
                .setProperty(PASS, "123")
                // use Agroal connection pool
                .setProperty("hibernate.agroal.maxSize", 20)
                // display SQL in console
                .setProperty(SHOW_SQL, true)
                .setProperty(FORMAT_SQL, true)
                .setProperty(HIGHLIGHT_SQL, true)
                .buildSessionFactory();

                // export the inferred database schema
                try{
                    sessionFactory.getSchemaManager().exportMappedObjects(true);
                }catch(Exception e){
                }

                // persist an entity
                sessionFactory.inTransaction(session -> {
                    session.persist(new Book("9781932394153", "Hibernate in Action (from Hibernate native APIs)"));
                });

                // query data using HQL
                sessionFactory.inSession(session -> {
                    out.println(session.createSelectionQuery("select isbn||': '||title from Book").getSingleResult());
                });

                // query data using criteria API
                sessionFactory.inSession(session -> {
                    var builder = sessionFactory.getCriteriaBuilder();
                    var query = builder.createQuery(String.class);
                    var book = query.from(Book.class); 
                    query.select(builder.concat(builder.concat(book.get(Book_.isbn), builder.literal(": ")),
                            book.get(Book_.title)));
                    out.println(session.createSelectionQuery(query).getSingleResult());
                });
	}

}
