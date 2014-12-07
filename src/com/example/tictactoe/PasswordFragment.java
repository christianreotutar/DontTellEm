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
	public boolean passwordSet;
	public boolean passwordEntered = false;
	public String password = "";
	public String passwordAttempt = "";
	int numchars = 0;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_password, container, false);
		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setAdapter(new CustomGrid(getActivity(), maskRA));
		SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		passwordSet = mSharedPrefs.getBoolean("passwordSet", false);
		//final TextView mPlayerText = (TextView) (((MainActivity)getActivity()).findViewById(R.id.playerText));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	if (!passwordSet) {
	        		if (numchars < 5) {
		        		password += "" + position;
			        	numchars++;
			        	if (numchars == 5) {
				        	savePassword();
				        	passwordAttempt = "";
			        	}
		        	}
	        	} else if (!passwordEntered) {
	        		passwordAttempt += "" + position;
	        		//Toast.makeText(getActivity(), passwordAttempt, Toast.LENGTH_SHORT).show();
		        	checkPassword();
	        	}
	        	// Mark tile as x or o
	        	if (x && !((TextView) v).getText().equals("\nX\n")) {
	        		((TextView) v).setText("\nX\n");
	        	} else if (!x && !((TextView) v).getText().equals("\nO\n")){
	        		((TextView) v).setText("\nO\n");
	        	}
	        	x = !x;
	        }
	    });
		return view;
	}

	public void savePassword() {
		passwordSet = true;
		Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
		editor.putBoolean("passwordSet", passwordSet);
		editor.commit();
		resetGrid();
		Toast.makeText(getActivity(), "Password saved: " + password, Toast.LENGTH_SHORT).show();
		((MainActivity) getActivity()).passwordSet();
	}

	public void resetGrid() {
		passwordAttempt = "";
		mGridView.setAdapter(new CustomGrid(getActivity(), maskRA));
	}

	private void checkPassword() {
		if (passwordAttempt.equals(password)) {
			Toast.makeText(getActivity(), "Unlocked", Toast.LENGTH_LONG).show();
			passwordEntered = true;
			passwordAttempt = "";
			((MainActivity) getActivity()).passwordEntered();
		}
	}
	
}
