package com.hatstick.flashcardium;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hatstick.flashcardium.tools.CardArrayAdapter;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private List<Deck> deckList = new ArrayList<Deck>();
	private List<Card> cardsList = new ArrayList<Card>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	setContentView(R.layout.activity_main);

		DatabaseHandler db = new DatabaseHandler(this);
		createDatabase(db);

		setListAdapter(new CardArrayAdapter(this,deckList));
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Load deck's cards into list
		//		cardsList = db.getCardsFromDeck(((TextView)view).getText().toString());
				// Begin flashcard session
				//String test = ((TextView)arg1).getText().toString();
		//		Log.d("Clicked",);
			}
		});
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
		case R.id.menu_add_cards:
			addCards();
			return true;

		case R.id.menu_get_cards:
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
		deckList = db.getAllDecks();

		Log.d("Reading: ", "Reading all decks..");
		for (Deck deck : deckList) {
			String log = "name " + deck.getName();
			Log.d("Name: ", log);
		}

		/*
		 Log.d("Insert: ", "Inserting..");
		 db.addCard(new Card("Math","1+1","2"));
		 db.addCard(new Card("Math","1+2","3"));
		 db.addCard(new Card("Math","2+2","4"));
		 db.addCard(new Card("Math","2+3","5"));
		 */
		Log.d("Reading: ", "Reading all cards..");
		cardsList = db.getAllCards();
		for (Card card : cardsList) {
			String log = "Id: " +card.getId()+", Subject: "+card.getSubject()+
					", Question: "+card.getQuestion();
			Log.d("Name: ", log);
		}
	}

	private void addCards() {
		Intent i = new Intent(MainActivity.this, NewCardActivity.class);
		startActivity(i);
	}

	private void getCards() {
		Intent i = new Intent(MainActivity.this, AllCardsActivity.class);
		startActivity(i);
	}
}
