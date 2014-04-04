package com.hatstick.flashcardium;

import com.hatstick.flashcardium.dialogs.NewCardActivity;
import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.tools.CardArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;
import com.larphoid.overscrolllistview.OverscrollListview;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class EditDeckActivity extends Activity {

	private Context context = this;

	private DatabaseHandler db;
	private CardArrayAdapter adapter;

	private String deck;

	private Card editedCard = new Card();

	// Request codes
	private static final int EDIT_CARD = 1;
	private static final int ADD_CARD = 2;

	private OverscrollListview listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_deck);

		Intent intent = getIntent();
		deck = intent.getExtras().getString("deck");
		setTitle("Edit " + deck);

		db = new DatabaseHandler(this);

		adapter = new CardArrayAdapter(this,db.getCardsFromDeck(deck));

		listView = (OverscrollListview)findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		listView.setLongClickable(true);
		listView.setElasticity(.15f);

		// Half-hack to stop OverScroll form sticking up above the list
		listView.smoothScrollToPosition(20);
		adapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			/** 
			 * Edit an individual card
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				// Create an object out of edited card (so we can remove it from our adapter
				// later if the editing completes successfully)
				editedCard = (Card)listView.getAdapter().getItem(position);
				editCard();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				// Note that we use position-1 because we are calling the adapter's item not the listView's
				// I do this so I can access the items outside of this method such as in deleteCard(int index);
				final int index = position-1;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle(adapter.getItem(index).getQuestion());
				// set dialog message
				alertDialogBuilder.setItems(new CharSequence[]{"Edit","Delete","Cancel"}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case 0: // Edit
							editedCard = adapter.getItem(index);
							editCard();
							return;
						case 1: // Delete
							deleteCard(index);
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

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == EDIT_CARD) {
				try {
					long id = data.getLongExtra("cardId", -99); 
					if (id != -99) {
						// Remove old (edited) card
						adapter.remove(editedCard);
						// And add the new version
						adapter.add(db.getCard(id, deck));
					}	
				} catch (Exception e) { e.getStackTrace(); }
			}
			else if (requestCode == ADD_CARD) {
				try {
					long id = data.getLongExtra("cardId", -99); 
					if (id != -99) {
						// And add the new version
						adapter.add(db.getCard(id, deck));
					}	
				} catch (Exception e) { e.getStackTrace(); }
			}
			adapter.sortCards();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		onContentChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_deck, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_create_object:
			createCard();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onDeleteButtonPressed(View view) {
		deleteCard((Integer)view.getTag());
	}

	private void editCard() {
		Intent i = new Intent(EditDeckActivity.this, NewCardActivity.class);
		i.putExtra("deck", deck);
		i.putExtra("card", editedCard.getId());
		startActivityForResult(i,EDIT_CARD);
	}

	private void createCard() {
		Intent i = new Intent(this, NewCardActivity.class);
		i.putExtra("deck", deck);
		startActivityForResult(i,ADD_CARD);
	}

	private void deleteCard(int index) {
		Card card = adapter.getItem(index);
		db.deleteCard(card);
		Toast.makeText(EditDeckActivity.this, R.string.message_card_deleted, Toast.LENGTH_SHORT).show();
		adapter.remove(card);
		adapter.sortCards();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition  (R.animator.slide_in, R.animator.slide_out);
		return;
	}
}
