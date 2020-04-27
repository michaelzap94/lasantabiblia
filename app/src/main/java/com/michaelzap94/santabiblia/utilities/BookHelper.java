package com.michaelzap94.santabiblia.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.michaelzap94.santabiblia.BuildConfig;
import com.michaelzap94.santabiblia.models.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BookHelper {
    private static final String TAG = "BookHelper";

    // static variable single_instance of type Singleton
    private static BookHelper single_instance = null;
    private static HashMap<Integer, Integer> indexMap = null;

    private static ArrayList<Book> listAT = null;
    private static ArrayList<Book> listNT = null;
    //private static ArrayList<Book> listAll = null;

//    public static ArrayList<Book> getAllBooks() {
//        return getListAll();
//    }

    public static ArrayList<Book> getbooksOT() {
        return getListAT();
    }
    public static ArrayList<Book> getbooksNT() {
        return getListNT();
    }

    // static method to create instance of Singleton class
    public static BookHelper getInstance() {
        if (single_instance == null){
            single_instance = new BookHelper();
        }
        return single_instance;
    }

    public static Book getBookByPositionId(int id) {

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

    public static Book getBook(int book_number) {

        ArrayList<Book> books;
        if (book_number < 470) {
            books = getListAT();
            if (books != null) {
                return (Book) books.get(getBookIndex(book_number)-1);
            }
        } else {
            books = getListNT();
            if (books != null) {
                return (Book) books.get(getBookIndex(book_number) - 40);
            }
        }
        return null;

//        ArrayList<Book> books = getListAll();
//        if (books != null) {
//            int indexOfBook = books.indexOf(book_number);
//            if(indexOfBook > -1) {
//                return (Book) books.get(indexOfBook);
//            }
//        }
    }

    //needed so book retrieval is O(1) rather than looping through elements to find book_number
    private static int getBookIndex(int book_number){
        if(indexMap == null) {
            indexMap = new HashMap<Integer, Integer>();
            indexMap.put(10,1);indexMap.put(20,2);indexMap.put(30,3);indexMap.put(40,4);indexMap.put(50,5);indexMap.put(60,6);
            indexMap.put(70,7);indexMap.put(80,8);indexMap.put(90,9);indexMap.put(100,10);indexMap.put(110,11);indexMap.put(120,12);
            indexMap.put(130,13);indexMap.put(140,14);indexMap.put(150,15);indexMap.put(160,16);indexMap.put(190,17);indexMap.put(220,18);
            indexMap.put(230,19);indexMap.put(240,20);indexMap.put(250,21);indexMap.put(260,22);indexMap.put(290,23);indexMap.put(300,24);
            indexMap.put(310,25);indexMap.put(330,26);indexMap.put(340,27);indexMap.put(350,28);indexMap.put(360,39);indexMap.put(370,30);indexMap.put(380,31);
            indexMap.put(390,32);indexMap.put(400,33);indexMap.put(410,34);indexMap.put(420,35);indexMap.put(430,36);indexMap.put(440,37);
            indexMap.put(450,38);indexMap.put(460,39);indexMap.put(470,40);indexMap.put(480,41);indexMap.put(490,42);indexMap.put(500,43);
            indexMap.put(510,44);indexMap.put(520,45);indexMap.put(530,46);indexMap.put(540,47);indexMap.put(550,48);indexMap.put(560,49);
            indexMap.put(570,50);indexMap.put(580,51);indexMap.put(590,52);indexMap.put(600,53);indexMap.put(610,54);indexMap.put(620,55);
            indexMap.put(630,56);indexMap.put(640,57);indexMap.put(650,58);indexMap.put(660,59);indexMap.put(670,60);indexMap.put(680,61);
            indexMap.put(690,62);indexMap.put(700,63);indexMap.put(710,64);indexMap.put(720,65);indexMap.put(730,66);
        }
        return indexMap.get(book_number);
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

    public static String getTitleBookAndCaps(String book, int chapter, List<Integer> selectedItems) {
        Log.d(TAG, "getTitleBookAndCaps: " + selectedItems);
        String verses = "";
        String versesFinal = "";
        if(selectedItems.size() == 1){
            verses = verses + (selectedItems.get(0) + 1);
        } else {
            List<List<Integer>> result = new ArrayList<List<Integer>>();
            List<Integer> currentList = null;
            for (int i = 0; i < selectedItems.size(); i++) {
                if(i == 0 || (selectedItems.get(i) != selectedItems.get(i-1)+1)) {
                    //if the current element is the first element or doesn't satisfy the condition
                    currentList = new ArrayList<Integer>(); //create a new list and set it to curr
                    result.add(currentList); //add the newly created list to the result list
                }
                currentList.add(selectedItems.get(i)); //add current element to the curr list
            }
            String[] resultArr = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                List<Integer> currentArr = result.get(i);
                int verseFrom = (currentArr.get(0)+1);
                int verseTo = (currentArr.get(currentArr.size()-1)+1);
                resultArr[i] = verseFrom + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
            }
            Log.d(TAG, "STRING: " + result);
            verses = TextUtils.join(", ", resultArr);

        }


        return book + " " + chapter + ":" + verses;//

    }

//    public static String getTitleBookAndCaps(String book, int chapter, List<Integer> selectedItems) {
//        Log.d(TAG, "getTitleBookAndCaps: " + selectedItems);
//        String verses = "";
//        String versesFinal = "";
//        if(selectedItems.size() == 1){
//            verses = verses + (selectedItems.get(0) + 1);
//        } else {
//            boolean needComma = false;
//            int i = 0;
//            int j;
//            int startVerse = 0;
//            int endVerse = 0;
//            int newStartVerse = 0;
//            int newEndVerse = 0;
//            for (j = 1; j < selectedItems.size(); j++) {
//                if (selectedItems.get(j) == selectedItems.get(j - 1) + 1) {
//                    startVerse = selectedItems.get(i) + 1;
//                    endVerse = selectedItems.get(j) + 1;
//                    if(!needComma){
//                        verses = startVerse + "-" + endVerse;
//                    } else {
//                        if(verses == ""){
//                            verses = startVerse + "-" + endVerse;
//                        } else {
//                            if(newStartVerse>0){
//                                verses = verses + "-" + endVerse;
//                                newStartVerse = 0;
//                            } else {
//                                verses = verses + ", " +  startVerse + "-" + endVerse;
//                            }
//                        }
//                    }
//                } else {
//                    needComma = true;
//                    i = j;
//                    newStartVerse = selectedItems.get(i) + 1;
////                    newEndVerse = selectedItems.get(j) + 1;
//                    if(verses == ""){
//                        verses = startVerse + "-" + endVerse;
//                    } else {
//                        verses = verses + ", " +  newStartVerse;
//                    }
//
//                }
//            }
//
//        }
//
//
//        return book + " " + chapter + ":" + verses;//verseFrom + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
//
//    }

//    public static String getTitleBookAndCaps(String book, int chapter, int verseFrom, int verseTo) {
////        Book selectedBook = getBook(book);
////        if (selectedBook == null || verseFrom == 0) {
////            return BuildConfig.FLAVOR;
////        }
////        return selectedBook.getName() + " " + chapter + ":" + verseFrom + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
//          return book + " " + chapter + ":" + verseFrom + (verseFrom < verseTo ? "-" + verseTo : BuildConfig.FLAVOR);
//
//    }
//
//    public static String getUrlLibCaps(int libro, int capitulo, int versiculoi, int versiculof) {
//        Book nlibro = getBook(libro);
//        if (nlibro == null || versiculoi == 0) {
//            return BuildConfig.FLAVOR;
//        }
//        return "http://tusversiculos.com/v/" + nlibro.getId() + "/" + capitulo + "/" + versiculoi + "/" + (versiculoi < versiculof ? versiculof + "/" : BuildConfig.FLAVOR);
//    }


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
//        }
//        return listNT;
//    }

    //    private static ArrayList<Book> getListAll() {
//        if (listAll == null) {
//            listAll = new ArrayList();
//            listAll.add(new Book(1, 10,"G\u00e9nesis", 50));
//            listAll.add(new Book(2, 20, "\u00c9xodo", 40));
//            listAll.add(new Book(3, 30, "Lev\u00edtico", 27));
//            listAll.add(new Book(4, 40,"N\u00fameros", 36));
//            listAll.add(new Book(5, 50,"Deuteronomio", 34));
//            listAll.add(new Book(6, 60,"Josu\u00e9", 24));
//            listAll.add(new Book(7,70 ,"Jueces", 21));
//            listAll.add(new Book(8, 80,"Rut", 4));
//            listAll.add(new Book(9,90 ,"1 Samuel", 31));
//            listAll.add(new Book(10,100 ,"2 Samuel", 24));
//            listAll.add(new Book(11, 110,"1 Reyes", 22));
//            listAll.add(new Book(12,120 ,"2 Reyes", 25));
//            listAll.add(new Book(13, 130,"1 Cr\u00f3nicas", 29));
//            listAll.add(new Book(14, 140,"2 Cr\u00f3nicas", 36));
//            listAll.add(new Book(15, 150,"Esdras", 10));
//            listAll.add(new Book(16, 160,"Nehem\u00edas", 13));
//            listAll.add(new Book(17, 190,"Ester", 10));
//            listAll.add(new Book(18, 220,"Job", 42));
//            listAll.add(new Book(19, 230,"Salmos", 150));
//            listAll.add(new Book(20, 240,"Proverbios", 31));
//            listAll.add(new Book(21, 250,"Eclesiast\u00e9s", 12));
//            listAll.add(new Book(22, 260,"Cantares", 8));
//            listAll.add(new Book(23, 290,"Isa\u00edas", 66));
//            listAll.add(new Book(24, 300,"Jerem\u00edas", 52));
//            listAll.add(new Book(25, 310,"Lamentaciones", 5));
//            listAll.add(new Book(26, 330,"Ezequiel", 48));
//            listAll.add(new Book(27, 340,"Daniel", 12));
//            listAll.add(new Book(28, 350,"Oseas", 14));
//            listAll.add(new Book(29, 360,"Joel", 3));
//            listAll.add(new Book(30,370 ,"Am\u00f3s", 9));
//            listAll.add(new Book(31, 380,"Abd\u00edas", 1));
//            listAll.add(new Book(32, 390,"Jon\u00e1s", 4));
//            listAll.add(new Book(33, 400,"Miqueas", 7));
//            listAll.add(new Book(34,410 ,"Nah\u00fam", 3));
//            listAll.add(new Book(35, 420,"Habacuc", 3));
//            listAll.add(new Book(36, 430,"Sofon\u00edas", 3));
//            listAll.add(new Book(37,440 ,"Hageo", 2));
//            listAll.add(new Book(38,450 ,"Zacar\u00edas", 14));
//            listAll.add(new Book(39, 460,"Malaqu\u00edas", 4));
//            listAll.add(new Book(40, 470,"Mateo", 28));
//            listAll.add(new Book(41, 480,"Marcos", 16));
//            listAll.add(new Book(42,490 ,"Lucas", 24));
//            listAll.add(new Book(43,500 ,"Juan", 21));
//            listAll.add(new Book(44, 510,"Hechos", 28));
//            listAll.add(new Book(45, 520,"Romanos", 16));
//            listAll.add(new Book(46, 530,"1 Corintios", 16));
//            listAll.add(new Book(47, 540,"2 Corintios", 13));
//            listAll.add(new Book(48, 550,"G\u00e1latas", 6));
//            listAll.add(new Book(49, 560,"Efesios", 6));
//            listAll.add(new Book(50,570 ,"Filipenses", 4));
//            listAll.add(new Book(51,580 ,"Colosenses", 4));
//            listAll.add(new Book(52, 590,"1 Tesalonicenses", 5));
//            listAll.add(new Book(53,600 ,"2 Tesalonicenses", 3));
//            listAll.add(new Book(54, 610,"1 Timoteo", 6));
//            listAll.add(new Book(55, 620,"2 Timoteo", 4));
//            listAll.add(new Book(56, 630,"Tito", 3));
//            listAll.add(new Book(57, 640,"Filem\u00f3n", 1));
//            listAll.add(new Book(58, 650,"Hebreos", 13));
//            listAll.add(new Book(59, 660,"Santiago", 5));
//            listAll.add(new Book(60, 670,"1 Pedro", 5));
//            listAll.add(new Book(61, 680,"2 Pedro", 3));
//            listAll.add(new Book(62, 690,"1 Juan", 5));
//            listAll.add(new Book(63, 700,"2 Juan", 1));
//            listAll.add(new Book(64, 710,"3 Juan", 1));
//            listAll.add(new Book(65, 720,"Judas", 1));
//            listAll.add(new Book(66, 730,"Apocalipsis", 22));
//        }
//        return listAll;
//    }
//

}
