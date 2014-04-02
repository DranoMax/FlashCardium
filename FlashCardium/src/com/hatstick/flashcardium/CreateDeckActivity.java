package com.hatstick.flashcardium;

import com.hatstick.flashcardium.entities.Deck;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateDeckActivity extends Activity {

	private Deck deck = new Deck();
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_create_deck);

		db = new DatabaseHandler(this);
		
		Button save = (Button)findViewById(R.id.save);

		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			EditText name=(EditText)findViewById(R.id.name);
			EditText desc=(EditText)findViewById(R.id.decription);
			deck.setName(name.getText().toString());
			deck.setDescription(desc.getText().toString());
			db.createDeck(deck);
			Intent resultData = new Intent();
			resultData.putExtra("deck", deck.getName());
			setResult(Activity.RESULT_OK, resultData);
			finish();
		}
	};
}
