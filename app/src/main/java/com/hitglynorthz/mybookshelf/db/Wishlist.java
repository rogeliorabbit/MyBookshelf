package com.hitglynorthz.mybookshelf.db;

public class Wishlist {

    // nombre de la tabla
    public static final String TABLE_NAME = "Wishlist";

    // elementos de la tabla
    public static final String COLUMN_WISHLIST_ID = "id_wishlist";
    public static final String COLUMN_WISHLIST_TITLE = "title";
    public static final String COLUMN_WISHLIST_AUTHOR = "author";
    public static final String COLUMN_WISHLIST_PUBLISHER = "publisher";
    public static final String COLUMN_WISHLIST_ISBN = "isbn";
    public static final String COLUMN_WISHLIST_TIMESTAMP = "timestamp";

    // creacion de la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_WISHLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_WISHLIST_TITLE + " TEXT, "
                    + COLUMN_WISHLIST_AUTHOR + " TEXT, "
                    + COLUMN_WISHLIST_PUBLISHER + " TEXT, "
                    + COLUMN_WISHLIST_ISBN + " TEXT, "
                    + COLUMN_WISHLIST_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private int id_wishlist;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String timestamp;

    public Wishlist() {}

    public Wishlist(int id_wishlist, String title, String author, String publisher, String isbn, String timestamp) {
        this.id_wishlist = id_wishlist;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.timestamp = timestamp;
    }

    public int getId_wishlist() {
        return id_wishlist;
    }

    public void setId_wishlist(int id_wishlist) {
        this.id_wishlist = id_wishlist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
