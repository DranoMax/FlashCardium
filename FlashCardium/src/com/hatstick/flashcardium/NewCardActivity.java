package com.hatstick.flashcardium;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewCardActivity extends Activity {

	private Card card;
	private DatabaseHandler db;
	private String deckName;
	private int editCard = -99;

	private EditText question;
	private EditText answer;
	private EditText subject;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_add_card);

		db = new DatabaseHandler(this);

		// Find our textViews
		question=(EditText)findViewById(R.id.question);
		answer=(EditText)findViewById(R.id.answer);
		subject=(EditText)findViewById(R.id.subject);

		Intent intent = getIntent();
		deckName = intent.getExtras().getString("deck");

		// Check to see if editing a card
		if (intent.getExtras().containsKey("card")) {
			editCard(intent.getExtras().getInt("card"));
		}

		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			// Create New Card
			if (editCard == -99) {
				card = new Card();
				setCard();
				card.setId(db.addCard(card));
			}
			// Else edit existing
			else {
				setCard();
				db.updateCard(card);
			}
			Intent resultData = new Intent();
			resultData.putExtra("valueName", card.getId());
			setResult(Activity.RESULT_OK, resultData);
			Toast.makeText(NewCardActivity.this, "Card Created", Toast.LENGTH_SHORT).show();
			finish();
		}
	};

	private void editCard(int id) {
		editCard = id;
		card = db.getCard(editCard,deckName);

		question.setText(card.getQuestion());
		answer.setText(card.getAnswer());
		subject.setText(card.getSubject());
	}
	
	private void setCard() {
		card.setQuestion(question.getText().toString());
		card.setAnswer(answer.getText().toString());
		card.setSubject(subject.getText().toString());
		card.setDeck(deckName);
	}
}
