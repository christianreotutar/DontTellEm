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
import android.widget.Toast;

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
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		getActionBar().setIcon(R.drawable.tictactoelogo);

		//Get Views
		mButton = (ImageView) findViewById(R.id.button);
		mGridView = (PasswordFragment) getSupportFragmentManager().findFragmentById(R.id.passwordGrid);
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		tutorialFrag = (TutorialFragment) getSupportFragmentManager().findFragmentById(R.id.tutorialFragment);
		tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);
		this.first_use = mSharedPreferences.getBoolean("first_use", true);
		//if (first_use) Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
		this.password = mSharedPreferences.getString("passwordString", "");
		this.locked = mSharedPreferences.getBoolean("locked", false);
		if (!first_use) {
			//remove tutorial fragment
			getSupportFragmentManager().beginTransaction().hide(tutorialFrag).commit();
			((TextView) findViewById(R.id.otherText)).setVisibility(View.INVISIBLE);
		}
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClicked(v);
			}
		});
		if (locked) {
			//hide button, show grid
			mButton.setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction().show(mGridView).commit();
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
		mSharedPreferences.edit().putBoolean("passwordSet", false).commit();
		first_use = mSharedPreferences.getBoolean("first_use", true);
		mGridView.newPassword();
		if (!first_use) {
			Intent intent = new Intent(this, HideActivity.class);
			intent.putExtra("REQUEST_CODE", RESULT_HIDE);
			startActivityForResult(intent, RESULT_HIDE);
		} else {
			// Hide button, show grid
			((TextView)(tutorialFrag.getView().findViewById(R.id.fragText))).setText(R.string.tutorialD);
			mButton.setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction().show(mGridView).commit();
			this.locked = true;
		}
		Toast.makeText(this, "Enter a new password", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		save();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		save();
	}
	
	private void save() {
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
		if (locked) {
			//hide button, show grid
			mButton.setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction().show(mGridView).commit();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		this.first_use = mSharedPreferences.getBoolean("first_use", true);
		this.password = mSharedPreferences.getString("passwordString", "");
		this.locked = mSharedPreferences.getBoolean("locked", false);
		if (locked) {
			//hide button, show grid
			mButton.setVisibility(View.INVISIBLE);
			getSupportFragmentManager().beginTransaction().show(mGridView).commit();
		}
	}

	public void passwordSet() {
		if (mSharedPreferences.getBoolean("passwordSet", false)){
			password = mSharedPreferences.getString("passwordString", "");
			//Toast.makeText(this, password + "", Toast.LENGTH_SHORT).show();
			if (first_use) {
				((TextView)(tutorialFrag.getView().findViewById(R.id.fragText))).setText(R.string.tutorialE);
				((TextView) findViewById(R.id.otherText)).setVisibility(View.VISIBLE);
			}
			this.locked = true;
			save();
		}
	}
	
	public void passwordEntered() {
		mGridView.resetGrid();
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		getSupportFragmentManager().beginTransaction().hide(tutorialFrag).commit();
		((TextView) findViewById(R.id.otherText)).setVisibility(View.INVISIBLE);
		this.locked = false;
		this.first_use = false;
		mButton.setVisibility(View.VISIBLE);
		save();

		Intent intent = new Intent(this, HideActivity.class);
        intent.putExtra("REQUEST_CODE", RESULT_UNHIDE);
        startActivityForResult(intent, RESULT_UNHIDE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == RESULT_HIDE) {
            if (resultCode == RESULT_OK) {
                // Hide button, show grid
				mButton.setVisibility(View.INVISIBLE);
				getSupportFragmentManager().beginTransaction().show(mGridView).commit();
				this.locked = true;
            }
        } else if (requestCode == RESULT_UNHIDE) {
        	if (resultCode == RESULT_OK) {
        		//cool
        	}
        }
    }

}
