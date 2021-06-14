package com.hitglynorthz.mybookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hitglynorthz.mybookshelf.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books_db.hnz.db";

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creamos las tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SQL_SHELVES", Shelves.CREATE_TABLE);
        db.execSQL(Shelves.CREATE_TABLE);
        Log.i("SQL_SHELFS", Shelf.CREATE_TABLE);
        db.execSQL(Shelf.CREATE_TABLE);
        Log.i("SQL_AUTHORS", Author.CREATE_TABLE);
        db.execSQL(Author.CREATE_TABLE);
        Log.i("SQL_PUBLISHERS", Publisher.CREATE_TABLE);
        db.execSQL(Publisher.CREATE_TABLE);
        Log.i("SQL", Book.CREATE_TABLE);
        db.execSQL(Book.CREATE_TABLE);
        Log.i("SQL", Wishlist.CREATE_TABLE);
        db.execSQL(Wishlist.CREATE_TABLE);
    }

    // si se necesita actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Shelf.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Shelves.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Author.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Publisher.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Wishlist.TABLE_NAME);
        onCreate(db);
    }

    // borrar libros y wishlist
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Shelf.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Shelves.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Author.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Publisher.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Wishlist.TABLE_NAME);
        onCreate(db);
    }

    /*
        INSERTS
     */

    // insertar Book
    public long insertBook(String title, int id_author, int id_publisher, String genre, int year, int pages, String lang, int read_status, String isbn,
                           String cover, int id_shelves, int id_shelf, String descp, String web_link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Book.COLUMN_BOOK_TITLE, title);
        values.put(Book.COLUMN_BOOK_AUTHOR, id_author);
        values.put(Book.COLUMN_BOOK_PUBLISHER, id_publisher);
        values.put(Book.COLUMN_BOOK_GENRE, genre);
        values.put(Book.COLUMN_BOOK_YEAR, year);
        values.put(Book.COLUMN_BOOK_PAGES, pages);
        values.put(Book.COLUMN_BOOK_LANG, lang);
        values.put(Book.COLUMN_BOOK_READ_STATUS, read_status);
        values.put(Book.COLUMN_BOOK_ISBN, isbn);
        values.put(Book.COLUMN_BOOK_COVER, cover);
        values.put(Book.COLUMN_BOOK_SHELVES, id_shelves);
        values.put(Book.COLUMN_BOOK_SHELF, id_shelf);
        values.put(Book.COLUMN_BOOK_DESCP, descp);
        values.put(Book.COLUMN_BOOK_WEB_LINK, web_link);

        long id = db.insert(Book.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // insertar Author
    public long insertAuthor(String authorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Author.COLUMN_AUTHOR_NAME, authorName);

        long id = db.insert(Author.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // insertar Publisher
    public long insertPublisher(String publisherName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Publisher.COLUMN_PUBLISHER_NAME, publisherName);

        long id = db.insert(Publisher.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // insertar Shelves
    public long insertShelves(String shelvesName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Shelves.COLUMN_SHELVES_NAME, shelvesName);

        long id = db.insert(Shelves.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // insertar Shelf
    public long insertShelf(String shelfName, int shelvesId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Shelf.COLUMN_SHELF_NAME, shelfName);
        values.put(Shelf.COLUM_SHELF_ID_SHELVES, shelvesId);

        long id = db.insert(Shelf.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    /*
        DELETES
     */

    // borrar libro segun su id
    public void deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_BOOK_ID + " = " + id, null);
        db.close();
    }

    // borrar author
    public void deleteAuthor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Author.TABLE_NAME, Author.COLUMN_AUTHOR_ID + " = " + id, null);
        db.close();
    }

    // borrar publisher
    public void deletePublisher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Publisher.TABLE_NAME, Publisher.COLUMN_PUBLISHER_ID + " = " + id, null);
        db.close();
    }

    // borrar shelves
    public void deleteShelves(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shelves.TABLE_NAME, Shelves.COLUMN_SHELVES_ID + " = " + id, null);
        db.close();
    }

    // borrar shelf
    public void deleteShelf(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shelf.TABLE_NAME, Shelf.COLUMN_SHELF_ID + " = " + id, null);
        db.close();
    }

    // borrar libro segun su author
    public void deleteBookByAuthorId(int authorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_BOOK_AUTHOR + " = " + authorId, null);
        db.close();
    }

    // borrar libro segun su publisher
    public void deleteBookByPublisherId(int publisherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_BOOK_PUBLISHER + " = " + publisherId, null);
        db.close();
    }

    // borrar libro segun su shelves
    public void deleteBookByShelvesId(int shelvesId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_BOOK_SHELVES + " = " + shelvesId, null);
        db.close();
    }

    // borrar libro segun su shelf
    public void deleteBookByShelfId(int shelfId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_BOOK_SHELF + " = " + shelfId, null);
        db.close();
    }

    // borrar shelf by shelves
    public void deleteShelfByShelves(int shelvesId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Shelf.TABLE_NAME, Shelf.COLUM_SHELF_ID_SHELVES + " = " + shelvesId, null);
        db.close();
    }

    /*
        UPDATES
     */

    // actualizar informacion de un libro segun su id
    public void updateBook(int id, String title, int id_author, int id_publisher, String genre, int year, int pages, String lang, int read_status, String isbn, String cover,
                           int id_shelves, int id_shelf, String descp, String web_link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_TITLE, title);
        cv.put(Book.COLUMN_BOOK_AUTHOR, id_author);
        cv.put(Book.COLUMN_BOOK_PUBLISHER, id_publisher);
        cv.put(Book.COLUMN_BOOK_GENRE, genre);
        cv.put(Book.COLUMN_BOOK_YEAR, year);
        cv.put(Book.COLUMN_BOOK_PAGES, pages);
        cv.put(Book.COLUMN_BOOK_LANG, lang);
        cv.put(Book.COLUMN_BOOK_READ_STATUS, read_status);
        cv.put(Book.COLUMN_BOOK_ISBN, isbn);
        cv.put(Book.COLUMN_BOOK_COVER, cover);
        cv.put(Book.COLUMN_BOOK_SHELVES, id_shelves);
        cv.put(Book.COLUMN_BOOK_SHELF, id_shelf);
        cv.put(Book.COLUMN_BOOK_DESCP, descp);
        cv.put(Book.COLUMN_BOOK_WEB_LINK, web_link);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_ID + " = " + id, null);
    }

    // actualizar nombre Author
    public void updateAuthorName(int authorId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Author.COLUMN_AUTHOR_NAME, newName);
        db.update(Author.TABLE_NAME, cv, Author.COLUMN_AUTHOR_ID + " = " + authorId, null);
        db.close();
    }

    // actualizar nombre Publisher
    public void updatePublisherName(int publisherId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Publisher.COLUMN_PUBLISHER_NAME, newName);
        db.update(Publisher.TABLE_NAME, cv, Publisher.COLUMN_PUBLISHER_ID + " = " + publisherId, null);
        db.close();
    }

    // actualizar nombre Shelves
    public void updateShelvesName(int shelvesId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Shelves.COLUMN_SHELVES_NAME, newName);
        db.update(Shelves.TABLE_NAME, cv, Shelves.COLUMN_SHELVES_ID + " = " + shelvesId, null);
        db.close();
    }

    // actualizar nombre Shelf
    public void updateShelfName(int shelfId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Shelf.COLUMN_SHELF_NAME, newName);
        db.update(Shelf.TABLE_NAME, cv, Shelf.COLUMN_SHELF_ID + " = " + shelfId, null);
        db.close();
    }

    //
    public void updateBookAuthor(int newAuthorId, int oldAuthorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_AUTHOR, newAuthorId);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_AUTHOR + " = " + oldAuthorId, null);
        db.close();
    }

    //
    public void updateBookPublisher(int newPublisherId, int oldPublisherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_PUBLISHER, newPublisherId);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_PUBLISHER + " = " + oldPublisherId, null);
        db.close();
    }

    //
    public void updateBookShelves(int newShelvesId, int oldShelvesId, int newShelfId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_SHELVES, newShelvesId);
        cv.put(Book.COLUMN_BOOK_SHELF, newShelfId);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_SHELVES + " = " + oldShelvesId, null);
        db.close();
    }

    //
    public void updateBookShelvesShelf(int newShelvesId, int oldShelvesId, int newShelfId, int oldShelfId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_SHELVES, newShelvesId);
        cv.put(Book.COLUMN_BOOK_SHELF, newShelfId);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_SHELVES + " = " + oldShelvesId + " AND " + Book.COLUMN_BOOK_SHELF + " = " + oldShelfId, null);
        db.close();
    }

    //
    public void updateShelvesTimestamp(int shelvesID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Shelves.COLUMN_SHELVES_TIMESTAMP, Utils.getDateTime());
        db.update(Shelves.TABLE_NAME, cv, Shelves.COLUMN_SHELVES_ID + " = " + shelvesID, null);
        db.close();
    }

    /*
        DATOS BOOKS
     */

    // recuperamos todos los libros en una List<BookBean>
    public List<BookBean> getAllBooksBean() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "order by b.id_book desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos informacion de un libro segun el ID
    public Book getBook(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Book book = null;

        if (cursor != null) {
            cursor.moveToFirst();

            book = new Book(
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_NOTE)),
                    cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)),
                    cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP))
            );
            cursor.close();
        }
        db.close();

        return book;
    }

    // recuperamos todos los libros ordenados alfabeticamente en una List<BookBean>
    public List<BookBean> getAllBooksBeanAZ() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos todos los libros no leidos en una List<BookBean>
    public List<BookBean> getAllBooksBeanUnreaded() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.book_read_status = 0 " +
                "order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos todos los libros que se estan leyendo en una List<BookBean>
    public List<BookBean> getAllBooksBeanReading() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.book_read_status = 1 " +
                "order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos todos los libros leidos en una List<BookBean>
    public List<BookBean> getAllBooksBeanReaded() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.book_read_status = 2 " +
                "order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos todos los libros en favoritos en una List<BookBean>
    public List<BookBean> getAllBooksBeanFavs() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.book_fav = 1 " +
                "order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // recuperamos todos los libros ordenados por mejor valoracion
    // sin contar los que no tienen valoracion List<BookBean>
    public List<BookBean> getBestRatingBooksBean() {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.book_rating > 0 " +
                "order by b.book_rating desc, " +
                "b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // comprobar fav de un libro segun id
    public int checkIfBookFav(int id) {
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_FAV + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int fav = cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV));

        cursor.close();
        db.close();

        return fav;
    }

    // actualizar el fav de un libro
    public void updateBookFav(int fav, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_FAV, fav);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_ID + " = " + id, null);
        db.close();
    }

    // nota de un libro segun su id
    public String getBookNote(int id) {
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_NOTE + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        String note = cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_NOTE));

        cursor.close();
        db.close();

        return note;
    }

    // guardar la nota de un libro
    public void updateBookNote(String note, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_NOTE, note);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_ID + " = " + id, null);
        db.close();
    }

    // comprobar el read_status de un libro segun su id
    public int checkBookReadStatus(int id) {
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_READ_STATUS + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int read_status = cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS));

        cursor.close();
        db.close();

        return read_status;
    }

    // actualizar el read_status de un libro
    public void updateBookReadStatus(int read_status, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_READ_STATUS, read_status);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_ID + " = " + id, null);
        db.close();
    }

    // valoracion de un libro segun su id
    public int checkBookRating(int id) {
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_RATING + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor != null)
            cursor.moveToFirst();

        int rating = cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING));

        cursor.close();
        db.close();

        return rating;
    }

    // actualizar el rating de un libro
    public void updateBookRating(int rating, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Book.COLUMN_BOOK_RATING, rating);
        db.update(Book.TABLE_NAME, cv, Book.COLUMN_BOOK_ID + " = " + id, null);
        db.close();
    }

    // libros por id de autor List<BookBean>
    public List<BookBean> getBooksBeanFromAuthorById(int authorId) {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.id_author = " + authorId +
                " order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // libros por id de editorial List<BookBean>
    public List<BookBean> getBooksBeanFromPublisherById(int publisherId) {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.id_publisher = " + publisherId +
                " order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // libros por id de estanteria List<BookBean>
    public List<BookBean> getBooksBeanFromShelvesById(int shelvesId) {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.id_shelves = " + shelvesId +
                " order by b.timestamp desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // libros por id de estante List<BookBean>
    public List<BookBean> getBooksBeanFromShelfById(int shelfId) {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.id_shelf = " + shelfId +
                " order by b.timestamp desc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // libros por genero List<BookBean>
    public List<BookBean> getBooksBeanFromGenre(String genre) {
        List<BookBean> books = new ArrayList<>();
        String selectQuery = "select b.id_book, b.book_title, b.id_author, a.author_name, b.id_publisher, p.publisher_name, b.genre, " +
                "b.book_year, b.book_pages, b.book_lang, b.book_read_status, b.book_isbn, b.book_fav, b.book_cover, b.id_shelves, " +
                "s.shelves_name, b.id_shelf, f.shelf_name, b.book_descp, b.book_note, b.book_rating, b.book_web_link, b.timestamp " +
                "from Books b " +
                "inner join Authors a " +
                "on a.id_author = b.id_author " +
                "inner join Publishers p " +
                "on p.id_publisher = b.id_publisher " +
                "inner join Shelves s " +
                "on s.id_shelves = b.id_shelves " +
                "inner join Shelfs f " +
                "on f.id_shelf = b.id_shelf " +
                "where b.genre LIKE " + "'" + genre.replace("'", "''") + "'" +
                " order by b.book_title asc";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                BookBean book = new BookBean();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setAuthorName(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setPublisherName(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelvesName(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setShelfName(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return  books;
    }

    // books por id de author except
    public List<Book> getBooksFromAuthorByIdExcept(int authorId, int bookId) {
        List<Book> books = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_AUTHOR + " = " + authorId
                + " AND " + Book.COLUMN_BOOK_ID + " != " + bookId + " ORDER BY " + Book.COLUMN_BOOK_TITLE + " ASC LIMIT 10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return books;
    }

    // books por id de publisher except
    public List<Book> getBooksFromPublisherByIdExcept(int publisherId, int bookId) {
        List<Book> books = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_PUBLISHER + " = " + publisherId
                + " AND " + Book.COLUMN_BOOK_ID + " != " + bookId + " ORDER BY " + Book.COLUMN_BOOK_TITLE + " ASC LIMIT 10";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return books;
    }

    // todos los generos
    public List<String> getAllGenres() {
        List<String> genresList = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + Book.COLUMN_BOOK_GENRE + " FROM " + Book.TABLE_NAME + " ORDER BY " + Book.COLUMN_BOOK_GENRE + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                genresList.add(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return genresList;
    }

    // books from genre except
    public List<Book> getBooksFromGenreExcept(String genre, int bookId) {
        List<Book> books = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_GENRE + " LIKE " + "'" + genre.replace("'", "''") + "'"
                + " AND " + Book.COLUMN_BOOK_GENRE + " NOT NULL AND " + Book.COLUMN_BOOK_GENRE + " != " + "'" + "'"
                + " AND " + Book.COLUMN_BOOK_ID + " != " + bookId + " ORDER BY " + Book.COLUMN_BOOK_TITLE + " ASC LIMIT 10";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return books;
    }

    // books from genre limit
    public List<Book> getBooksFromGenreLimit(String genre) {
        List<Book> books = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_GENRE + " LIKE " + "'" + genre.replace("'", "''") + "'"
                + " ORDER BY random() ASC LIMIT 6";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId_book(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TITLE)));
                book.setAuthor(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
                book.setPublisher(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
                book.setGenre(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
                book.setYear(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_YEAR)));
                book.setPages(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PAGES)));
                book.setLang(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_LANG)));
                book.setRead_status(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_READ_STATUS)));
                book.setIsbn(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_ISBN)));
                book.setFav(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_FAV)));
                book.setCover(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_COVER)));
                book.setShelves(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELVES)));
                book.setShelf(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_SHELF)));
                book.setDescp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_DESCP)));
                book.setRating(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_RATING)));
                book.setWeb_link(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_WEB_LINK)));
                book.setTimestamp(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_TIMESTAMP)));

                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return books;
    }

    public List<String> getMostGenres() {
        List<String> genresList = new ArrayList<>();
        String selectQuery = "Select genre from Books group by genre order by count(*) desc limit 3";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                genresList.add(cursor.getString(cursor.getColumnIndex(Book.COLUMN_BOOK_GENRE)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return genresList;
    }

    /*
        DATOS AUTHOR
     */

    // recuperamos todos los autores List<Author>
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Author.TABLE_NAME + " GROUP BY " + Author.COLUMN_AUTHOR_ID + " ORDER BY " +
                Author.COLUMN_AUTHOR_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Author author = new Author();
                author.setId_author(cursor.getInt(cursor.getColumnIndex(Author.COLUMN_AUTHOR_ID)));
                author.setAuthor_name(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                author.setTimestamp(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_TIMESTAMP)));

                authors.add(author);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authors;
    }

    // recuperamos todos los autores List<Author> con mas libros
    public List<Author> getAllAuthorsNBooks() {
        List<Author> authors = new ArrayList<>();

        //String selectQuery = "select * from Authors inner join Books on Authors.id_author = Books.id_author group by Books.id_author order by count(*) desc, Authors.author_name asc";
        String selectQuery = "SELECT * FROM " + Author.TABLE_NAME + " INNER JOIN " + Book.TABLE_NAME
                + " ON " + Author.TABLE_NAME + "." + Author.COLUMN_AUTHOR_ID + " = "+ Book.TABLE_NAME + "." + Book.COLUMN_BOOK_AUTHOR
                + " GROUP BY " + Book.TABLE_NAME + "." + Book.COLUMN_BOOK_AUTHOR + " ORDER BY COUNT(*) DESC, " + Author.COLUMN_AUTHOR_ID + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Author author = new Author();
                author.setId_author(cursor.getInt(cursor.getColumnIndex(Author.COLUMN_AUTHOR_ID)));
                author.setAuthor_name(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                author.setTimestamp(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_TIMESTAMP)));

                authors.add(author);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authors;
    }

    // recuperamos los autores en una List<String> para usar en el AutoCompleteTextView
    public List<String> getListAuthors() {
        List<String> authorList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Author.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // get Author ID by Name
    public int getAuthorIdByName(String authorName) {
        int authorId = 0;
        String selectQuery = "SELECT " + Author.COLUMN_AUTHOR_ID + " FROM " + Author.TABLE_NAME + " WHERE " + Author.COLUMN_AUTHOR_NAME
                + " LIKE " + "'" + authorName.replace("'", "''") + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            authorId = cursor.getInt(cursor.getColumnIndex(Author.COLUMN_AUTHOR_ID));
        }
        cursor.close();
        db.close();

        return authorId;
    }

    // get Author name by ID
    public String getAuthorNameById(int authorId) {
        String selectQuery = "SELECT " + Author.COLUMN_AUTHOR_NAME + " FROM " + Author.TABLE_NAME + " WHERE " + Author.COLUMN_AUTHOR_ID + " = " + authorId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String authorName = "";
        if (cursor.moveToFirst()) {
            authorName = cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME));
        }
        cursor.close();
        db.close();

        return authorName;
    }

    // get authorID by publisherId
    public List<Integer> getAuthorIdbyPublisherId(int publisherId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_AUTHOR + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_PUBLISHER + " = " + publisherId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // get authorId by shelvesId
    public List<Integer> getAuthorIdByShelvesId(int shelvesId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_AUTHOR + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELVES + " = " + shelvesId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // get authorId by shelfId
    public List<Integer> getAuthorIdByShelfId(int shelfId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_AUTHOR + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELF + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_AUTHOR)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // Author con mas libros
    public List<Author> getAuthorMostBooks() {
        List<Author> authorList = new ArrayList<>();
        //String selectQuery = "select * from Authors where id_author in (select id_author from Books group by id_author order by count(*) desc limit 3)";
        String selectQuery = "select * from Authors left join Books on Authors.id_author = Books.id_author group by Books.id_author order by count(*) desc limit 3";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Author author = new Author();
                author.setId_author(cursor.getInt(cursor.getColumnIndex(Author.COLUMN_AUTHOR_ID)));
                author.setAuthor_name(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                author.setTimestamp(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_TIMESTAMP)));

                authorList.add(author);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // Author con mas libros leidos
    public List<Author> getAuthorMostReaded() {
        List<Author> authorList = new ArrayList<>();
        //String selectQuery = "select * from Authors where id_author in (select id_author from Books where book_read_status = 2 group by id_author order by count(*) desc limit 3)";
        String selectQuery = "select * from Authors left join Books on Authors.id_author = Books.id_author where book_read_status = 2 group by Books.id_author order by count(*) desc limit 3";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Author author = new Author();
                author.setId_author(cursor.getInt(cursor.getColumnIndex(Author.COLUMN_AUTHOR_ID)));
                author.setAuthor_name(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_NAME)));
                author.setTimestamp(cursor.getString(cursor.getColumnIndex(Author.COLUMN_AUTHOR_TIMESTAMP)));

                authorList.add(author);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    /*
        DATOS PUBLISHER
     */

    // recuperamos todas las editoriales List<Publisher>
    public List<Publisher> getAllPublishers() {
        List<Publisher> publishers = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Publisher.TABLE_NAME + " GROUP BY " + Publisher.COLUMN_PUBLISHER_NAME + " ORDER BY " +
                Publisher.COLUMN_PUBLISHER_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Publisher publisher = new Publisher();
                publisher.setId_publisher(cursor.getInt(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_ID)));
                publisher.setPublisher_name(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                publisher.setTimestamp(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_TIMESTAMP)));

                publishers.add(publisher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return publishers;
    }

    // recuperamos todas las editoriales List<Publisher> con mas libros
    public List<Publisher> getAllPublishersNBooks() {
        List<Publisher> publishers = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Publisher.TABLE_NAME + " INNER JOIN " + Book.TABLE_NAME
                + " ON " + Publisher.TABLE_NAME + "." + Publisher.COLUMN_PUBLISHER_ID + " = " + Book.TABLE_NAME + "." + Book.COLUMN_BOOK_PUBLISHER
                + " GROUP BY " + Book.TABLE_NAME + "." + Book.COLUMN_BOOK_PUBLISHER + " ORDER BY COUNT(*) DESC, " + Publisher.TABLE_NAME + "." + Publisher.COLUMN_PUBLISHER_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Publisher publisher = new Publisher();
                publisher.setId_publisher(cursor.getInt(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_ID)));
                publisher.setPublisher_name(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                publisher.setTimestamp(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_TIMESTAMP)));

                publishers.add(publisher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return publishers;
    }

    // get Publisher ID by Name
    public int getPublisherIdByName(String publisherName) {
        int publisherId = 0;
        String selectQuery = "SELECT " + Publisher.COLUMN_PUBLISHER_ID + " FROM " + Publisher.TABLE_NAME + " WHERE " + Publisher.COLUMN_PUBLISHER_NAME
                + " LIKE " + "'" + publisherName.replace("'", "''") + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            publisherId = cursor.getInt(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_ID));
        }
        cursor.close();
        db.close();

        return publisherId;
    }

    // get Publisher Name by ID
    public String getPublisherNameById(int publisherId) {
        String selectQuery = "SELECT " + Publisher.COLUMN_PUBLISHER_NAME + " FROM " + Publisher.TABLE_NAME + " WHERE " + Publisher.COLUMN_PUBLISHER_ID + " = " + publisherId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String publisherName = "";
        if (cursor.moveToFirst()) {
            publisherName = cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME));
        }
        cursor.close();
        db.close();

        return publisherName;
    }

    // recuperamos los editores en una List<String> para usar en el AutoCompleteTextView
    public List<String> getListPublishers() {
        List<String> publisherList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Publisher.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                publisherList.add(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return publisherList;
    }

    // get publisherId by shelvesId
    public List<Integer> getPublisherIdByShelvesId(int shelfId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_PUBLISHER + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELVES + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // get publisherId by AuthorId
    public List<Integer> getPublisherIdByAuthorId(int authorId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_PUBLISHER + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_AUTHOR + " = " + authorId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // get publisherId by shelfId
    public List<Integer> getPublisherIdByShelfId(int shelfId) {
        List<Integer> authorList = new ArrayList<>();
        String selectQuery = "SELECT " + Book.COLUMN_BOOK_PUBLISHER + " FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELF + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                authorList.add(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_BOOK_PUBLISHER)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return authorList;
    }

    // Publisher con mas libros
    public List<Publisher> getPublisherMostBooks() {
        List<Publisher> publisherList = new ArrayList<>();
        //String selectQuery = "select * from Publishers where id_publisher in (select id_publisher from Books group by id_publisher order by count(*) desc limit 3);";
        String selectQuery = "select * from Publishers left join Books on Publishers.id_publisher = Books.id_publisher group by Books.id_publisher order by count(*) desc limit 3";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Publisher publisher = new Publisher();
                publisher.setId_publisher(cursor.getInt(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_ID)));
                publisher.setPublisher_name(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                publisher.setTimestamp(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_TIMESTAMP)));

                publisherList.add(publisher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return publisherList;
    }

    // Publisher con mas libros leidos
    public List<Publisher> getPublisherMostReaded() {
        List<Publisher> publisherList = new ArrayList<>();
        //String selectQuery = "select * from Publishers where id_publisher in (select id_publisher from Books where book_read_status = 2 group by id_publisher order by count(*) desc limit 3)";
        String selectQuery = "select * from Publishers left join Books on Publishers.id_publisher = Books.id_publisher where book_read_status = 2 group by Books.id_publisher order by count(*) desc limit 3";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Publisher publisher = new Publisher();
                publisher.setId_publisher(cursor.getInt(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_ID)));
                publisher.setPublisher_name(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_NAME)));
                publisher.setTimestamp(cursor.getString(cursor.getColumnIndex(Publisher.COLUMN_PUBLISHER_TIMESTAMP)));

                publisherList.add(publisher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return publisherList;
    }

    /*
        DATOS SHELVES
     */

    // recuperamos todas las estanterias List<Shelves>
    public List<Shelves> getAllShelvesTimestamp() {
        List<Shelves> shelvesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Shelves.TABLE_NAME + " ORDER BY " + Shelves.COLUMN_SHELVES_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shelves shelves = new Shelves();
                shelves.setId_shelves(cursor.getInt(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_ID)));
                shelves.setShelves_name(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                shelves.setTimestamp(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_TIMESTAMP)));

                shelvesList.add(shelves);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelvesList;
    }

    // recuperamos todas las estanterias List<Shelves>
    public List<Shelves> getAllShelvesAZ() {
        List<Shelves> shelvesList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Shelves.TABLE_NAME + " ORDER BY " + Shelves.COLUMN_SHELVES_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shelves shelves = new Shelves();
                shelves.setId_shelves(cursor.getInt(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_ID)));
                shelves.setShelves_name(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                shelves.setTimestamp(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_TIMESTAMP)));

                shelvesList.add(shelves);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelvesList;
    }

    // recuperamos todas las estanterias List<Shelves> con mas libros
    public List<Shelves> getAllShelvesNBooks() {
        List<Shelves> shelvesList = new ArrayList<>();

        //String selectQuery = "select * from Shelves inner join Books on Shelves.id_shelves = Books.id_shelves group by Books.id_shelves order by count(*) desc, Shelves.shelves_name asc";
        String selectQuery = "SELECT * FROM " + Shelves.TABLE_NAME + " INNER JOIN " + Book.TABLE_NAME
                + " ON " + Shelves.TABLE_NAME + "." + Shelves.COLUMN_SHELVES_ID + " = " + Book.TABLE_NAME + "." + Book.COLUMN_BOOK_SHELVES
                + " GROUP BY " + Book.TABLE_NAME + "." + Book.COLUMN_BOOK_SHELVES + " ORDER BY COUNT(*) DESC, " + Shelves.TABLE_NAME + "." + Shelves.COLUMN_SHELVES_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shelves shelves = new Shelves();
                shelves.setId_shelves(cursor.getInt(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_ID)));
                shelves.setShelves_name(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
                shelves.setTimestamp(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_TIMESTAMP)));

                shelvesList.add(shelves);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelvesList;
    }

    // get Shelves ID by Name
    public int getShelvesIdByName(String shelvesName) {
        int shelvesId = 0;
        String selectQuery = "SELECT " + Shelves.COLUMN_SHELVES_ID + " FROM " + Shelves.TABLE_NAME + " WHERE " + Shelves.COLUMN_SHELVES_NAME
                + " LIKE " + "'" + shelvesName.replace("'", "''") + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            shelvesId = cursor.getInt(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_ID));
        }
        cursor.close();
        db.close();

        return shelvesId;
    }

    // get Shelves name by Id
    public String getShelvesNameById(int shelvesId) {
        String selectQuery = "SELECT " + Shelves.COLUMN_SHELVES_NAME + " FROM " + Shelves.TABLE_NAME + " WHERE " + Shelves.COLUMN_SHELVES_ID + " = " + shelvesId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String shelvesName = "";
        if (cursor.moveToFirst()) {
            shelvesName = cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME));
        }
        cursor.close();
        db.close();

        return shelvesName;
    }

    // recuperamos las estanterias en una List<String> para usar en el AutoCompleteTextView
    public List<String> getListShelves() {
        List<String> shelvesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Shelves.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                shelvesList.add(cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelvesList;
    }

    //
    public Shelves getLastShelves() {
        String selectQuery = "SELECT * FROM " + Shelves.TABLE_NAME + " ORDER BY " + Shelves.COLUMN_SHELVES_TIMESTAMP + " LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Shelves shelves = null;

        if (cursor != null) {
            cursor.moveToFirst();

            shelves = new Shelves(
                    cursor.getInt(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_ID)),
                    cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_NAME)),
                    cursor.getString(cursor.getColumnIndex(Shelves.COLUMN_SHELVES_TIMESTAMP))
            );
            cursor.close();
        }
        db.close();

        return shelves;
    }

    /*
        DATOS SHELF
     */

    // get Shelf ID by Name and Shelves ID
    public int getShelfIdByNameAndShelves(String shelfName, int shelvesId) {
        int shelfId = 0;
        String selectQuery = "SELECT " + Shelf.COLUMN_SHELF_ID + " FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUMN_SHELF_NAME
                + " LIKE " + "'" + shelfName.replace("'", "''") + "'"
                + " AND " + Shelf.COLUM_SHELF_ID_SHELVES + " = " + shelvesId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            shelfId = cursor.getInt(cursor.getColumnIndex(Shelf.COLUMN_SHELF_ID));
        }
        cursor.close();
        db.close();

        return shelfId;
    }

    // get Shelf name by Id
    public String getShelfNameById(int shelfId) {
        String selectQuery = "SELECT " + Shelf.COLUMN_SHELF_NAME + " FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUMN_SHELF_ID + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String shelfName = "";
        if (cursor.moveToFirst()) {
            shelfName = cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME));
        }
        cursor.close();
        db.close();

        return shelfName;
    }

    // recuperamos Shelf en una List<String> para usar en el AutoCompleteTextView
    public List<String> getListShelf(int shelvesId) {
        List<String> shelfList = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + Shelf.COLUMN_SHELF_NAME + " FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUM_SHELF_ID_SHELVES + " = " + shelvesId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                shelfList.add(cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelfList;
    }

    // shelf en shelves por ID de shelves
    public List<Shelf> getShelfInShelvesById(int shelvesId) {
        List<Shelf> shelfList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUM_SHELF_ID_SHELVES + " = " + shelvesId + " ORDER BY " + Shelf.COLUMN_SHELF_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = new Shelf(
                        cursor.getInt(cursor.getColumnIndex(Shelf.COLUMN_SHELF_ID)),
                        cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)),
                        cursor.getInt(cursor.getColumnIndex(Shelf.COLUM_SHELF_ID_SHELVES)),
                        cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_TIMESTAMP))
                );
                shelfList.add(shelf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelfList;
    }

    // shelves id by shelfid
    public int getShelvesByShelfId(int shelfId) {
        int shelvesId = 0;
        String selectQuery = "SELECT " + Shelf.COLUM_SHELF_ID_SHELVES + " FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUMN_SHELF_ID + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            shelvesId = cursor.getInt(cursor.getColumnIndex(Shelf.COLUM_SHELF_ID_SHELVES));
        }
        cursor.close();
        db.close();

        return shelvesId;
    }

    /*
        COUNTS
     */

    // total libros por ID de autor
    public int getCountBooksFromAuthorById(int authorId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_AUTHOR + " = " + authorId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // total libros por ID de editorial
    public int getCountBooksFromPublisherById(int publisherId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_PUBLISHER + " = " + publisherId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // total autores por ID de editorial
    public int getCountAuthorPublisherId(int publisherId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_PUBLISHER + " = " + publisherId + " GROUP BY " + Book.COLUMN_BOOK_AUTHOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    //
    public int getCountPublishersByAuthorId(int authorId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_AUTHOR + " = " + authorId + " GROUP BY " + Book.COLUMN_BOOK_PUBLISHER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // total libros en shelves por ID
    public int getCountBooksFromShelvesById(int shelvesId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELVES + " = " + shelvesId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // libros en estante por ID
    public int getCountBooksFromShelfById(int shelfId) {
        String countQuery = "SELECT * FROM " + Book.TABLE_NAME + " WHERE " + Book.COLUMN_BOOK_SHELF + " = " + shelfId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // estantes en estanteria por nombre except
    public List<Shelf> getShelfInShelvesByIdExcept(int shelvesId, int shelfId) {
        List<Shelf> shelfList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Shelf.TABLE_NAME + " WHERE " + Shelf.COLUM_SHELF_ID_SHELVES + " = " + shelvesId + " AND " + Shelf.COLUMN_SHELF_ID + " != " + shelfId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Shelf shelf = new Shelf(
                        cursor.getInt(cursor.getColumnIndex(Shelf.COLUMN_SHELF_ID)),
                        cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_NAME)),
                        cursor.getInt(cursor.getColumnIndex(Shelf.COLUM_SHELF_ID_SHELVES)),
                        cursor.getString(cursor.getColumnIndex(Shelf.COLUMN_SHELF_TIMESTAMP))
                );
                shelfList.add(shelf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return shelfList;
    }

    /*
        WISHLIST
     */

    // insertar Wishlist
    public long insertWishlist(String title, String author, String publisher, String isbn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Wishlist.COLUMN_WISHLIST_TITLE, title);
        values.put(Wishlist.COLUMN_WISHLIST_AUTHOR, author);
        values.put(Wishlist.COLUMN_WISHLIST_PUBLISHER, publisher);
        values.put(Wishlist.COLUMN_WISHLIST_ISBN, isbn);

        long id = db.insert(Wishlist.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // recuperamos informacion de un libro de wishlist segun el ID
    public Wishlist getWishlist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Wishlist.TABLE_NAME + " WHERE " + Wishlist.COLUMN_WISHLIST_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        Wishlist wishlist = null;

        if (cursor != null) {
            cursor.moveToFirst();

            wishlist = new Wishlist(
                    cursor.getInt(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_ID)),
                    cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_TITLE)),
                    cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_PUBLISHER)),
                    cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_ISBN)),
                    cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_TIMESTAMP))
            );
            cursor.close();
        }
        db.close();

        return wishlist;
    }

    //
    public List<Wishlist> getAllWishlist() {
        List<Wishlist> wishlistList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Wishlist.TABLE_NAME + " ORDER BY " + Wishlist.COLUMN_WISHLIST_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                Wishlist wishlist = new Wishlist(
                        cursor.getInt(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_ID)),
                        cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_AUTHOR)),
                        cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_PUBLISHER)),
                        cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_ISBN)),
                        cursor.getString(cursor.getColumnIndex(Wishlist.COLUMN_WISHLIST_TIMESTAMP))
                );
                wishlistList.add(wishlist);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return wishlistList;
    }

    // borrar libro segun su id
    public void deleteWishlist(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Wishlist.TABLE_NAME, Wishlist.COLUMN_WISHLIST_ID + " = " + id, null);
        db.close();
    }

    // actualizar wishlist
    public void updateWIshlist(int wishlistId, String title, String author, String publisher, String isbn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Wishlist.COLUMN_WISHLIST_TITLE, title);
        cv.put(Wishlist.COLUMN_WISHLIST_AUTHOR, author);
        cv.put(Wishlist.COLUMN_WISHLIST_PUBLISHER, publisher);
        cv.put(Wishlist.COLUMN_WISHLIST_ISBN, isbn);
        db.update(Wishlist.TABLE_NAME, cv, Wishlist.COLUMN_WISHLIST_ID + " = " + wishlistId, null);
        db.close();
    }

}
