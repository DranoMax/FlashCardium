package com.hatstick.flashcardium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.entities.Deck;
import com.hatstick.flashcardium.tools.CardArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private List<Card> cardsList = new ArrayList<Card>();

	private Context context = this;

	private DatabaseHandler db;
	private CardArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DatabaseHandler(this);
		createDatabase(db);

		adapter = new CardArrayAdapter(this,db.getAllDecks());
		adapter.sortDecks();
		setListAdapter(adapter);

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setLongClickable(true);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				final int index = position;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle(adapter.getItem(index).getName());
				// set dialog message
				alertDialogBuilder.setItems(new CharSequence[]{"Edit","Delete","Cancel"}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {

						case 0: // Edit

							return;
						case 1: // Delete
							Log.d("delete",adapter.getItem(index).getName());
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Deck deck = (Deck) getListAdapter().getItem(position);
		startFlashCards(deck.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		onContentChanged();
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
		db.createDeck(new Deck("Math 101", "I hate this class", "MccLovin"));
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
		 */
		Log.d("Reading: ", "Reading all cards..");
		cardsList = db.getAllCards();
		for (Card card : cardsList) {
			String log = "Id: " +card.getId()+ " Deck: " + card.getDeck() + ", Subject: "+card.getSubject()+
					", Question: "+card.getQuestion();
			Log.d("Name: ", log);
		}

	}

	private void createDeck() {
		Intent i = new Intent(MainActivity.this, CreateDeckActivity.class);
		startActivity(i);
	}

	private void startFlashCards(String deck) {
		Intent i = new Intent(this, FlashCardActivity.class);
		i.putExtra("deck", deck);
		startActivity(i);
	}

	private void getCards() {
		Intent i = new Intent(MainActivity.this, AllCardsActivity.class);
		startActivity(i);
	}
}
