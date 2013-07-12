package com.exsplit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author: Joydeep Paul
 * @description: Simple Expense Splitting database access helper class. Defines basic CRUD operations 
 * for the Expense Splitting application
 * @version: 1.0
 */
public class ExSplitDbAdapter {
	private static final String TAG = "ExSplitDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/* fields for user table */
	public static final String USER_ID    = "_id";
	public static final String USER_FNAME = "first_name";
	public static final String USER_LNAME = "last_name";
	public static final String USER_EMAIL = "email";
	
	/* fields for category table */
	public static final String CAT_ID   = "_id";
	public static final String CAT_NAME = "category_name";
	
	/* fields for expense item table */
	public static final String EXP_ID      = "_id";
	public static final String EXP_USER_ID = "user_id";
	public static final String EXP_CAT_ID  = "cat_id";
	public static final String EXP_DATE    = "exp_date";
	public static final String EXP_AMOUNT  = "exp_amount";
	
	/*
	 * Database creation SQL statement
	 */
	public static final String USER_TAB_CREATE = 
			"create table users (_id integer primary key autoincrement," + 
		    "first_name text, last_name text, email text);";
	public static final String CATEG_TAB_CREATE = 
			"create table category (_id integer primary key autoincrement," 
			+ "category_name varchar(20));";
	public static final String EXPI_TAB_CREATE = 
			"create table expenses (_id integer primary key autoincrement,"
			+"user_id integer not null, cat_id integer not null, exp_date text, exp_amount float,"
			+"FOREIGN KEY(user_id) references users(_id) ON DELETE CASCADE,"
			+"FOREIGN KEY(cat_id) references category(_id) ON DELETE CASCADE);";

    private static final String DATABASE_NAME  = "exsplit";
    private static final String DB_TAB_USER    = "users";
    private static final String DB_TAB_CATEG   = "category";
    private static final String DB_TAB_EXP     = "expenses";
    private static final int DATABASE_VERSION  = 4;
	
	private final Context mCtx;
	
	private static final class DatabaseHelper extends SQLiteOpenHelper{
		
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
		@Override
		public void onCreate(SQLiteDatabase db) {			
			db.execSQL(USER_TAB_CREATE);
			db.execSQL(CATEG_TAB_CREATE);
			db.execSQL(EXPI_TAB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS category");
            db.execSQL("DROP TABLE IF EXISTS expenses");
            onCreate(db);			
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			if (!db.isReadOnly()) {
		        // Enable foreign key constraints
		        db.execSQL("PRAGMA foreign_keys=ON;");
		    }			
		}			
		
	}

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ExSplitDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the exsplit database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ExSplitDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb	= mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new user using first name, last name and email. If the user is
     * successfully created return the new rowID for that user, otherwise return
     * a -1 to indicate failure.
     * 
     * @param first_name: First name of the user
     * @param last_name: Last name of the user
     * @param email: Email id of the user
     */
    public long add_user(String first_name, String last_name, String email){
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(USER_FNAME, first_name);
    	initialValues.put(USER_LNAME, last_name);
    	initialValues.put(USER_EMAIL, email);
    	
    	return mDb.insert(DB_TAB_USER, null, initialValues);    	
    }

    /**
     * Create a new category using the category name. If the category is
     * successfully created return the new rowID for that category, otherwise return
     * a -1 to indicate failure.
     * 
     * @param category_name: category name
     */
    public long add_category(String category_name){
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(CAT_NAME,category_name);
    	
    	return mDb.insert(DB_TAB_CATEG, null, initialValues);
    }
   
    /**
     * Create a new expense item. If the expense item is
     * successfully created return the new rowID for that expense item,
     * otherwise return -1 to indicate failure.
     * 
     * @param user_id: User Id
     * @param cat_id: Category Id
     * @param amount: Expense Amount
     */    
    public long create_expense_item(int user_id, int cat_id, String date, float amount){
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(EXP_USER_ID, user_id);
    	initialValues.put(EXP_CAT_ID,cat_id);
    	initialValues.put(EXP_DATE, date);
    	initialValues.put(EXP_AMOUNT, amount);
    	
    	return mDb.insert(DB_TAB_EXP, null, initialValues);
    }
    
