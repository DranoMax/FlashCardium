package com.hatstick.flashcardium.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hatstick.flashcardium.R;
import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.entities.Deck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardArrayAdapter extends ArrayAdapter<Card>{
	
	private Context context;
	private List<Card> cardList = new ArrayList<Card>();
	
	public CardArrayAdapter(Context context, List<Card> cards) {
		super(context, R.layout.card_array_adapter, cards);
		this.context = context;
		this.cardList = cards;
		Log.d("HERE","MAKING ARARY");
		for (Card card : cardList) {
			Log.d("card",card.getQuestion()+"");
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.card_array_adapter, parent, false);
		((TextView)rowView.findViewById(R.id.question)).setText(cardList.get(position).getQuestion());
		((TextView)rowView.findViewById(R.id.author)).setText(cardList.get(position).getAnswer());
		
		return rowView;
	}
	
	public void sortCards() {
		Collections.sort(cardList, new Comparator<Card>(){
			@Override
			public int compare(Card card1, Card card2) {
				// TODO Auto-generated method stub
				return (int)(card1.getId()-card2.getId());
			}
		});
	}
}
