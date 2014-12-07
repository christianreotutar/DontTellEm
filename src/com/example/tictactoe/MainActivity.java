package com.example.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	public static final int RESULT_HIDE = 1;
	public static final int RESULT_UNHIDE = 2;
	
	ImageView mButton;
	PasswordFragment mGridView;
	TextView mPlayerText;
	TutorialFragment tutorialFrag;
	TextView tutorialText;
	public SharedPreferences mSharedPreferences;
	private boolean locked;
	protected boolean first_use;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get Views
		mButton = (ImageView) findViewById(R.id.button);
		mGridView = (PasswordFragment) getSupportFragmentManager().findFragmentById(R.id.passwordGrid);
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		tutorialFrag = (TutorialFragment) getSupportFragmentManager().findFragmentById(R.id.tutorialFragment);
		tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.first_use = mSharedPreferences.getBoolean("first_use", true);
		this.password = mSharedPreferences.getString("passwordString", "");
		this.locked = mSharedPreferences.getBoolean("locked", false);
		if (!first_use) {
			//remove tutorial fragment
			getSupportFragmentManager().beginTransaction().remove(tutorialFrag).commit();
		}
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClicked(v);
			}
		});
		if (locked) {
			this.buttonClicked(mButton);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new) {
			mGridView.resetGrid();
			return true;
		} else if (id == R.id.action_settings) {
			startActivity(new Intent(this, Settings.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void buttonClicked(View v) {
		Intent intent = new Intent(this, HideActivity.class);
		intent.putExtra("REQUEST_CODE", RESULT_HIDE);
		startActivityForResult(intent, RESULT_HIDE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean("first_use", this.first_use);
		editor.putBoolean("locked", locked);
		editor.putString("passwordString", password);
		
		//Save
		editor.commit();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.first_use = mSharedPreferences.getBoolean("first_use", true);
		this.password = mSharedPreferences.getString("passwordString", "");
		this.locked = mSharedPreferences.getBoolean("locked", false);
	}

	public void passwordSet() {
		if (mGridView.passwordSet){
			password = mGridView.password;
			tutorialText.setText(getResources().getString(R.string.tutorialE));
			((TextView) findViewById(R.id.otherText)).setVisibility(View.VISIBLE);
		}
	}
	
	public void passwordEntered() {
		mGridView.resetGrid();
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		getSupportFragmentManager().beginTransaction().hide(tutorialFrag).commit();
		((TextView) findViewById(R.id.otherText)).setVisibility(View.INVISIBLE);
		first_use = false;
		mButton.setVisibility(View.VISIBLE);
		mGridView.passwordEntered = false;
		mGridView.passwordAttempt = "";
		this.locked = false;
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == RESULT_HIDE) {
            if (resultCode == RESULT_OK) {
                // Hide button, show grid
				mButton.setVisibility(View.INVISIBLE);
				getSupportFragmentManager().beginTransaction().show(mGridView).commit();
				this.locked = true;
				if (first_use) tutorialText.setText(getResources().getString(R.string.tutorialD));
            }
        } else if (requestCode == RESULT_UNHIDE) {
        	if (resultCode == RESULT_OK) {
        		//cool
        	}
        }
    }

}
