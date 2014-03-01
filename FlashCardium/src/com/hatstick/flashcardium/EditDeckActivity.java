package com.hatstick.flashcardium;

import com.hatstick.flashcardium.tools.CardArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;
import com.larphoid.overscrolllistview.OverscrollListview;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class EditDeckActivity extends Activity {

	private Context context = this;

	private DatabaseHandler db;
	private CardArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_deck);
		
		Intent intent = getIntent();
		String deck = intent.getExtras().getString("deck");
		setTitle("Edit " + deck);
		
		db = new DatabaseHandler(this);

		adapter = new CardArrayAdapter(this,db.getCardsFromDeck(deck));

		final OverscrollListview listView = (OverscrollListview)findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		listView.setLongClickable(true);
		listView.setElasticity(.15f);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				

			}

		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				final int index = position;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle(adapter.getItem(index).getQuestion());
				// set dialog message
				alertDialogBuilder.setItems(new CharSequence[]{"Edit","Delete","Cancel"}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case 0: // Edit

							return;
						case 1: // Delete
							
							return;

						default: // Cancel
							return;
						}
					}

				});		// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

				return true;
			}
		});
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_deck, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() 
	{
	    this.finish();
	    overridePendingTransition  (R.animator.slide_in, R.animator.slide_out);
	    return;
	}

}
