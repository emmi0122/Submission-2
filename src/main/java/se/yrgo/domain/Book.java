package se.yrgo.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String genre;
    private int publicationYear;

    @ManyToMany
    @JoinTable(name = "book_reader", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "reader_id"))
    private Set<Reader> readers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Set<Reader> getReaders() {
        return readers;
    }

    public void setReaders(Set<Reader> readers) {
        this.readers = readers;
    }

    public void addReader(Reader reader) {
        this.readers.add(reader);
        reader.getBooks().add(this);
    }
}