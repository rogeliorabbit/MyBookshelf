package com.hitglynorthz.mybookshelf.db;

public class BookBean {

    private int id_book;
    private String title;
    private int author;
    private String authorName;
    private int publisher;
    private String publisherName;
    private String genre;
    private int year;
    private int pages;
    private String lang;
    private int read_status;
    private String isbn;
    private int fav;
    private String cover;
    private int shelves;
    private String shelvesName;
    private int shelf;
    private String shelfName;
    private String descp;
    private String note;
    private int rating;
    private String web_link;
    private String timestamp;

    public BookBean() {}

    public BookBean(int id_book, String title, int author, String authorName, int publisher, String publisherName, String genre, int year, int pages, String lang, int read_status,
                    String isbn, int fav, String cover, int shelves, String shelvesName, int shelf, String shelfName, String descp, String note, int rating, String web_link, String timestamp) {
        this.id_book = id_book;
        this.title = title;
        this.author = author;
        this.authorName = authorName;
        this.publisher = publisher;
        this.publisherName = publisherName;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.lang = lang;
        this.read_status = read_status;
        this.isbn = isbn;
        this.fav = fav;
        this.cover = cover;
        this.shelves = shelves;
        this.shelvesName = shelvesName;
        this.shelf = shelf;
        this.shelfName = shelfName;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
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

    public String getShelvesName() {
        return shelvesName;
    }

    public void setShelvesName(String shelvesName) {
        this.shelvesName = shelvesName;
    }

    public int getShelf() {
        return shelf;
    }

    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
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
