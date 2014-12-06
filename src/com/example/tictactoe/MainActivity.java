package com.example.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	ImageView mButton;
	PasswordFragment mGridView;
	TextView mPlayerText;
	ProgressBar mProgress;
	TutorialFragment tutorialFrag;
	SharedPreferences mSharedPreferences;
	final Context context = this;
	private boolean locked = false;
	public boolean first_use = true;
	private String password;
	//For progress bar
	Handler mHandler = new Handler();
	private int progressStatus = 0;
	private boolean hideProgress = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get Views
		mButton = (ImageView) findViewById(R.id.button);
		mGridView = (PasswordFragment) getSupportFragmentManager().findFragmentById(R.id.passwordGrid);
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		//getSupportFragmentManager().beginTransaction().hide(mSetPassword);
		mPlayerText = (TextView) findViewById(R.id.playerText);
		mProgress = (ProgressBar) findViewById(R.id.loading);
		tutorialFrag = (TutorialFragment) getSupportFragmentManager().findFragmentById(R.id.tutorialFragment);

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		//first_use = mSharedPreferences.getBoolean("first_use", true);
		this.password = mSharedPreferences.getString("passwordString", "");
		//this.locked = mSharedPreferences.getBoolean("locked", false);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void buttonClicked(View v) {
		if (first_use) {
			TextView tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);
			tutorialText.setText(getResources().getString(R.string.tutorialB));
			choosePhotos();
		}
		// Hide button, show grid
		mButton.setVisibility(View.INVISIBLE);
		getSupportFragmentManager().beginTransaction().show(mGridView).commit();
		this.hide();
		this.locked = true;
	}
	
	private void hide() {
		mProgress.setVisibility(View.VISIBLE);
		TextView tutorialText;
		if (first_use) {
			tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);
			tutorialText.setText(getResources().getString(R.string.tutorialC));
            setPassword();
		}
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 10000) {
                    //Hide stuff
                	progressStatus++;
            		//Code for hiding files, get from Eric

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start();
        mProgress.setVisibility(View.GONE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean("first_use", false);
		editor.putBoolean("locked", locked);
		editor.putString("passwordString", password);
		
		//Save
		editor.commit();
	}
	
	private void choosePhotos() {
		//TODO
	}

	private void setPassword() {
		TextView tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);
		tutorialText.setText(getResources().getString(R.string.tutorialD));
		getSupportFragmentManager().beginTransaction().show(mGridView).commit();
	}
	
	public void passwordSet() {
		TextView tutorialText = (TextView) tutorialFrag.getView().findViewById(R.id.fragText);
		if (mGridView.passwordSet){
			password = mGridView.password;
			tutorialText.setText(getResources().getString(R.string.tutorialE));
			((TextView) findViewById(R.id.otherText)).setVisibility(View.VISIBLE);
		}
	}
	
	public void passwordEntered() {
		mGridView.resetGrid();
		getSupportFragmentManager().beginTransaction().hide(mGridView).commit();
		((TextView) findViewById(R.id.otherText)).setVisibility(View.INVISIBLE);
		getSupportFragmentManager().beginTransaction().hide(tutorialFrag).commit();
		mButton.setVisibility(View.VISIBLE);
		mGridView.passwordEntered = false;
		mGridView.passwordAttempt = "";
	}

}
