package ru.nsu.egorov.models;

import jakarta.persistence.*;

@Entity
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libraryName;
    private String address;

}
