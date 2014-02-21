package com.hatstick.flashcardium;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FlashCardFragment extends Fragment  {
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// -- inflate the layout for this fragment
	    View view = inflater.inflate(R.layout.fragment_card_slide, container,false);

	    // Set the Text to try this out
	    TextView t = (TextView) view.findViewById(R.id.flash_card_text);
	    t.setText("Blank");
		return view;
	}
	
	public void setText(String text){
        TextView textView = (TextView)view.findViewById(R.id.flash_card_text);
        textView.setText(text);
    }
}