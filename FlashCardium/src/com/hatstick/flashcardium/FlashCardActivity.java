package com.hatstick.flashcardium;

import java.util.ArrayList;
import java.util.List;

import com.hatstick.flashcardium.entities.Card;
import com.hatstick.flashcardium.tools.DatabaseHandler;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;


public class FlashCardActivity extends FragmentActivity
implements FragmentManager.OnBackStackChangedListener {
	/**
	 * A handler object, used for deferring UI operations.
	 */
	private Handler mHandler = new Handler();

	/**
	 * Whether or not we're showing the back of the card (otherwise showing the front).
	 */
	private boolean mShowingBack = false;

	private long startTime;
	//constant for defining the time to wait between clicks
	static final int MIN_WAIT= 500;
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
	private PagerAdapter mPagerAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash_card);

		db = new DatabaseHandler(this);

		Intent intent = getIntent();
		deckName = intent.getExtras().getString("deck");

		cardList = db.getCardsFromDeck(deckName);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.flashcard);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		if (savedInstanceState == null) {
			CardFrontFragment frontFrag = new CardFrontFragment();
			// If there is no saved instance state, add a fragment representing the
			// front of the card to this activity. If there is saved instance state,
			// this fragment will have already been added to the activity.
			getFragmentManager()
			.beginTransaction()
			.add(R.id.flashcard, frontFrag)
			.commit();
		} else {
			mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
		}
		// Monitor back stack changes to ensure the action bar shows the appropriate
		// button (either "photo" or "info").
		getFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (duration >= MIN_WAIT) {
			duration = 0;
			startTime = System.currentTimeMillis();

			flipCard();
		}else {
			duration+= (System.currentTimeMillis()-startTime);
		}

		return true;
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

	private void addCard() {
		Intent i = new Intent(this, NewCardActivity.class);
		i.putExtra("deck", deckName);
		startActivity(i);
	}

	private void flipCard() {
		if (mShowingBack) {
			getFragmentManager().popBackStack();
			return;
		}

		// Flip to the back.

		mShowingBack = true;

		CardBackFragment backFrag = new CardBackFragment();
		// Create and commit a new fragment transaction that adds the fragment for the back of
		// the card, uses custom animations, and is part of the fragment manager's back stack.

		getFragmentManager()
		.beginTransaction()

		// Replace the default fragment animations with animator resources representing
		// rotations when switching to the back of the card, as well as animator
		// resources representing rotations when flipping back to the front (e.g. when
		// the system Back button is pressed).
		.setCustomAnimations(
				R.animator.card_flip_right_in, R.animator.card_flip_right_out,
				R.animator.card_flip_left_in, R.animator.card_flip_left_out)

				// Replace any fragments currently in the container view with a fragment
				// representing the next page (indicated by the just-incremented currentPage
				// variable).
				.replace(R.id.flashcard, backFrag)

				// Add this transaction to the back stack, allowing users to press Back
				// to get to the front of the card.
				.addToBackStack(null)

				// Commit the transaction.
				.commit();

		// Defer an invalidation of the options menu (on modern devices, the action bar). This
		// can't be done immediately because the transaction may not yet be committed. Commits
		// are asynchronous in that they are posted to the main thread's message loop.
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
			}
		});
	}

	@Override
	public void onBackStackChanged() {
		mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

		// When the back stack changes, invalidate the options menu (action bar).
		invalidateOptionsMenu();
	}
	
	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public FlashCardFragment getItem(int position) {
			FlashCardFragment frag = new FlashCardFragment();
			frag.setText(cardList.get(position).getQuestion());
			return frag;
		}

		@Override
		public int getCount() {
			return cardList.size();
		}
	}

	/**
	 * A fragment representing the front of the card.
	 */
	public static class CardFrontFragment extends Fragment {

		private View frontView;

		public CardFrontFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.d("front","here");
			LayoutInflater lf = getActivity().getLayoutInflater();

			frontView = lf.inflate(R.layout.fragment_card_front, container, false);

			return frontView;
		}

		public void setText(String question) {

			TextView questionText2 = (TextView) frontView.findViewById(R.id.question_text);

			questionText2.setText(question);
		}
	}

	/**
	 * A fragment representing the back of the card.
	 */
	public static class CardBackFragment extends Fragment {
		public CardBackFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View backView = inflater.inflate(R.layout.fragment_card_back, container, false);

			return backView;
		}
	}
}
