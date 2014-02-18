package com.hatstick.flashcardium.tools;

import java.util.ArrayList;
import java.util.List;

import com.hatstick.flashcardium.Card;
import com.hatstick.flashcardium.Deck;
import com.hatstick.flashcardium.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardArrayAdapter extends ArrayAdapter<Deck>{
	
	private Context context;
	private List<Deck> deckList = new ArrayList<Deck>();
	
	public CardArrayAdapter(Context context, List<Deck> decks) {
		super(context, R.layout.activity_main, decks);
		this.context = context;
		this.deckList = decks;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.activity_main, parent, false);
		((TextView)rowView.findViewById(R.id.name)).setText(deckList.get(position).getName());
		((TextView)rowView.findViewById(R.id.description)).setText(deckList.get(position).getDescription());
		
		return rowView;
	}
}
