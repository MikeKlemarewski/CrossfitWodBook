package com.mklem.android.crossfitwodbook;

import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.DatePicker;

public class TrackingActivity extends Activity implements OnClickListener{

	Button              backButton, forwardButton;
	TextView            dateTextView;
	Calendar            calendar;
	String              displayDate;
	Date                date;
	DatePicker          datePicker;
	ListView            wodList;
	DBHelper            dbHelper;
	SQLiteDatabase      db;
	Cursor              wodCursor;
	SimpleCursorAdapter adapter;
	
    static final int DATE_DIALOG_ID = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking);
        
        dateTextView  = (TextView)findViewById(R.id.dateText);
        forwardButton = (Button)findViewById(R.id.forwardButton);
        backButton    = (Button)findViewById(R.id.backButton);
        wodList       = (ListView)findViewById(R.id.WodListView);

        forwardButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        dateTextView.setOnClickListener(this);
        
        //Get current date
        calendar = Calendar.getInstance();
        date     = calendar.getTime();
		
        //Display date
        displayDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        dateTextView.setText(displayDate);
        
        dbHelper = new DBHelper(this);
        db       = dbHelper.getWritableDatabase();
    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
        /*
         * Query database for WODs on current day
         */
		try {
			updateWODS(displayDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
	
	/*
	 * Get the date and display all WODs done on that day
	 */
	private void updateWODS(String queryDate) throws ParseException{

		// Have to convert date to milis then to query format
		Long milis              = Date.parse(queryDate);
		String pattern          = "MMM d, yyyy";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		queryDate               = format.format(milis).toString();
		
        String sql = "SELECT w._id, w.wod_title, s.score FROM wod_table w, wod_scores s where s.date = \"" + queryDate + "\" AND s.wod_id = w._id";
        wodCursor = db.rawQuery(sql, null);

        String[] from = {dbHelper.WOD_TITLE, dbHelper.WOD_SCORE};
        int[] to      = {R.id.WodTextView, R.id.ScoreTextView};
        
        adapter = new SimpleCursorAdapter(this, R.layout.tracking_wod_entry, wodCursor, from , to);
        wodList.setAdapter(adapter);
        		
	}
	
	public void onClick(View src) {
		// TODO Auto-generated method stub
		switch(src.getId()){
		
		//Decrement date and update display
		case R.id.backButton:
			calendar.add(Calendar.DATE, -1);
	        date = calendar.getTime();
	        displayDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
	        dateTextView.setText(displayDate);

	        try {
				updateWODS(displayDate.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			break;
			
		//Increment date and update display
		case R.id.forwardButton:
			calendar.add(Calendar.DATE, 1);
	        date = calendar.getTime();
	        displayDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
	        dateTextView.setText(displayDate);
	        
	        try {
				updateWODS(displayDate.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
			break;
			
		case R.id.dateText:
			showDialog(DATE_DIALOG_ID);
			break;
		}
	}
	
	// Creating dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		calendar   = Calendar.getInstance();
		int cyear  = calendar.get(Calendar.YEAR);
		int cmonth = calendar.get(Calendar.MONTH);
		int cday   = calendar.get(Calendar.DAY_OF_MONTH);
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
		}
		return null;
	}
	
	//Update date when one has been selected
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(year, monthOfYear, dayOfMonth);
			date = calendar.getTime();
			displayDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
	        dateTextView.setText(displayDate);
	    
	        try {
				updateWODS(displayDate.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
}
