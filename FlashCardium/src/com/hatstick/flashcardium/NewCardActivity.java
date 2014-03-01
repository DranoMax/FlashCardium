package com.hatstick.flashcardium;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewCardActivity extends Activity {

	private Card card = new Card();
	private DatabaseHandler db;
	private String deckName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_add_card);

		db = new DatabaseHandler(this);

		Intent intent = getIntent();
		deckName = intent.getExtras().getString("deck");
		
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			EditText question=(EditText)findViewById(R.id.question);
			EditText answer=(EditText)findViewById(R.id.answer);
			EditText subject=(EditText)findViewById(R.id.subject);
			card.setQuestion(question.getText().toString());
			card.setAnswer(answer.getText().toString());
			card.setSubject(subject.getText().toString());
			card.setDeck(deckName);
			db.addCard(card);
			finish();
		}
	};
}
