package com.hatstick.flashcardium;

import java.util.ArrayList;
import java.util.List;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.entities.Deck;
import com.hatstick.flashcardium.tools.DeckArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;
import com.hatstick.webtools.AllCardsActivity;
import com.larphoid.overscrolllistview.OverscrollListview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private List<Card> cardsList = new ArrayList<Card>();

	private Context context = this;

	private DatabaseHandler db;
	private DeckArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DatabaseHandler(this);
		createDatabase(db);

		adapter = new DeckArrayAdapter(this,db.getAllDecks());
		adapter.sortDecks();

		final OverscrollListview listView = (OverscrollListview)findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
		listView.setLongClickable(true);
		listView.setElasticity(.15f);
		
		// Half-hack to stop OverScroll form sticking up above the list
		listView.smoothScrollToPosition(20);
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
		adapter.notifyDataSetChanged();
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

	private void createDatabase(DatabaseHandler db) {

		/**
		 * CRUD operations
		 */
		/*
		db.createDeck(new Deck("Math 101", "I love this class", "MccLovin"));
		db.createDeck(new Deck("Science 1030", "Biolab", "XxHeroxX"));
		db.createDeck(new Deck("English 2010", "Love me some English!", "MccLovin"));
		db.createDeck(new Deck("Computer Class", "", "Stan"));
		 
*/
	/*	
		Log.d("Reading: ", "Reading all decks..");
		for (Deck deck : deckList) {
			String log = "name " + deck.getName();
			Log.d("Name: ", log);
		}
		 */
		/*
		 Log.d("Insert: ", "Inserting..");
		 db.addCard(new Card("Math 101","Math","1+1","2"));
		 db.addCard(new Card("Math 101","Math","1+2","3"));
		 db.addCard(new Card("Math 101","Math","2+2","4"));
		 db.addCard(new Card("Math 101","Math","2+3","5"));
		 
		Log.d("Reading: ", "Reading all cards..");
		cardsList = db.getAllCards();
		for (Card card : cardsList) {
			String log = "Id: " +card.getId()+ " Deck: " + card.getDeck() + ", Subject: "+card.getSubject()+
					", Question: "+card.getQuestion();
			Log.d("Name: ", log);
		}
*/
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Log.d("FUCK","YOU");
			}
		}
	}

	private void createDeck() {
		Intent i = new Intent(MainActivity.this, CreateDeckActivity.class);
		startActivity(i);
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
