package com.hatstick.flashcardium;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FlashCardFragment extends Fragment {

	public static final String Question = "Question";
	private View v;

	public static final FlashCardFragment newInstance(String message) {
		FlashCardFragment f = new FlashCardFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(Question, message);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String message = getArguments().getString(Question);
		v = inflater.inflate(R.layout.fragment_card_slide, container,false);
		TextView textView = (TextView)v.findViewById(R.id.flash_card_text);
		textView.setText(message);
		// -- inflate the layout for this fragment
		return v;
	}

	public void setText(String text) {
		TextView textView = (TextView)v.findViewById(R.id.flash_card_text);
		textView.setText(text);
	}
}