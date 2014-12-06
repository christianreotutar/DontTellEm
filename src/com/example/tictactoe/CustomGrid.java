package com.example.tictactoe;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomGrid extends BaseAdapter {

	Context context;
	String[] values;
	
	public CustomGrid(Context context, String[] values) {
		this.context = context;
		this.values = values;
	}

	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = new TextView(context);
        text.setText(values[position]);
        text.setTextColor(Color.BLACK);
        text.setTextSize(24);
        text.setGravity(Gravity.CENTER);
        text.setBackgroundColor(Color.WHITE);
        return text;
	}
	
}
