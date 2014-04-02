package com.hatstick.flashcardium.tools;

import java.util.ArrayList;
import java.util.List;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.entities.Deck;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class makes use of the SQLiteAssetHelper available via github: https://github.com/jgilfelt/android-sqlite-asset-helper.
 * I owe a great debt of thanks to them for their awesome library!
 * 
 * One may ask: "Why use a prebuilt database?" and my answer is simple - We don't have to; especially not for one so
 * small to begin with.  But I prefer to keep as many things out of the class code as possible - the same way I define
 * everything I can for layouts inside of the xml rather than programatically.
 * 
 * It looks better in my opinion and if I decided to expand the database for whatever reason it would run much quicker on 
 * first load if it's already built rather by than executing a mile-long page of sql scripts.
 * 
 * @author dragon_slayer2000
 */

public class DatabaseHandler extends SQLiteAssetHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "flashcardium.db";
	private static final String TABLE_DECKS = "decks";
	private static final String TABLE_CARDS = "cards";

	// TABLE decks Column names
	private static final String KEY_DECK = "deck";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_AUTHOR = "author";

	// TABLE cards Column names
	private static final String KEY_ID = "id";
	private static final String KEY_SUBJECT = "subject";
	private static final String KEY_QUESTION = "question";
	private static final String KEY_ANSWER = "answer";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void createDeck(Deck deck) {
		Log.d("deck","add");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DECK, deck.getName());
		values.put(KEY_DESCRIPTION, deck.getDescription());
		values.put(KEY_AUTHOR, deck.getAuthor());

		db.insert(TABLE_DECKS, null, values);
		db.close();
	}

	public Deck getDeck(String deckName) {
		Log.d("deck","get");
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DECKS, new String[] { KEY_DECK,
				KEY_DESCRIPTION, KEY_AUTHOR }, KEY_DECK + "=?",
				new String[] {deckName}, null, null, null);
		if(cursor != null) cursor.moveToFirst();

		Deck deck = new Deck(cursor.getString(0), cursor.getString(1),
				cursor.getString(2));
		db.close();
		return deck;
	}

	public List<Deck> getAllDecks() {

		List<Deck> deckList = new ArrayList<Deck>();

		String selectQuery = "SELECT * FROM " + TABLE_DECKS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst()) {
			do {
				Deck d = new Deck();
				d.setName(cursor.getString(0));
				d.setDescription(cursor.getString(1));
				d.setAuthor(cursor.getString(2));

				deckList.add(d);
			} while(cursor.moveToNext());
		}
		db.close();
		return deckList;
	}


	public long addCard(Card card) {
		Log.d("here","add");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DECK, card.getDeck());
		values.put(KEY_SUBJECT, card.getSubject());
		values.put(KEY_QUESTION, card.getQuestion());
		values.put(KEY_ANSWER, card.getAnswer());
		
		Long result = db.insert(TABLE_CARDS, null, values);
		db.close();
		return result;
	}

	public Card getCard(long id, String deck) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CARDS, new String[] { KEY_ID,
				KEY_SUBJECT, KEY_QUESTION, KEY_ANSWER }, KEY_DECK + "=? AND "+ KEY_ID + "=?",
				new String[] {deck,String.valueOf(id)}, null, null, null, null);
		if(cursor != null) cursor.moveToFirst();

		Card card = new Card(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				cursor.getString(2), cursor.getString(3));
		db.close();
		return card;
	}

	public List<Card> getCardsFromDeck(String deck) {
		List<Card> cardList = new ArrayList<Card>();

		String selectQuery = "SELECT * FROM " + TABLE_CARDS + " WHERE " + KEY_DECK + "=\"" + deck +"\"";

		Log.d("Query: ",selectQuery+"");

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst()) {
			do {
				Card card = new Card();
				card.setId(Integer.parseInt(cursor.getString(0)));
				card.setDeck(cursor.getString(1));
				card.setSubject(cursor.getString(2));
				card.setQuestion(cursor.getString(3));
				card.setAnswer(cursor.getString(4));

				cardList.add(card);
			} while(cursor.moveToNext());
		}
		db.close();
		return cardList;
	}

	public List<Card> getAllCards() {
		List<Card> cardList = new ArrayList<Card>();

		String selectQuery = "SELECT * FROM " + TABLE_CARDS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst()) {
			do {
				Card card = new Card();
				card.setId(Integer.parseInt(cursor.getString(0)));
				card.setDeck(cursor.getString(1));
				card.setSubject(cursor.getString(2));
				card.setQuestion(cursor.getString(3));
				card.setAnswer(cursor.getString(4));

				cardList.add(card);
			} while(cursor.moveToNext());
		}
		db.close();
		return cardList;
	}

	public int updateCard(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT, card.getSubject());
		values.put(KEY_QUESTION, card.getQuestion());
		values.put(KEY_ANSWER, card.getAnswer());
		
		int result = db.update(TABLE_CARDS, values, KEY_DECK + "=? AND "+ KEY_ID + "=?",
				new String[] { card.getDeck(), String.valueOf(card.getId()) });
		db.close();
		return result;
	}

	public void deleteCard(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CARDS, KEY_ID + " = ?",
				new String[] { String.valueOf(card.getId()) });
		db.close();
	}

	public void deleteDeck(String deck) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DECKS, KEY_DECK + " = ?",
				new String[] { deck });
		db.close();
	}
}
