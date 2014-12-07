package com.example.tictactoe;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class Settings extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	public void openDialog(View v) {
		ResetDialogFragment clearData = new ResetDialogFragment();
		FragmentManager fManager = getSupportFragmentManager();
		clearData.show(fManager, "clearData");
	}

}
