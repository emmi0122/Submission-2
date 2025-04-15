package se.yrgo.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import se.yrgo.domain.Author;
import se.yrgo.domain.Book;
import se.yrgo.domain.Reader;

import java.util.List;

public class Main {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistenceUnit");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Author author1 = new Author();
            author1.setName("Victoria Aveyard");
            author1.setNationality("Canada");

            Author author2 = new Author();
            author2.setName("J.R.R. Tolkien");
            author2.setNationality("British");

            Author author3 = new Author();
            author3.setName("George Orwell");
            author3.setNationality("British");

            em.persist(author1);
            em.persist(author2);
            em.persist(author3);

            Book book1 = new Book();
            book1.setTitle("Red Queen");
            book1.setGenre("Fantasy");
            book1.setPublicationYear(2015);
            author1.getBooks().add(book1);

            Book book2 = new Book();
            book2.setTitle("Glass Sword");
            book2.setGenre("Fantasy");
            book2.setPublicationYear(2016);
            author1.getBooks().add(book2);

            Book book3 = new Book();
            book3.setTitle("The Hobbit");
            book3.setGenre("Fantasy");
            book3.setPublicationYear(1937);
            author2.getBooks().add(book3);

            Book book4 = new Book();
            book4.setTitle("1984");
            book4.setGenre("Dystopian");
            book4.setPublicationYear(1949);
            author3.getBooks().add(book4);

            Book book5 = new Book();
            book5.setTitle("Animal Farm");
            book5.setGenre("Satire");
            book5.setPublicationYear(1945);
            author3.getBooks().add(book5);

            em.persist(book1);
            em.persist(book2);
            em.persist(book3);
            em.persist(book4);
            em.persist(book5);

            Reader reader1 = new Reader();
            reader1.setName("Alice Nilsson");
            reader1.setEmail("alice@example.com");
            reader1.getBooks().add(book1);
            reader1.getBooks().add(book2);

            Reader reader2 = new Reader();
            reader2.setName("Bob Bobsson");
            reader2.setEmail("bob@example.com");
            reader2.getBooks().add(book3);
            reader2.getBooks().add(book4);

            Reader reader3 = new Reader();
            reader3.setName("Charlie Wonka");
            reader3.setEmail("charlie@example.com");
            reader3.getBooks().add(book5);
            reader3.getBooks().add(book1);

            em.persist(reader1);
            em.persist(reader2);
            em.persist(reader3);

            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        EntityManager emQuery = emf.createEntityManager();

        try {
            String authorName = "Victoria Aveyard";
            TypedQuery<Book> query1 = emQuery.createQuery(
                    "SELECT b FROM Author a JOIN a.books b WHERE a.name = :authorName", Book.class);
            query1.setParameter("authorName", authorName);
            List<Book> booksByAuthor = query1.getResultList();

            System.out.println("Books by " + authorName + ":");
            for (Book book : booksByAuthor) {
                System.out.println("- " + book.getTitle());
            }

            String authorNameToFind = "J.R.R. Tolkien";
            TypedQuery<Author> query2 = emQuery.createQuery(
                    "SELECT a FROM Author a WHERE a.name = :authorName", Author.class);
            query2.setParameter("authorName", authorNameToFind);
            Author author = query2.getSingleResult();

            System.out.println("\nAuthor: " + author.getName());
            System.out.println("Books by " + author.getName() + ":");
            for (Book book : author.getBooks()) {
                System.out.println("- " + book.getTitle());
            }

            String bookTitle = "Red Queen";
            TypedQuery<Reader> query3 = emQuery.createQuery(
                    "SELECT r FROM Reader r WHERE :book MEMBER OF r.books", Reader.class);
            Book book = emQuery.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class)
                    .setParameter("title", bookTitle).getSingleResult();
            query3.setParameter("book", book);
            List<Reader> readers = query3.getResultList();

            System.out.println("\nReaders of " + bookTitle + ":");
            for (Reader reader : readers) {
                System.out.println("- " + reader.getName());
            }

            TypedQuery<Author> query4 = emQuery.createQuery(
                    "SELECT DISTINCT a FROM Author a JOIN a.books b JOIN b.readers r", Author.class);
            List<Author> authors = query4.getResultList();

            System.out.println("\nAuthors whose books have been read:");
            for (Author a : authors) {
                System.out.println("- " + a.getName());
            }

            TypedQuery<Object[]> query5 = emQuery.createQuery(
                    "SELECT a.name, COUNT(b) FROM Author a JOIN a.books b GROUP BY a.name", Object[].class);
            List<Object[]> results = query5.getResultList();

            System.out.println("\nNumber of books per author:");
            for (Object[] result : results) {
                System.out.println(result[0] + ": " + result[1]);
            }

            TypedQuery<Book> query6 = emQuery.createNamedQuery("Book.findByGenre", Book.class);
            query6.setParameter("genre", "Fantasy");
            List<Book> fantasyBooks = query6.getResultList();

            System.out.println("\nFantasy books:");
            for (Book b : fantasyBooks) {
                System.out.println("- " + b.getTitle());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            emQuery.close();
        }
    }
}