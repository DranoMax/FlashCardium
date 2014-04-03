package com.hatstick.flashcardium;

import com.hatstick.flashcardium.dialogs.CreateDeckActivity;
import com.hatstick.flashcardium.dialogs.NewCardActivity;
import com.hatstick.flashcardium.entities.Deck;
import com.hatstick.flashcardium.tools.DeckArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;
import com.hatstick.flashcardium.webtools.AllCardsActivity;
import com.larphoid.overscrolllistview.OverscrollListview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity {

	private Context context = this;

	private DatabaseHandler db;
	private OverscrollListview listView;
	private DeckArrayAdapter adapter;

	private ProgressDialog dialog;
	private Handler handler;

	// Request codes
	private static final int CREATE_DECK = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		handler = new Handler();

		dialog = ProgressDialog.show(this, "Loading", "Loading flashcard decks!");
		Thread th = new Thread() {
			public void run() {
				try {
					db = new DatabaseHandler(MainActivity.this);
					handler.post(new Runnable() {
						public void run() {
							adapter = new DeckArrayAdapter(MainActivity.this,db.getAllDecks());
							adapter.sortDecks();
							createDeckListView();
							dialog.dismiss();
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}; th.start();
	}

	private void createDeckListView() {
		listView = (OverscrollListview)findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		listView.setLongClickable(true);
		listView.setElasticity(.15f);

		// Half-hack to stop OverScroll form sticking up above the list
		listView.smoothScrollToPosition(adapter.size());
		adapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				Deck deck = (Deck) listView.getAdapter().getItem(position);
				startFlashCards(deck.getName());
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				// For whatever reason, the position returned by onItemLongClick is returning
				// the position of onItemClick+1.  Thus, we -1 here.
				final int index = position-1;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle(adapter.getItem(index).getName());
				// set dialog message
				alertDialogBuilder.setItems(new CharSequence[]{"Edit","Delete","Cancel"}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case 0: // Edit
							Intent i = new Intent(MainActivity.this, EditDeckActivity.class);
							i.putExtra("deck", adapter.getItem(index).getName());
							startActivity(i);
							overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
							return;
						case 1: // Delete
							db.deleteDeck(adapter.getItem(index).getName());
							adapter.remove(adapter.getItem(index));
							Toast.makeText(MainActivity.this, "Deleted " + adapter.getItem(index).getName(), Toast.LENGTH_SHORT).show();
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
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_create_object:
			createDeck();
			return true;

		case R.id.menu_get_update:
			getCards();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == CREATE_DECK) { // Create Deck request
			if (resultCode == RESULT_OK) {
				Log.d("here","making deck");
				try {
					String deckName = data.getStringExtra("deck"); 
					if (deckName != null) {
						// Need to add new Card to fragmentManager
						Deck deck = db.getDeck(deckName);
						adapter.add(deck);
						adapter.sortDecks();
						adapter.notifyDataSetChanged();
					}
				} catch (Exception e) {}
			}
		}
	}

	private void createDeck() {
		Intent i = new Intent(MainActivity.this, CreateDeckActivity.class);
		startActivityForResult(i,CREATE_DECK);
		overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
	}

	private void startFlashCards(String deck) {
		Intent i = new Intent(this, FlashCardActivity.class);
		i.putExtra("deck", deck);
		startActivity(i);
		overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
	}

	private void getCards() {
		Intent i = new Intent(MainActivity.this, AllCardsActivity.class);
		startActivity(i);
		overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
	}
}
