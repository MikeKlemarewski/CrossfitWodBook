package com.mklem.android.crossfitwodbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WodSelectionActivity extends Activity{
	
	ListView            wodList;
	TextView            categoryText;
	String				category;
	Cursor              cursor;
	SQLiteDatabase      db;
	DBHelper            dbHelper;
	SimpleCursorAdapter adapter;
	
	int pageType;
	
	public static final int WOD_LIST = 1;
	public static final int PERSONAL_RECORD = 2;
	public static final int NEW = 2;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.wod_selection);
        
        wodList      = (ListView)findViewById(R.id.WodListView);
        categoryText = (TextView)findViewById(R.id.CategoryTextView);

        /*
         * Get category passed from intent
         */
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
        	category = extras.getString("category");
        	pageType = extras.getInt("pageType");
        	categoryText.setText(extras.getString("categoryDisplay"));
        }
        

        dbHelper = new DBHelper(this);
        db       = dbHelper.getWritableDatabase();

        
        /*
         * Passes WOD title to WOD viewer activity when list item clicked
         */
        wodList.setOnItemClickListener(new OnItemClickListener() {
        	Intent myIntent;

        	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
     			myIntent = new Intent(a.getContext(), WodViewerActivity.class);
    			myIntent.putExtra("id", cursor.getString(0));
    			myIntent.putExtra("title", cursor.getString(1));
    			startActivity(myIntent);
        	 }
        });
    }
    
    public void onStart(){
    	
    	super.onStart();
    	    	
    	/*
    	 * Query database for WODS of correct category and populate list view
    	 */
        cursor   = db.query(dbHelper.WOD_TABLE, new String[] {"_id", dbHelper.WOD_TITLE}, dbHelper.WOD_CATEGORY + " = " + "'" + category + "'", null, null, null, dbHelper.WOD_TITLE + " ASC");        
        
        String[] from = {dbHelper.WOD_TITLE};
        int[] to      = {R.id.WodTitle};
        
        adapter = new SimpleCursorAdapter(this, R.layout.wod_entry, cursor, from , to);
        wodList.setAdapter(adapter);
    }
    
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if(dbHelper!=null){
	    	dbHelper.close();
	    	cursor.close();
	    }
	}
	
	
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cursor.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		/*
		 * Set menu button text depending on what view user is in
		 */
		if(pageType == WOD_LIST){
			menu.add(0, R.id.addEntry, 0, "Add New WOD");
		}
		else{
			menu.add(0, R.id.addEntry, 0, "Add Personal Record");
		}
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.wod_list_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
	    switch (item.getItemId()) {
	    case R.id.addEntry:
			Intent myIntent = new Intent(this, EditWodActivity.class);
			myIntent.putExtra("category", category);
			myIntent.putExtra("saveType", NEW);
            startActivity(myIntent);
			return true;
	    default:
	    	return true;
	    }
	}
}
