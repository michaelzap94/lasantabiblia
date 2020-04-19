package com.michaelzap94.santabiblia.utilities;

import android.content.Context;


import com.michaelzap94.santabiblia.objects.Book;

import java.util.ArrayList;


public class BookHelper {

//    // static variable single_instance of type Singleton
//    private static BookHelper single_instance = null;
//
//    private static ArrayList<Book> listAT = null;
//    private static ArrayList<Book> listNT = null;
//
//    public static ArrayList<Book> getLibrosAT() {
//        return getListAT();
//    }
//    public static ArrayList<Book> getLibrosNT() {
//        return getListNT();
//    }
//
//    // static method to create instance of Singleton class
//    public static BookHelper getInstance() {
//        if (single_instance == null){
//            single_instance = new BookHelper();
//        }
//        return single_instance;
//    }
//
//    // static method to create instance of Singleton class
//    public static void initBooks(Context ct) {
//        getListOTDB();
//        getLibrosNT();
//    }
//
//    public static Book getBook(int id) {
//        ArrayList<Book> books;
//        if (id < 40) {
//            books = getListAT();
//            if (books != null) {
//                return (Book) books.get(id - 1);
//            }
//        }
//        books = getListNT();
//        if (books != null) {
//            return (Book) books.get(id - 40);
//        }
//        return null;
//    }
//
//    public static String getTitleLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
//        Book nlibro = getBook(libro);
//        if (nlibro == null || versiculoi == 0) {
//            return BuildConfig.FLAVOR;
//        }
//        return nlibro.getName() + " " + capitulo + ":" + versiculoi + (versiculoi < versiculof ? "-" + versiculof : BuildConfig.FLAVOR);
//    }
//
//    public static String getUrlLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
//        Book nlibro = getBook(libro);
//        if (nlibro == null || versiculoi == 0) {
//            return BuildConfig.FLAVOR;
//        }
//        return "http://tusversiculos.com/v/" + nlibro.getId() + "/" + capitulo + "/" + versiculoi + "/" + (versiculoi < versiculof ? versiculof + "/" : BuildConfig.FLAVOR);
//    }
//
//    private static ArrayList<Book> getListOTDB() {
//        if (listAT == null) {
//            listAT = new ArrayList();
//            // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
//            //BibleDBHelper handler = new BibleDBHelper(context, BibleDBHelper.DB_NAME_BIBLE_CONTENT);
//            //SQLiteDatabase db = handler.getReadableDatabase();
//            //versesCursor = db.rawQuery("SELECT  verse AS _id, chapter, book_number, text FROM verses WHERE book_number = ? AND chapter = ?", new String[] {String.valueOf(book_number), String.valueOf(tab_number)});
//
//        }
//        return listAT;
//    }
//    private static ArrayList<Book> getListNTDB() {
//        if (listNT == null) {
//            listNT = new ArrayList();
//
//        }
//        return listNT;
//    }
//
//
//    private static ArrayList<Book> getListAT() {
//        if (listAT == null) {
//            listAT = new ArrayList();
//            listAT.add(new Book(1, "G\u00e9nesis", 50));
//            listAT.add(new Book(2, "\u00c9xodo", 40));
//            listAT.add(new Book(3, "Lev\u00edtico", 27));
//            listAT.add(new Book(4, "N\u00fameros", 36));
//            listAT.add(new Book(5, "Deuteronomio", 34));
//            listAT.add(new Book(6, "Josu\u00e9", 24));
//            listAT.add(new Book(7, "Jueces", 21));
//            listAT.add(new Book(8, "Rut", 4));
//            listAT.add(new Book(9, "1 Samuel", 31));
//            listAT.add(new Book(10, "2 Samuel", 24));
//            listAT.add(new Book(11, "1 Reyes", 22));
//            listAT.add(new Book(12, "2 Reyes", 25));
//            listAT.add(new Book(13, "1 Cr\u00f3nicas", 29));
//            listAT.add(new Book(14, "2 Cr\u00f3nicas", 36));
//            listAT.add(new Book(15, "Esdras", 10));
//            listAT.add(new Book(16, "Nehem\u00edas", 13));
//            listAT.add(new Book(17, "Ester", 10));
//            listAT.add(new Book(18, "Job", 42));
//            listAT.add(new Book(19, "Salmos", 150));
//            listAT.add(new Book(20, "Proverbios", 31));
//            listAT.add(new Book(21, "Eclesiast\u00e9s", 12));
//            listAT.add(new Book(22, "Cantares", 8));
//            listAT.add(new Book(23, "Isa\u00edas", 66));
//            listAT.add(new Book(24, "Jerem\u00edas", 52));
//            listAT.add(new Book(25, "Lamentaciones", 5));
//            listAT.add(new Book(26, "Ezequiel", 48));
//            listAT.add(new Book(27, "Daniel", 12));
//            listAT.add(new Book(28, "Oseas", 14));
//            listAT.add(new Book(29, "Joel", 3));
//            listAT.add(new Book(30, "Am\u00f3s", 9));
//            listAT.add(new Book(31, "Abd\u00edas", 1));
//            listAT.add(new Book(32, "Jon\u00e1s", 4));
//            listAT.add(new Book(33, "Miqueas", 7));
//            listAT.add(new Book(34, "Nah\u00fam", 3));
//            listAT.add(new Book(35, "Habacuc", 3));
//            listAT.add(new Book(36, "Sofon\u00edas", 3));
//            listAT.add(new Book(37, "Hageo", 2));
//            listAT.add(new Book(38, "Zacar\u00edas", 14));
//            listAT.add(new Book(39, "Malaqu\u00edas", 4));
//        }
//        return listAT;
//    }
//
//    private static ArrayList<Book> getListNT() {
//        if (listNT == null) {
//            listNT = new ArrayList();
//            listNT.add(new Book(40, "Mateo", 28));
//            listNT.add(new Book(41, "Marcos", 16));
//            listNT.add(new Book(42, "Lucas", 24));
//            listNT.add(new Book(43, "Juan", 21));
//            listNT.add(new Book(44, "Hechos", 28));
//            listNT.add(new Book(45, "Romanos", 16));
//            listNT.add(new Book(46, "1 Corintios", 16));
//            listNT.add(new Book(47, "2 Corintios", 13));
//            listNT.add(new Book(48, "G\u00e1latas", 6));
//            listNT.add(new Book(49, "Efesios", 6));
//            listNT.add(new Book(50, "Filipenses", 4));
//            listNT.add(new Book(51, "Colosenses", 4));
//            listNT.add(new Book(52, "1 Tesalonicenses", 5));
//            listNT.add(new Book(53, "2 Tesalonicenses", 3));
//            listNT.add(new Book(54, "1 Timoteo", 6));
//            listNT.add(new Book(55, "2 Timoteo", 4));
//            listNT.add(new Book(56, "Tito", 3));
//            listNT.add(new Book(57, "Filem\u00f3n", 1));
//            listNT.add(new Book(58, "Hebreos", 13));
//            listNT.add(new Book(59, "Santiago", 5));
//            listNT.add(new Book(60, "1 Pedro", 5));
//            listNT.add(new Book(61, "2 Pedro", 3));
//            listNT.add(new Book(62, "1 Juan", 5));
//            listNT.add(new Book(63, "2 Juan", 1));
//            listNT.add(new Book(64, "3 Juan", 1));
//            listNT.add(new Book(65, "Judas", 1));
//            listNT.add(new Book(66, "Apocalipsis", 22));
//        }
//        return listNT;
//    }
}
