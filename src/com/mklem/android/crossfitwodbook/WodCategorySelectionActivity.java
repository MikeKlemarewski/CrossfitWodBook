package com.mklem.android.crossfitwodbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WodCategorySelectionActivity extends Activity implements OnClickListener{
    
	Button girlsButton, heroesButton, customButton;
	
	public static final int WOD_LIST = 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.wod_categories);
        
        girlsButton  = (Button)findViewById(R.id.ButtonGirls);
        heroesButton = (Button)findViewById(R.id.ButtonHeroes);
        customButton = (Button)findViewById(R.id.ButtonCustom);
        
        girlsButton.setOnClickListener(this);
        heroesButton.setOnClickListener(this);
        customButton.setOnClickListener(this);
        
    }

	public void onClick(View src) {

		Intent myIntent;
		switch(src.getId()){
		case R.id.ButtonGirls:
			myIntent = new Intent(src.getContext(), WodSelectionActivity.class);
			myIntent.putExtra("categoryDisplay", "The Girls");
			myIntent.putExtra("category", "girl");
			myIntent.putExtra("pageType", WOD_LIST);
			startActivity(myIntent);
            break;
		case R.id.ButtonHeroes:
			myIntent = new Intent(src.getContext(), WodSelectionActivity.class);
			myIntent.putExtra("categoryDisplay", "The Heroes");
			myIntent.putExtra("category", "hero");
			myIntent.putExtra("pageType", WOD_LIST);
            startActivity(myIntent);
			break;
		case R.id.ButtonCustom:
			myIntent = new Intent(src.getContext(), WodSelectionActivity.class);
			myIntent.putExtra("categoryDisplay", "Custom WODS");
			myIntent.putExtra("category", "custom");
			myIntent.putExtra("pageType", WOD_LIST);
			startActivity(myIntent);
			break;
		}
	}
}
