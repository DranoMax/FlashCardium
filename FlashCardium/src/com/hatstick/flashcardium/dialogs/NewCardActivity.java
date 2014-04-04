package com.hatstick.flashcardium.dialogs;

import com.hatstick.flashcardium.R;
import com.hatstick.flashcardium.R.id;
import com.hatstick.flashcardium.R.layout;
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
	private long editCard = -99;

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
			setTitle(R.string.title_edit_card);
			editCard(intent.getExtras().getLong("card"));
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
				Toast.makeText(NewCardActivity.this, R.string.message_card_created, Toast.LENGTH_SHORT).show();
			}
			// Else edit existing
			else {
				setCard();
				db.updateCard(card);
				Toast.makeText(NewCardActivity.this, R.string.message_card_edited, Toast.LENGTH_SHORT).show();
			}
			Intent resultData = new Intent();
			resultData.putExtra("cardId", card.getId());
			setResult(Activity.RESULT_OK, resultData);
			finish();
		}
	};

	private void editCard(long id) {
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
