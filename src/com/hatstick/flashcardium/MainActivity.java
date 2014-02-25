package com.hatstick.flashcardium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.entities.Deck;
import com.hatstick.flashcardium.tools.CardArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private List<Deck> deckList = new ArrayList<Deck>();
	private List<Card> cardsList = new ArrayList<Card>();
	private DatabaseHandler db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	setContentView(R.layout.activity_main);

		db = new DatabaseHandler(this);
		createDatabase(db);

		sortDecks();
		setListAdapter(new CardArrayAdapter(this,deckList));
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Deck deck = (Deck) getListAdapter().getItem(position);

		Intent i = new Intent(this, FlashCardActivity.class);
		i.putExtra("deck", deck.getName());
		startActivity(i);
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

	private void sortDecks() {

		Collections.sort(deckList, new Comparator<Deck>(){
			@Override
			public int compare(Deck deck1, Deck deck2) {
				return deck1.getName().compareToIgnoreCase(deck2.getName());
			}
		});
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
		deckList = db.getAllDecks();

		Log.d("Reading: ", "Reading all decks..");
		for (Deck deck : deckList) {
			String log = "name " + deck.getName();
			Log.d("Name: ", log);
		}

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

	private void startFlashCards() {
		Intent i = new Intent(MainActivity.this, FlashCardActivity.class);
		startActivity(i);
	}

	private void getCards() {
		Intent i = new Intent(MainActivity.this, AllCardsActivity.class);
		startActivity(i);
	}
}
