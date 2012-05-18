package com.mklem.android.crossfitwodbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static String PATH = "/data/data/com.mklem.android.crossfitwodbook/databases/";
	private static final String DB_NAME = "wodbook_db";
	private static final int DB_VERSION = 1;
		
	public final String WOD_TABLE    = "wod_table";
	public final String SCORE_TABLE  = "wod_scores";
	public final String WOD_TITLE    = "wod_title";
	public final String WOD_DETAILS  = "wod_details";
	public final String WOD_CATEGORY = "wod_category";
	public final String WOD_SCORE    = "score";
	public final String WOD_DATE     = "date";
	public final String WOD_ID       = "wod_id";
	
	private final Context myContext;
	
	public DBHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		myContext = context;
	}
    // TODO: override the constructor and other methods for the parent class

	  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}
    	else{
 
    		//Create empty database in default system path
        	this.getReadableDatabase();
 
        	try {
    			copyDataBase();
    		}
        	catch (IOException e){
        		throw new Error("Error copying database");
        	}
    	}
    }
	
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
    
	/*
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + WOD_TABLE + " (_id integer not null primary key autoincrement, " + WOD_TITLE + " string unique, " +
					WOD_DETAILS + " string, " + WOD_CATEGORY + " string)";
		db.execSQL(sql);
	}
		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
		// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE
		// FROM OLDER VERSIONS.
	}
	*/
}

