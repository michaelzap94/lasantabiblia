package com.michaelzap94.santabiblia.utilities;

import android.content.Context;


import com.michaelzap94.santabiblia.BuildConfig;
import com.michaelzap94.santabiblia.models.Book;

import java.util.ArrayList;


public class BookHelper {

    // static variable single_instance of type Singleton
    private static BookHelper single_instance = null;

    private static ArrayList<Book> listAT = null;
    private static ArrayList<Book> listNT = null;

    public static ArrayList<Book> getLibrosAT() {
        return getListAT();
    }
    public static ArrayList<Book> getLibrosNT() {
        return getListNT();
    }

    // static method to create instance of Singleton class
    public static BookHelper getInstance() {
        if (single_instance == null){
            single_instance = new BookHelper();
        }
        return single_instance;
    }

    // static method to create instance of Singleton class
    public static void initBooks(Context ct) {
        getListOTDB();
        getLibrosNT();
    }

    public static Book getBook(int id) {
        ArrayList<Book> books;
        if (id < 40) {
            books = getListAT();
            if (books != null) {
                return (Book) books.get(id - 1);
            }
        }
        books = getListNT();
        if (books != null) {
            return (Book) books.get(id - 40);
        }
        return null;
    }

    public static String getTitleLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
        Book nlibro = getBook(libro);
        if (nlibro == null || versiculoi == 0) {
            return BuildConfig.FLAVOR;
        }
        return nlibro.getName() + " " + capitulo + ":" + versiculoi + (versiculoi < versiculof ? "-" + versiculof : BuildConfig.FLAVOR);
    }

    public static String getUrlLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
        Book nlibro = getBook(libro);
        if (nlibro == null || versiculoi == 0) {
            return BuildConfig.FLAVOR;
        }
        return "http://tusversiculos.com/v/" + nlibro.getId() + "/" + capitulo + "/" + versiculoi + "/" + (versiculoi < versiculof ? versiculof + "/" : BuildConfig.FLAVOR);
    }

    private static ArrayList<Book> getListOTDB() {
        if (listAT == null) {
            listAT = new ArrayList();
            // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
            //BibleDBHelper handler = new BibleDBHelper(context, BibleDBHelper.DB_NAME_BIBLE_CONTENT);
            //SQLiteDatabase db = handler.getReadableDatabase();
            //versesCursor = db.rawQuery("SELECT  verse AS _id, chapter, book_number, text FROM verses WHERE book_number = ? AND chapter = ?", new String[] {String.valueOf(book_number), String.valueOf(tab_number)});

        }
        return listAT;
    }
    private static ArrayList<Book> getListNTDB() {
        if (listNT == null) {
            listNT = new ArrayList();
        }
        return listNT;
    }


    private static ArrayList<Book> getListAT() {
        if (listAT == null) {
            listAT = new ArrayList();
            listAT.add(new Book(1, 10,"G\u00e9nesis", 50));
            listAT.add(new Book(2, 20, "\u00c9xodo", 40));
            listAT.add(new Book(3, 30, "Lev\u00edtico", 27));
            listAT.add(new Book(4, 40,"N\u00fameros", 36));
            listAT.add(new Book(5, 50,"Deuteronomio", 34));
            listAT.add(new Book(6, 60,"Josu\u00e9", 24));
            listAT.add(new Book(7,70 ,"Jueces", 21));
            listAT.add(new Book(8, 80,"Rut", 4));
            listAT.add(new Book(9,90 ,"1 Samuel", 31));
            listAT.add(new Book(10,100 ,"2 Samuel", 24));
            listAT.add(new Book(11, 110,"1 Reyes", 22));
            listAT.add(new Book(12,120 ,"2 Reyes", 25));
            listAT.add(new Book(13, 130,"1 Cr\u00f3nicas", 29));
            listAT.add(new Book(14, 140,"2 Cr\u00f3nicas", 36));
            listAT.add(new Book(15, 150,"Esdras", 10));
            listAT.add(new Book(16, 160,"Nehem\u00edas", 13));
            listAT.add(new Book(17, 190,"Ester", 10));
            listAT.add(new Book(18, 220,"Job", 42));
            listAT.add(new Book(19, 230,"Salmos", 150));
            listAT.add(new Book(20, 240,"Proverbios", 31));
            listAT.add(new Book(21, 250,"Eclesiast\u00e9s", 12));
            listAT.add(new Book(22, 260,"Cantares", 8));
            listAT.add(new Book(23, 290,"Isa\u00edas", 66));
            listAT.add(new Book(24, 300,"Jerem\u00edas", 52));
            listAT.add(new Book(25, 310,"Lamentaciones", 5));
            listAT.add(new Book(26, 330,"Ezequiel", 48));
            listAT.add(new Book(27, 340,"Daniel", 12));
            listAT.add(new Book(28, 350,"Oseas", 14));
            listAT.add(new Book(29, 360,"Joel", 3));
            listAT.add(new Book(30,370 ,"Am\u00f3s", 9));
            listAT.add(new Book(31, 380,"Abd\u00edas", 1));
            listAT.add(new Book(32, 390,"Jon\u00e1s", 4));
            listAT.add(new Book(33, 400,"Miqueas", 7));
            listAT.add(new Book(34,410 ,"Nah\u00fam", 3));
            listAT.add(new Book(35, 420,"Habacuc", 3));
            listAT.add(new Book(36, 430,"Sofon\u00edas", 3));
            listAT.add(new Book(37,440 ,"Hageo", 2));
            listAT.add(new Book(38,450 ,"Zacar\u00edas", 14));
            listAT.add(new Book(39, 460,"Malaqu\u00edas", 4));
        }
        return listAT;
    }

    private static ArrayList<Book> getListNT() {
        if (listNT == null) {
            listNT = new ArrayList();
            listNT.add(new Book(40, 470,"Mateo", 28));
            listNT.add(new Book(41, 480,"Marcos", 16));
            listNT.add(new Book(42,490 ,"Lucas", 24));
            listNT.add(new Book(43,500 ,"Juan", 21));
            listNT.add(new Book(44, 510,"Hechos", 28));
            listNT.add(new Book(45, 520,"Romanos", 16));
            listNT.add(new Book(46, 530,"1 Corintios", 16));
            listNT.add(new Book(47, 540,"2 Corintios", 13));
            listNT.add(new Book(48, 550,"G\u00e1latas", 6));
            listNT.add(new Book(49, 560,"Efesios", 6));
            listNT.add(new Book(50,570 ,"Filipenses", 4));
            listNT.add(new Book(51,580 ,"Colosenses", 4));
            listNT.add(new Book(52, 590,"1 Tesalonicenses", 5));
            listNT.add(new Book(53,600 ,"2 Tesalonicenses", 3));
            listNT.add(new Book(54, 610,"1 Timoteo", 6));
            listNT.add(new Book(55, 620,"2 Timoteo", 4));
            listNT.add(new Book(56, 630,"Tito", 3));
            listNT.add(new Book(57, 640,"Filem\u00f3n", 1));
            listNT.add(new Book(58, 650,"Hebreos", 13));
            listNT.add(new Book(59, 660,"Santiago", 5));
            listNT.add(new Book(60, 670,"1 Pedro", 5));
            listNT.add(new Book(61, 680,"2 Pedro", 3));
            listNT.add(new Book(62, 690,"1 Juan", 5));
            listNT.add(new Book(63, 700,"2 Juan", 1));
            listNT.add(new Book(64, 710,"3 Juan", 1));
            listNT.add(new Book(65, 720,"Judas", 1));
            listNT.add(new Book(66, 730,"Apocalipsis", 22));
        }
        return listNT;
    }
}
