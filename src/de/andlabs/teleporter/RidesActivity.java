package de.andlabs.teleporter;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.andlabs.teleporter.adt.BaseRide;
import de.andlabs.teleporter.adt.Place;
import de.andlabs.teleporter.adt.PlaceType;
import de.andlabs.teleporter.util.constants.Constants;
/**
 * @author Nicolas Gramlich
 * @since 23:05:12 - 25.05.2010
 */
public class RidesActivity extends ListActivity implements Constants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private SharedPreferences mScoreFactorsSharedPreferences;
	private QueryMultiplexer mQueryMultiplexer;
	private final OnFactorSliderChangedListener mOnFactorSliderChangedListener = new OnFactorSliderChangedListener();
	private View mLoadingView;
	private final BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context pContext, final Intent pIntent) {
			RidesActivity.this.getListView().invalidateViews();
		}
	};
	private RidesAdapter mRidesAdapter;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.rideslist);

		final Place start = new Place(52457577, 13292519, PlaceType.ADDRESS, "Droidcamp - Dahlem Cube", "Takustraﬂe 39, Berlin");
		final Place destination = new Place(52512288, 13419910, PlaceType.ADDRESS, "C-Base Raumstation", "Rungestr 20, Berlin");

		this.mQueryMultiplexer = new QueryMultiplexer(this, start, destination);

		this.mRidesAdapter = new RidesAdapter();
		this.setListAdapter(this.mRidesAdapter);

		final SeekBar funSeekBar = ((SeekBar)this.findViewById(R.id.seekbar_scorefactor_fun));
		final SeekBar moneySeekBar = ((SeekBar)this.findViewById(R.id.seekbar_scorefactor_money));
		final SeekBar speedSeekBar = ((SeekBar)this.findViewById(R.id.seekbar_scorefactor_speed));
		final SeekBar ecologySeekBar = ((SeekBar)this.findViewById(R.id.seekbar_scorefactor_ecology));
		final SeekBar socialSeekBar = ((SeekBar)this.findViewById(R.id.seekbar_scorefactor_social));

		funSeekBar.setOnSeekBarChangeListener(this.mOnFactorSliderChangedListener);
		moneySeekBar.setOnSeekBarChangeListener(this.mOnFactorSliderChangedListener);
		speedSeekBar.setOnSeekBarChangeListener(this.mOnFactorSliderChangedListener);
		ecologySeekBar.setOnSeekBarChangeListener(this.mOnFactorSliderChangedListener);
		socialSeekBar.setOnSeekBarChangeListener(this.mOnFactorSliderChangedListener);

		this.mScoreFactorsSharedPreferences = this.getSharedPreferences(PREFERENCES_FACTORS_NAME, MODE_PRIVATE);

		funSeekBar.setProgress(this.mScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_FUN, 0));
		moneySeekBar.setProgress(this.mScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_MONEY, 0));
		speedSeekBar.setProgress(this.mScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_SPEED, 0));
		ecologySeekBar.setProgress(this.mScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_ECOLOGY, 0));
		socialSeekBar.setProgress(this.mScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_SOCIAL, 0));

		this.mQueryMultiplexer.sort();

		this.mLoadingView = LayoutInflater.from(this).inflate(R.layout.loadingview, null);
		final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(600);
		rotateAnimation.setRepeatMode(Animation.RESTART);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		this.mLoadingView.findViewById(R.id.iv_loadingview_loading).startAnimation(rotateAnimation);
	}

	@Override
	protected void onListItemClick(final ListView pListView, final View pView, final int pPosition, final long pID) {
		final BaseRide baseRide = this.mRidesAdapter.getItem(pPosition);
		switch(baseRide.getIntentType()) {
			case STARTACTIVITY:
				this.startActivity(baseRide.getIntent());
				break;
			case SENDBROADCAST:
				this.sendBroadcast(baseRide.getIntent());
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		this.registerReceiver(this.mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
	}

	@Override
	protected void onPause() {
		super.onPause();

		this.unregisterReceiver(this.mTimeTickReceiver);
	}

	@Override
	protected void onDestroy() {
		this.mQueryMultiplexer.onDestroy();
		super.onDestroy();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onRidesChanged() {
		this.getListView().invalidateViews();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class RidesAdapter extends BaseAdapter {
		@Override
		public View getView(final int pPosition, View pView, final ViewGroup pParent) {
			if (pView == null) {
				pView = RidesActivity.this.getLayoutInflater().inflate(R.layout.rideview, pParent, false);
			}
			if (pPosition < RidesActivity.this.mQueryMultiplexer.mRides.size()) {
				((RideView)pView).setRide(RidesActivity.this.mQueryMultiplexer.mRides.get(pPosition));
				return pView;
			} else {
				RidesActivity.this.mQueryMultiplexer.searchLaterAsync();
				this.notifyDataSetChanged();
				return RidesActivity.this.mLoadingView;
			}
		}

		@Override
		public long getItemId(final int position) {
			if (position < RidesActivity.this.mQueryMultiplexer.mRides.size()) {
				return RidesActivity.this.mQueryMultiplexer.mRides.get(position).hashCode();
			} else {
				return -1;
			}
		}

		@Override
		public BaseRide getItem(final int pPosition) {
			return RidesActivity.this.mQueryMultiplexer.mRides.get(pPosition);
		}

		@Override
		public int getCount() {
			return RidesActivity.this.mQueryMultiplexer.mRides.size() + 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public int getItemViewType(final int pPosition) {
			if (pPosition < RidesActivity.this.mQueryMultiplexer.mRides.size()) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}
	}

	private class OnFactorSliderChangedListener implements OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(final SeekBar pSeekBar, final int pProgress, final boolean pFromUser) {
			RidesActivity.this.mScoreFactorsSharedPreferences.edit().putInt((String)pSeekBar.getTag(), pProgress).commit();
			RidesActivity.this.mQueryMultiplexer.sort();
			RidesActivity.this.getListView().invalidateViews();
		}

		@Override
		public void onStartTrackingTouch(final SeekBar pSeekBar) { }

		@Override
		public void onStopTrackingTouch(final SeekBar pSeekBar) { }
	}
}
