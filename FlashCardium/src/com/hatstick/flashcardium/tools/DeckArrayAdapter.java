package com.hatstick.flashcardium.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hatstick.flashcardium.R;
import com.hatstick.flashcardium.entities.Deck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeckArrayAdapter extends ArrayAdapter<Deck>{
	
	private Context context;
	private List<Deck> deckList = new ArrayList<Deck>();
	
	public DeckArrayAdapter(Context context, List<Deck> decks) {
		super(context, R.layout.deck_array_adapter, decks);
		this.context = context;
		this.deckList = decks;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.deck_array_adapter, parent, false);
		((TextView)rowView.findViewById(R.id.name)).setText(deckList.get(position).getName());
		((TextView)rowView.findViewById(R.id.description)).setText(deckList.get(position).getDescription());
		
		return rowView;
	}
	
	public void sortDecks() {

		Collections.sort(deckList, new Comparator<Deck>(){
			@Override
			public int compare(Deck deck1, Deck deck2) {
				return deck1.getName().compareToIgnoreCase(deck2.getName());
			}
		});
	}

}
