package com.hitglynorthz.mybookshelf.db;

public class Author {

    // nombre de la tabla
    public static final String TABLE_NAME = "Authors";

    // elementos de la tabla
    public static final String COLUMN_AUTHOR_ID = "id_author";
    public static final String COLUMN_AUTHOR_NAME = "author_name";
    public static final String COLUMN_AUTHOR_TIMESTAMP = "timestamp";

    // creacion de la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_AUTHOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_AUTHOR_NAME + " TEXT, "
                    + COLUMN_AUTHOR_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private int id_author;
    private String author_name;
    private String timestamp;

    public Author() {}

    public Author(int id_author, String author_name, String timestamp) {
        this.id_author = id_author;
        this.author_name = author_name;
        this.timestamp = timestamp;
    }

    public int getId_author() {
        return id_author;
    }

    public void setId_author(int id_author) {
        this.id_author = id_author;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
