package org.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.teleporter.adt.BaseRide;
import org.teleporter.adt.DurationRide;
import org.teleporter.adt.IntentType;
import org.teleporter.adt.Place;
import org.teleporter.adt.RideScore;
import org.teleporter.adt.RideType;
import org.teleporter.adt.TimedRide;
import org.teleporter.plugin.ITeleporterPlugin;
import org.teleporter.plugin.TeleporterPlugin;
import org.teleporter.plugin.constants.PluginConstants;
import org.teleporter.ui.activity.RidesListActivity;
import org.teleporter.util.UniqueArrayList;
import org.teleporter.util.constants.Constants;
import org.teleporter.util.constants.TimeConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.util.Log;

public class QueryMultiplexer implements Constants, PluginConstants, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Context mContext;

	private final Place mStart;
	private final Place mDestination;

	public ArrayList<BaseRide> mRides = new UniqueArrayList<BaseRide>();
	private final ArrayList<ITeleporterPlugin> mPlugins = new ArrayList<ITeleporterPlugin>();

	private final SharedPreferences mScoreFactorsSharedPreferences;
	private final RideComparator mRideComparator;

	private Thread mQueryWorkerThread;

	private long mLastQueryTime;

	private final Handler mUpdateHandler = new Handler();

	private final BroadcastReceiver mPluginResponseReceiver;

	// ===========================================================
	// Constructors
	// ===========================================================

	public QueryMultiplexer(final Context pContext, final Place pStart, final Place pDestination) {
		this.mContext = pContext;
		this.mStart = pStart;
		this.mDestination = pDestination;
		this.mScoreFactorsSharedPreferences = pContext.getSharedPreferences(PREFERENCES_FACTORS_NAME, Context.MODE_PRIVATE);
		this.mRideComparator = new RideComparator(this.mScoreFactorsSharedPreferences);

		this.mPlugins.add(new TeleporterPlugin(pContext)); // TODO <-- Dummy

		this.mPluginResponseReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context pContext, final Intent pIntent) {
				final int durationSeconds = pIntent.getIntExtra(ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_DURATION, -1);
				final int offsetSeconds = pIntent.getIntExtra(ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_OFFSET, -1);
				final RideType rideType = RideType.fromString(pIntent.getStringExtra(ACTION_SEARCH_RESPONSE_EXTRA_RIDETYPE));
				final Intent intent = pIntent.getParcelableExtra(ACTION_SEARCH_RESPONSE_EXTRA_INTENT);

				final DurationRide ride = new DurationRide(QueryMultiplexer.this.mStart, QueryMultiplexer.this.mDestination, rideType, new RideScore(1, 1, 5, 1, 1), 100, intent, IntentType.SENDBROADCAST, offsetSeconds, durationSeconds);

				QueryMultiplexer.this.mUpdateHandler.post(new Runnable() {
					@Override
					public void run() {
						QueryMultiplexer.this.mRides.add(ride);
						QueryMultiplexer.this.sort();
						((RidesListActivity)QueryMultiplexer.this.mContext).onRidesChanged(); // TODO <-- BÖÖÖSE
					}
				});
			}
		};

		this.mContext.registerReceiver(this.mPluginResponseReceiver, new IntentFilter(ACTION_SEARCH_RESPONSE));

		this.mScoreFactorsSharedPreferences.registerOnSharedPreferenceChangeListener(this.mRideComparator);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public void onDestroy() {
		this.mScoreFactorsSharedPreferences.unregisterOnSharedPreferenceChangeListener(this.mRideComparator);
		this.mContext.unregisterReceiver(this.mPluginResponseReceiver);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void searchLaterAsync() {
		if (this.mQueryWorkerThread != null && this.mQueryWorkerThread.isAlive()) {
			return;
		}

		this.mQueryWorkerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				QueryMultiplexer.this.searchLaterWorker();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Log.e(DEBUGTAG, "Error", e);
				}
			}
		});

		this.mQueryWorkerThread.start();
	}


	private void searchLaterWorker() {
		if(this.mLastQueryTime == 0) {
			this.mLastQueryTime = System.currentTimeMillis();
		} else {
			this.mLastQueryTime += 5 * SECONDSPERMINUTE * MILLISECONDSPERSECOND;
		}

		for (final ITeleporterPlugin plugin : this.mPlugins) {
			Log.d(DEBUGTAG, "Querying plugin: " + plugin);
			final List<? extends BaseRide> rides = plugin.find(this.mStart, this.mDestination, this.mLastQueryTime);

			this.mUpdateHandler.post(new Runnable() {
				@Override
				public void run() {
					QueryMultiplexer.this.mRides.addAll(rides);
					QueryMultiplexer.this.sort();
					((RidesListActivity)QueryMultiplexer.this.mContext).onRidesChanged(); // TODO <-- BÖÖÖSE
				}
			});
		}

		final Intent requestIntent = new Intent(ACTION_SEARCH_REQUEST);
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_SEARCHTIME, this.mLastQueryTime);
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_START_NAME, this.mStart.getName());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_START_ADDRESS, this.mStart.getAddress());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_START_LATITUDE, this.mStart.getLatitudeE6());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_START_LONGITUDE, this.mStart.getLongitudeE6());
		
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_NAME, this.mDestination.getName());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_ADDRESS, this.mDestination.getAddress());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_LATITUDE, this.mDestination.getLatitudeE6());
		requestIntent.putExtra(ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_LONGITUDE, this.mDestination.getLongitudeE6());
		this.mContext.sendBroadcast(requestIntent);
	}

	public void sort() {
		Collections.sort(this.mRides, this.mRideComparator);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class RideComparator implements Comparator<BaseRide>, OnSharedPreferenceChangeListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private float mFactorFun;
		private float mFactorMoney;
		private float mFactorSpeed;
		private float mFactorEcology;
		private float mFactorSocial;

		// ===========================================================
		// Constructors
		// ===========================================================

		public RideComparator(final SharedPreferences pSharedPreferences){
			this.refreshFactors(pSharedPreferences);
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		public void onSharedPreferenceChanged(final SharedPreferences pSharedPreferences, final String pKey) {
			this.refreshFactors(pSharedPreferences);
		}

		@Override
		public int compare(final BaseRide pBaseRideA, final BaseRide pBaseRideB) {
			final float scoreA = pBaseRideA.getRideScore().calculate(this.mFactorFun, this.mFactorMoney, this.mFactorSpeed, this.mFactorEcology, this.mFactorSocial);
			final float scoreB = pBaseRideB.getRideScore().calculate(this.mFactorFun, this.mFactorMoney, this.mFactorSpeed, this.mFactorEcology, this.mFactorSocial);

			if (scoreA > scoreB) {
				return -1;
			} else if (scoreA < scoreB) {
				return 1;
			} else {
				if(pBaseRideA instanceof TimedRide && pBaseRideB instanceof TimedRide) {
					return (int)Math.signum(pBaseRideA.getDeparture().getTimeInMillis() - pBaseRideB.getDeparture().getTimeInMillis());
				} else if (pBaseRideA instanceof DurationRide && pBaseRideB instanceof TimedRide) {
					return (int)Math.signum(System.currentTimeMillis() + ((DurationRide)pBaseRideA).getOffsetSeconds() * MILLISECONDSPERSECOND - pBaseRideB.getDeparture().getTimeInMillis());
				}  else if (pBaseRideA instanceof TimedRide && pBaseRideB instanceof DurationRide) {
					return (int)Math.signum(pBaseRideA.getDeparture().getTimeInMillis() - (System.currentTimeMillis() + ((DurationRide)pBaseRideB).getOffsetSeconds() * MILLISECONDSPERSECOND));
				} else {
					return (int)Math.signum(((DurationRide)pBaseRideA).getOffsetSeconds() - ((DurationRide)pBaseRideB).getOffsetSeconds());
				}
			}
		}

		// ===========================================================
		// Methods
		// ===========================================================

		private void refreshFactors(final SharedPreferences pScoreFactorsSharedPreferences) {
			this.mFactorFun = ((float)pScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_FUN, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorMoney = ((float)pScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_MONEY, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorSpeed = ((float)pScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_SPEED, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorEcology = ((float)pScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_ECOLOGY, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorSocial = ((float)pScoreFactorsSharedPreferences.getInt(PREFERENCES_SCOREFACTOR_SOCIAL, 0)) / PREFERENCES_FACTOR_MAX;
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
