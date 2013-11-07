package edu.mines.locationfinder;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class EnterName extends Activity implements OnClickListener {

	
	private String name;
	private EditText enterName;
	private Button proceed;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_name);

		name = "";

		proceed = (Button)findViewById(R.id.proceed);
		proceed.setEnabled(false);
		proceed.setText(R.string.no_proceed);
		proceed.setOnClickListener(this);

		enterName = (EditText)findViewById(R.id.enterName);
		enterName.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				name = arg0.toString();
				if (name.equals("")) {
					proceed.setEnabled(false);
					proceed.setText(R.string.no_proceed);
				} else {
					proceed.setEnabled(true);
					proceed.setText(getResources().getString(R.string.yes_proceed) + " " + name);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_name, menu);
		return true;
	}

	public void checkName() {
		/**
		 * This function check is the name has been used and changes it if it has.
		 *
		 */

		// open a cursor, get an array of previous names
		String[] projection = { LocationTable.COLUMN_NAME };
		//Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		/*if (cursor != null) {
			cursor.moveToFirst();
			
			String n = cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME));
			Log.d("name!!!", n);
		}*/
	
	}

	public void proceed() {
		checkName();
		Intent intent = new Intent(this, RecordTrail.class);
		intent.putExtra("name", name);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.proceed:
			proceed();
		default:
			//do nothing
		}

	}
}
