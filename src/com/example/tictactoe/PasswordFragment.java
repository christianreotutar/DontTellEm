package com.example.tictactoe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordFragment extends Fragment {

	GridView mGridView;
	String[] maskRA = {
			" \n\n", " \n\n", " \n\n",
			" \n\n", " \n\n", " \n\n",
			" \n\n", " \n\n", " \n\n"
	};
	private boolean x = true;
	public boolean passwordSet = false;
	public boolean passwordEntered = false;
	public String password = "";
	public String passwordAttempt = "";
	int numchars = 0;
	SharedPreferences mSharedPrefs;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_password, container, false);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setAdapter(new CustomGrid(getActivity(), maskRA));
		//final TextView mPlayerText = (TextView) (((MainActivity)getActivity()).findViewById(R.id.playerText));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	if (!passwordSet) {
	        		if (numchars < 5) {
		        		password += "" + position;
			        	numchars++;
			        	if (numchars == 5) {
			        		passwordSet = true;
				        	savePassword();
				        	passwordAttempt = "";
			        	}
		        	}
	        	} else if (!passwordEntered) {
	        		passwordAttempt += "" + position;
		        	checkPassword();
	        	}
	        	// Mark tile as x or o
	        	if (x && !((TextView) v).getText().toString().equals("\nX\n")) {
	        		((TextView) v).setText("\nX\n");
	        	} else if (!x && !((TextView) v).getText().toString().equals("\nO\n")){
	        		((TextView) v).setText("\nO\n");
	        	}
	        	x = !x;
	        }
	    });
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		passwordSet = mSharedPrefs.getBoolean("passwordSet", false);
		if (passwordSet) {
			password = mSharedPrefs.getString("passwordString", "");
		}
		passwordEntered = false;
	}

	public void savePassword() {
		passwordSet = true;
		Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
		editor.putBoolean("passwordSet", true);
		editor.putBoolean("locked", true);
		editor.putString("passwordString", password);
		editor.commit();
		resetGrid();
		Toast.makeText(getActivity(), "Password saved", Toast.LENGTH_SHORT).show();
		((MainActivity) getActivity()).passwordSet();
	}

	public void resetGrid() {
		passwordAttempt = "";
		numchars = 0;
		mGridView.setAdapter(new CustomGrid(getActivity(), maskRA));
	}

	private void checkPassword() {
		if (passwordAttempt.equals(password)) {
			passwordEntered = true;
			passwordAttempt = "";
			((MainActivity) getActivity()).passwordEntered();
		}
	}
	
	public void newPassword() {
		password = "";
		numchars = 0;
		passwordAttempt = "";
		passwordEntered = false;
	}
	
}
