package com.mklem.android.crossfitwodbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditWodActivity extends Activity implements OnClickListener{

	Button         okButton, cancelButton;
	EditText       wodTitle, wodDescription;
	String         title, description, category;
	SQLiteDatabase db;
	DBHelper       dbHelper;
	
	int saveType;
	
	public static final int EDIT = 1;
	public static final int NEW = 2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_wod);

		okButton       = (Button)this.findViewById(R.id.ButtonSave);
		cancelButton   = (Button)this.findViewById(R.id.ButtonCancel);
		wodTitle       = (EditText)this.findViewById(R.id.EditWodTitle);
		wodDescription = (EditText)this.findViewById(R.id.EditWodDescription);
		
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
        	title       = extras.getString("wodTitle");
        	description = extras.getString("wodDescription");
        	category    = extras.getString("category");
        	saveType    = extras.getInt("saveType");
        	
        	wodTitle.setText(title);
        	wodDescription.setText(description);
        }	
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if(dbHelper!=null){
	    	dbHelper.close();
	    }
	}

	public void onClick(View src) {
		// TODO Auto-generated method stub
		switch(src.getId()){
		case R.id.ButtonSave:

	        dbHelper = new DBHelper(this);
	        db       = dbHelper.getWritableDatabase();
	        
	        String newTitle = wodTitle.getText().toString();
	        String newDescription = wodDescription.getText().toString();
	        
	        if(saveType == EDIT){
		        ContentValues updates = new ContentValues();
		        updates.put(dbHelper.WOD_TITLE, newTitle);
		        updates.put(dbHelper.WOD_DETAILS, newDescription);
		        
				db.update(dbHelper.WOD_TABLE, updates, dbHelper.WOD_TITLE + "=?", new String[] {title});	        	
	        }
	        else if(saveType == NEW){
	        	ContentValues values = new ContentValues(); 
	        	values.put(dbHelper.WOD_TITLE, newTitle);
	        	values.put(dbHelper.WOD_DETAILS, newDescription);
	        	values.put(dbHelper.WOD_CATEGORY, category);
				db.insert(dbHelper.WOD_TABLE, null, values);
	        }
	        
			Intent myIntent = this.getIntent();
			myIntent.putExtra("title",newTitle);
			setResult(RESULT_OK, myIntent);
			finish();
            break;
		case R.id.ButtonCancel:
			finish();
			break;
		}
	}	
}
