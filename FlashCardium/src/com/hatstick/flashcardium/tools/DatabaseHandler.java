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
		
		return db.insert(TABLE_CARDS, null, values);
	}
	
	public Card getCard(long id, String deck) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CARDS, new String[] { KEY_ID,
				KEY_SUBJECT, KEY_QUESTION, KEY_ANSWER }, KEY_DECK + "=? AND "+ KEY_ID + "=?",
				new String[] {deck,String.valueOf(id)}, null, null, null, null);
		if(cursor != null) cursor.moveToFirst();
		
		Card card = new Card(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				cursor.getString(2), cursor.getString(3));
		
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
		return cardList;
	}
	
	public int updateCard(Card card) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_SUBJECT, card.getSubject());
		values.put(KEY_QUESTION, card.getQuestion());
		values.put(KEY_ANSWER, card.getAnswer());
		return db.update(TABLE_CARDS, values, KEY_DECK + "=? AND "+ KEY_ID + "=?",
				new String[] { card.getDeck(), String.valueOf(card.getId()) });
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