    /**
     * Delete the user with the given user id
     * 
     * @param user id of user to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteUser(long userId) {

        return mDb.delete(DB_TAB_USER, USER_ID + "=" + userId, null) > 0;
    }   
    
    /**
     * Delete a category with the given category id
     * 
     * @param category id of category to delete
     * @return true if deleted, false otherwise
     */    
    public boolean deleteCategory(long categId) {

        return mDb.delete(DB_TAB_CATEG, CAT_ID + "=" + categId, null) > 0;
    }    
    
    /**
     * Delete an expense item with the given expense id
     * 
     * @param expense id of an expense item to delete
     * @return true if deleted, false otherwise
     */     
    public boolean deleteExpensItem(long expId){
    	return mDb.delete(DB_TAB_EXP, EXP_ID + "=" + expId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all users in the database
     * @return Cursor of all users
     */
    public Cursor fetchAllUsers(){
        return mDb.query(DB_TAB_USER, new String[] {USER_ID, USER_FNAME,
                USER_LNAME, USER_EMAIL}, null, null, null, null, null);    	
    }
    
    /**
     * Return a Cursor over the list of all categories in the database
     * @return Cursor of all categories
     */    
    public Cursor fetchAllCategory(){
        return mDb.query(DB_TAB_CATEG, new String[] {CAT_ID, CAT_NAME},
        		null, null, null, null, null); 
    }
    
    /**
     * Return a Cursor over the list of all expense items in the database
     * @return Cursor of all expense items
     */    
    public Cursor fetchAllExpenses(){
        return mDb.query(DB_TAB_EXP, new String[] {EXP_ID, EXP_USER_ID, 
        		EXP_CAT_ID, EXP_DATE, EXP_AMOUNT}, null, null, null, null, null); 
    }    
    
    /**
     * Return a Cursor positioned at the user that matches the given userId
     * 
     * @param userId id of user to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchUser(long userId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TAB_USER, new String[] {USER_ID, USER_FNAME,
                    USER_LNAME, USER_EMAIL}, USER_ID + "=" + userId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    } 
    
    /**
     * Return a Cursor positioned at the category that matches the given categId
     * 
     * @param categId id of user to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchCateg(long categId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TAB_CATEG, new String[] {CAT_ID,
                    CAT_NAME}, CAT_ID + "=" + categId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }    
    
    /**
     * Return a Cursor positioned at the expense that matches the given expenseId
     * 
     * @param categId id of user to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchExpense(long expenseId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DB_TAB_EXP, new String[] {EXP_ID, EXP_USER_ID,
                    EXP_CAT_ID, EXP_DATE, EXP_AMOUNT}, EXP_ID + "=" + expenseId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }    
    
    /**
     * Update the user using the details provided. The user to be updated is
     * specified using the userId, and it is altered to use the first name, 
     * last name and email values passed in
     * 
     * @param userId id of user to update
     * @param first_name value to set user first name to
     * @param last_name value to set user last name to
     * @param email value to set user email to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateUser(long userId, String first_name, String last_name, String email) {
        ContentValues args = new ContentValues();
        args.put(USER_FNAME, first_name);
        args.put(USER_LNAME, last_name);
        args.put(USER_EMAIL, email);

        return mDb.update(DB_TAB_USER, args, USER_ID + "=" + userId, null) > 0;
    }
    
    /**
     * Update the category using the details provided. The category to be updated is
     * specified using the categoryId, and it is altered to use the category name, 
     * value passed in
     * 
     * @param userId id of user to update
     * @param categ_name value to set user category name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCateg(long categId, String categ_name) {
        ContentValues args = new ContentValues();
        args.put(CAT_NAME, categ_name);

        return mDb.update(DB_TAB_CATEG, args, CAT_ID + "=" + categId, null) > 0;
    }   
    
    /**
     * Update the category using the details provided. The category to be updated is
     * specified using the categoryId, and it is altered to use the category name, 
     * value passed in
     * 
     * @param userId id of user to update
     * @param categ_name value to set user category name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateExpense(long expId, int userId, int catId, String date, float amount) {
        ContentValues args = new ContentValues();
        args.put(EXP_USER_ID, userId);
        args.put(EXP_CAT_ID, catId);
        args.put(EXP_DATE,date);
        args.put(EXP_AMOUNT,amount);

        return mDb.update(DB_TAB_EXP, args, EXP_ID + "=" + expId, null) > 0;
    }   
    
    
}
