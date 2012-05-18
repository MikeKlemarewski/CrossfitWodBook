package com.mklem.android.crossfitwodbook;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HomeActivity extends Activity implements OnClickListener{
	
	Button              dailyTrackingButton, wodButton, personalRecordButton;
	DBHelper            dbHelper;
	Cursor              cursor;
	SimpleCursorAdapter adapter;
	
	public static final int PERSONAL_RECORD = 2;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        dailyTrackingButton  = (Button)findViewById(R.id.ButtonDailyTracking);
        wodButton            = (Button)findViewById(R.id.ButtonWods);
        personalRecordButton = (Button)findViewById(R.id.ButtonPersonalRecords);

        dailyTrackingButton.setOnClickListener(this);
        wodButton.setOnClickListener(this);
        personalRecordButton.setOnClickListener(this);
        
        dbHelper = new DBHelper(this);

        try {
        	dbHelper.createDataBase();
        }
        catch(IOException ioe) {
        	throw new Error("Unable to create database");
        }
 
        
    }

	public void onClick(View src) {
		
		Intent myIntent;
		switch(src.getId()){
		case R.id.ButtonDailyTracking:
			myIntent = new Intent(src.getContext(), TrackingActivity.class);
            startActivity(myIntent);
            break;
		case R.id.ButtonWods:
			myIntent = new Intent(src.getContext(), WodCategorySelectionActivity.class);
            startActivity(myIntent);
			break;
		case R.id.ButtonPersonalRecords:
			myIntent = new Intent(src.getContext(), WodSelectionActivity.class);
			myIntent.putExtra("categoryDisplay", "Personal Records");
			myIntent.putExtra("category", "personal_record");
			myIntent.putExtra("pageType", PERSONAL_RECORD);
			startActivity(myIntent);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
	
}