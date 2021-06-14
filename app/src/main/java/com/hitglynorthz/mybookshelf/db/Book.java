package com.hitglynorthz.mybookshelf.db;

public class Book {

    // nombre de la tabla
    public static final String TABLE_NAME = "Books";

    // elementos de la tabla
    public static final String COLUMN_BOOK_ID = "id_book";
    public static final String COLUMN_BOOK_TITLE = "book_title";
    public static final String COLUMN_BOOK_AUTHOR = "id_author";
    public static final String COLUMN_BOOK_PUBLISHER = "id_publisher";
    public static final String COLUMN_BOOK_GENRE = "genre";
    public static final String COLUMN_BOOK_YEAR = "book_year";
    public static final String COLUMN_BOOK_PAGES = "book_pages";
    public static final String COLUMN_BOOK_LANG = "book_lang";
    public static final String COLUMN_BOOK_READ_STATUS = "book_read_status";
    public static final String COLUMN_BOOK_ISBN = "book_isbn";
    public static final String COLUMN_BOOK_FAV = "book_fav";
    public static final String COLUM_BOOK_WISHLIST = "book_wishlist";
    public static final String COLUMN_BOOK_COVER = "book_cover";
    public static final String COLUMN_BOOK_SHELVES = "id_shelves";
    public static final String COLUMN_BOOK_SHELF = "id_shelf";
    public static final String COLUMN_BOOK_DESCP = "book_descp";
    public static final String COLUMN_BOOK_NOTE = "book_note";
    public static final String COLUMN_BOOK_RATING = "book_rating";
    public static final String COLUMN_BOOK_WEB_LINK = "book_web_link";
    public static final String COLUMN_BOOK_TIMESTAMP = "timestamp";

    // creacion de la tabla Books
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_BOOK_TITLE + " TEXT, "
                    + COLUMN_BOOK_AUTHOR + " INTEGER, "
                    + COLUMN_BOOK_PUBLISHER + " INTEGER, "
                    + COLUMN_BOOK_GENRE + " TEXT, "
                    + COLUMN_BOOK_YEAR + " INTEGER DEFAULT 1, "
                    + COLUMN_BOOK_PAGES + " INTEGER DEFAULT 0, "
                    + COLUMN_BOOK_LANG + " TEXT, "
                    + COLUMN_BOOK_READ_STATUS + " INTEGER DEFAULT 0, "
                    + COLUMN_BOOK_ISBN + " TEXT, "
                    + COLUMN_BOOK_FAV + " INTEGER DEFAULT 0, "
                    + COLUM_BOOK_WISHLIST + " INTEGER DEFAULT 0, "
                    + COLUMN_BOOK_COVER + " TEXT, "
                    + COLUMN_BOOK_SHELVES + " INTEGER, "
                    + COLUMN_BOOK_SHELF + " INTEGER, "
                    + COLUMN_BOOK_DESCP + " TEXT, "
                    + COLUMN_BOOK_NOTE + " TEXT, "
                    + COLUMN_BOOK_RATING + " INTEGER DEFAULT 0, "
                    + COLUMN_BOOK_WEB_LINK + " TEXT, "
                    + COLUMN_BOOK_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY(" + COLUMN_BOOK_AUTHOR + ") REFERENCES " + Author.TABLE_NAME + "(" + Author.COLUMN_AUTHOR_ID + "), "
                    + "FOREIGN KEY(" + COLUMN_BOOK_PUBLISHER + ") REFERENCES " + Publisher.TABLE_NAME + "(" + Publisher.COLUMN_PUBLISHER_ID + "), "
                    + "FOREIGN KEY(" + COLUMN_BOOK_SHELVES + ") REFERENCES " + Shelves.TABLE_NAME + "(" + Shelves.COLUMN_SHELVES_ID + "), "
                    + "FOREIGN KEY(" + COLUMN_BOOK_SHELF + ") REFERENCES " + Shelf.TABLE_NAME + "(" + Shelf.COLUMN_SHELF_ID + ")"
                    + ")";

    private int id_book;
    private String title;
    private int author;
    private int publisher;
    private String genre;
    private int year;
    private int pages;
    private String lang;
    private int read_status;
    private String isbn;
    private int fav;
    private String cover;
    private int shelves;
    private int shelf;
    private String descp;
    private String note;
    private int rating;
    private String web_link;
    private String timestamp;

    public Book() {}

    public Book(int id_book, String title, int author, int publisher, String genre, int year, int pages, String lang,
                int read_status, String isbn, int fav, String cover, int shelves, int shelf, String descp, String note,
                int rating, String web_link, String timestamp) {
        this.id_book = id_book;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.lang = lang;
        this.read_status = read_status;
        this.isbn = isbn;
        this.fav = fav;
        this.cover = cover;
        this.shelves = shelves;
        this.shelf = shelf;
        this.descp = descp;
        this.note = note;
        this.rating = rating;
        this.web_link = web_link;
        this.timestamp = timestamp;
    }

    public int getId_book() {
        return id_book;
    }

    public void setId_book(int id_book) {
        this.id_book = id_book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getRead_status() {
        return read_status;
    }

    public void setRead_status(int read_status) {
        this.read_status = read_status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getShelves() {
        return shelves;
    }

    public void setShelves(int shelves) {
        this.shelves = shelves;
    }

    public int getShelf() {
        return shelf;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getWeb_link() {
        return web_link;
    }

    public void setWeb_link(String web_link) {
        this.web_link = web_link;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
