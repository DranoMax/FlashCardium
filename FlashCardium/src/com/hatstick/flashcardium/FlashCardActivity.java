package com.hatstick.flashcardium;

import java.util.ArrayList;
import java.util.List;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class FlashCardActivity extends FragmentActivity {

	private long startTime;
	//constant for defining the time to wait between clicks
	static final int MIN_WAIT= 250;
	//variable for calculating the total time
	private long duration = MIN_WAIT;

	private DatabaseHandler db;

	private List<Card> cardList = new ArrayList<Card>();

	private String deckName;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private ScreenSlidePagerAdapter mPagerAdapter;
	private List<FlashCardFragment> fragments;

	private boolean showingFront = true;
	private Animator leftOut;
	private Animator leftIn;
	private Animator rightOut;
	private Animator rightIn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash_card);

		db = new DatabaseHandler(this);

		Intent intent = getIntent();
		deckName = intent.getExtras().getString("deck");
		cardList = db.getCardsFromDeck(deckName);
		if(cardList.size() > 0) {
			setTitle(this.getString(R.string.text_question));
		}

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.layout_flashcard);

		fragments = getFragments();
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),fragments);
		setupPageView();

		loadAnimations();
	}
	
	private void setupPageView() {
		mPager.setAdapter(mPagerAdapter);
		mPager.setOffscreenPageLimit(0);

		mPager.setOnTouchListener(new OnTouchListener() {
			private float pointX;
			private float pointY;
			private int tolerance = 50;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					return false; //This is important, if you return TRUE the action of swipe will not take place.
				case MotionEvent.ACTION_DOWN:
					pointX = event.getX();
					pointY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance < event.getX();
					boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance < event.getY();
					if(sameX && sameY){
						screenTouched();
					}
				}
				return false;
			}
		});

		/**
		 * We need to "reset" each fragment view when we slide to a new one.
		 */
		mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				showingFront = true;
				duration = MIN_WAIT;
				setTitle(getResources().getString(R.string.text_question));
			}
		});
	}

	private void loadAnimations() {
		leftOut = AnimatorInflater.loadAnimator(FlashCardActivity.this, R.animator.card_flip_left_out);
		leftIn = AnimatorInflater.loadAnimator(FlashCardActivity.this, R.animator.card_flip_left_in);

		rightOut = AnimatorInflater.loadAnimator(FlashCardActivity.this, R.animator.card_flip_right_out);
		rightIn = AnimatorInflater.loadAnimator(FlashCardActivity.this, R.animator.card_flip_right_in);
	}
	
	private List<FlashCardFragment> getFragments(){
		List<FlashCardFragment> fList = new ArrayList<FlashCardFragment>();

		for(int i = 0; i < cardList.size(); i++) {
			fList.add(FlashCardFragment.newInstance(cardList.get(i).getQuestion()));
		}
		return fList;
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition  (R.animator.slide_in, R.animator.slide_out);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_create_object:
			addCard();
			return true;

		case R.id.menu_get_update:
			//		getCards();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * OnActivityResult tells us the id of the card added during FlashCardActivity,
	 * this allows us to inform the PagerAdapter and update its cardList accordingly.
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				try {
					long id = data.getLongExtra("valueName", -99); 
					if (id != -99) {
						// Need to add new Card to fragmentManager
						Card card = db.getCard(id, deckName);
						cardList.add(card);
						mPagerAdapter.addFragment(FlashCardFragment.newInstance(card.getQuestion()));
						mPagerAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {}
			}
		}
	}

	private void addCard() {
		Intent i = new Intent(this, NewCardActivity.class);
		i.putExtra("deck", deckName);
		startActivityForResult(i,1);
		//	startActivity(i);
	}

	/**
	 * Screen was touched, check to see if enough time has passed between touches.
	 * if(true) then flip card
	 * else: wait until enough time has passed.
	 * 
	 * Useful to keep card from flipping more than once in same touch.
	 */
	private void screenTouched() {
		duration+= (System.currentTimeMillis()-startTime);

		if (duration >= MIN_WAIT) {
			duration = 0;
			startTime = System.currentTimeMillis();
			flipCard();
		}
	}

	private void flipCard() {

		int index = mPager.getCurrentItem();
		FlashCardFragment frag = fragments.get(index);
		
		if(showingFront) {
			setTitle(this.getString(R.string.text_answer));
			leftOut.setTarget(frag.getView());
			leftIn.setTarget(frag.getView());
			leftOut.start();
			// Change text
			frag.setText(cardList.get(index).getAnswer());
			leftIn.start();
			frag.getView().findViewById(R.id.flash_card_background).setBackgroundResource(R.drawable.flashcard_portrait_back);
			showingFront = false;
		}
		else {
			setTitle(this.getString(R.string.text_question));
			rightOut.setTarget(frag.getView());
			rightIn.setTarget(frag.getView());
			rightOut.start();
			// Change text
			frag.setText(cardList.get(index).getQuestion());
			rightIn.start();
			frag.getView().findViewById(R.id.flash_card_background).setBackgroundResource(R.drawable.flashcard_portrait);
			showingFront = true;
		}
	}

	/**
	 * A simple pager adapter that represents our ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		private List<FlashCardFragment> fragments = new ArrayList<FlashCardFragment>();

		public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fragmentManager,
				List<FlashCardFragment> fragments) {
			super(fragmentManager);
			this.fragments = fragments;
		}

		@Override
		public FlashCardFragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		public void addFragment(FlashCardFragment fragment) {
			fragments.add(fragment);
		}
	}
}
