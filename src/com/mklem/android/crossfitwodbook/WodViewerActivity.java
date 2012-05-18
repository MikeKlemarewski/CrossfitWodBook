package com.mklem.android.crossfitwodbook;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.EditText;

public class WodViewerActivity extends Activity implements OnClickListener{
	
	TextView            wodTitleTextView, wodDescriptionTextView, scoreTextView, dateTextView;
	EditText			dateEditText, scoreEditText;
	ListView            scoreList;
	String              wodTitle,wodId;
	DBHelper            dbHelper;
	SQLiteDatabase      db;
	Cursor              wodCursor, scoreCursor;
	SimpleCursorAdapter adapter;
	
	final int SCORE_DIALOG_ID       = 0;
	final int WOD_EDITOR            = 1;
	static final int DATE_DIALOG_ID = 2;
	
	Calendar   calendar;
	Date       date;
	String     displayDate;
	DatePicker datePicker;
	Button     okButton, cancelButton;
	Dialog     scoreDialog, dateDialog;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wod_page);
		
		wodTitleTextView       = (TextView)this.findViewById(R.id.WodTitleTextView);
		wodDescriptionTextView = (TextView)this.findViewById(R.id.DescriptionTextView);
		scoreList			   = (ListView)this.findViewById(R.id.ScoreListView);
		
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
        	wodTitle = extras.getString("title");
        	wodId    = extras.getString("id");
        }
        
        /*
         * Set up dbHelper
         */
        dbHelper = new DBHelper(this);
        db       = dbHelper.getWritableDatabase();
        
	}
	
	@Override
	public void onStart(){

		super.onStart();

		/*
		 * Query database for WOD details
		 */
		wodCursor   = db.query(dbHelper.WOD_TABLE, new String[] {"_id", dbHelper.WOD_DETAILS}, dbHelper.WOD_TITLE + " = " + "'" + wodTitle + "'", null, null, null, null);
		wodCursor.moveToFirst();
        
    	wodTitleTextView.setText(wodTitle);
        wodDescriptionTextView.setText(wodCursor.getString(1));
        
        String wodID = wodCursor.getString(0);
        /*
         * Query database for WOD Scores
         */
        scoreCursor   = db.query(dbHelper.SCORE_TABLE, new String[] {"_id", dbHelper.WOD_DATE, dbHelper.WOD_SCORE}, dbHelper.WOD_ID + " = " + wodID, null, null, null, null);        
        
        String[] from = {dbHelper.WOD_DATE, dbHelper.WOD_SCORE};
        int[] to      = {R.id.DateTextView, R.id.ScoreTextView};
        
        adapter = new SimpleCursorAdapter(this, R.layout.score_entry, scoreCursor, from , to);
        scoreList.setAdapter(adapter);
		
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if(dbHelper!=null){
	    	dbHelper.close();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.wod_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	    case R.id.editWod:
			Intent myIntent = new Intent(this, EditWodActivity.class);
			myIntent.putExtra("wodTitle", wodTitle);
			myIntent.putExtra("wodDescription", wodCursor.getString(1));
			myIntent.putExtra("saveType", WOD_EDITOR);
            startActivityForResult(myIntent, WOD_EDITOR);
			return true;
			
	    case R.id.deleteWod:
	    	db.delete(dbHelper.WOD_TABLE, dbHelper.WOD_TITLE + "=?", new String[] {wodTitle});
	    	finish();
	    	return true;
	    	
	    case R.id.addScore:
	    	showDialog(SCORE_DIALOG_ID);
	    	return true;
	    
	    default:
	    	return true;
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); 

		switch (requestCode) {
	    	case WOD_EDITOR:

	     		if (resultCode == RESULT_OK){
	     			Bundle extras = data.getExtras();
	     			if(extras!=null){
	     				wodTitle = extras.getString("title");
	            	}
	            } 
	    }
	}
	
	// Creating dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case SCORE_DIALOG_ID:
				scoreDialog = new Dialog(this);
				scoreDialog.setContentView(R.layout.enter_score);
				
				dateTextView  = (TextView)scoreDialog.findViewById(R.id.dateTextView);
				dateEditText  = (EditText)scoreDialog.findViewById(R.id.dateTextField);
				scoreTextView = (TextView)scoreDialog.findViewById(R.id.scoreTextView);
				scoreEditText  = (EditText)scoreDialog.findViewById(R.id.scoreTextField);
				dateTextView.setText(R.string.enter_date);
				scoreTextView.setText(R.string.enter_score);
				
		        //Get current date
		        calendar = Calendar.getInstance();
		        date     = calendar.getTime();
				
		        //Display date
		        displayDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date);
		        dateEditText.setText(displayDate);
		        
		        okButton     = (Button)scoreDialog.findViewById(R.id.okButton);
		        cancelButton = (Button)scoreDialog.findViewById(R.id.cancelButton);
		        
		        okButton.setOnClickListener(this);
		        cancelButton.setOnClickListener(this);
		        dateEditText.setOnClickListener(this);
		        return scoreDialog;
			case DATE_DIALOG_ID:
				calendar   = Calendar.getInstance();
				int cyear  = calendar.get(Calendar.YEAR);
				int cmonth = calendar.get(Calendar.MONTH);
				int cday   = calendar.get(Calendar.DAY_OF_MONTH);
				dateDialog = new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
				return dateDialog;
			default:
				return null;
		}
	}


	public void onClick(View src) {
		// TODO Auto-generated method stub
		switch(src.getId()){
			case R.id.dateTextField:
				showDialog(DATE_DIALOG_ID);
				break;
			case R.id.okButton:
				String date,score;
				date  = dateEditText.getText().toString();
				score = scoreEditText.getText().toString();
				
	        	ContentValues values = new ContentValues(); 
	        	values.put(dbHelper.WOD_DATE, date);
	        	values.put(dbHelper.WOD_SCORE, score);
	        	values.put(dbHelper.WOD_ID, wodId);				
				db.insert(dbHelper.SCORE_TABLE, null, values);
				scoreDialog.cancel();
				WodViewerActivity.this.onStart();
				break;
			case R.id.cancelButton:
				scoreDialog.cancel();
				break;
		}
	}
	
	//Update date when one has been selected
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(year, monthOfYear, dayOfMonth);
			date = calendar.getTime();
			displayDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date);
	        dateEditText.setText(displayDate);
		}
	};
	
}
