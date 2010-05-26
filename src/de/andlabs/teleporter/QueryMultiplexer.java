package de.andlabs.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import de.andlabs.teleporter.adt.BaseRide;
import de.andlabs.teleporter.adt.DurationRide;
import de.andlabs.teleporter.adt.IntentType;
import de.andlabs.teleporter.adt.Place;
import de.andlabs.teleporter.adt.RideScore;
import de.andlabs.teleporter.adt.RideType;
import de.andlabs.teleporter.adt.TimedRide;
import de.andlabs.teleporter.plugin.BahnDePlugIn;
import de.andlabs.teleporter.plugin.ITeleporterPlugIn;
import de.andlabs.teleporter.util.UniqueArrayList;
import de.andlabs.teleporter.util.constants.Constants;
import de.andlabs.teleporter.util.constants.TimeConstants;

public class QueryMultiplexer implements Constants, TimeConstants {
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
	private final ArrayList<ITeleporterPlugIn> mPlugins = new ArrayList<ITeleporterPlugIn>();

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

//		final SharedPreferences pluginSettings = pContext.getSharedPreferences(PREFERENCES_PLUGINS_NAME, Context.MODE_PRIVATE);
		try {
			this.mPlugins.add(new BahnDePlugIn()); // TODO <-- Dummy adden

			//			for (final String pluginClassName : pluginSettings.getAll().keySet()) {
			//				Log.d(DEBUGTAG, "Plugin found: " + pluginClassName);
			//				if (pluginSettings.getBoolean(pluginClassName, false)) {
			//					final String pluginFullyQualifiedName = "de.andlabs.teleporter.plugin." + pluginClassName;
			//					Log.d(DEBUGTAG, "Adding plugin: " + pluginFullyQualifiedName);
			//					final ITeleporterPlugIn pluginInstance = (ITeleporterPlugIn) Class.forName(pluginFullyQualifiedName).newInstance();
			//					this.mPlugins.add(pluginInstance);
			//				}
			//			}
		} catch (final Exception e) {
			Log.e(DEBUGTAG, "Schade!", e);
		}

		this.mPluginResponseReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context pContext, final Intent pIntent) {
				Log.d(DEBUGTAG, "Plugin Response Received.");
				final int duration = pIntent.getIntExtra("dur", -1);

				// TODO ACHTUNG AB HIER AUF ANDNAV2 ANGEPASST
				{
					final String ANDNAV2_NAV_ACTION = "org.andnav2.intent.ACTION_NAV_TO";

					final Intent intent = new Intent(ANDNAV2_NAV_ACTION);

					// Create a bundle that will transfer the routing-information
					final Bundle b = new Bundle();
					b.putString("to", "" + QueryMultiplexer.this.mStart.getLatitude() + "," + QueryMultiplexer.this.mStart.getLongitude());			        
					intent.putExtras(b);

					final DurationRide ride = new DurationRide(QueryMultiplexer.this.mStart, QueryMultiplexer.this.mDestination, RideType.DRIVE, new RideScore(1, 1, 5, 1, 1), 100, intent, IntentType.SENDBROADCAST, duration);

					QueryMultiplexer.this.mUpdateHandler.post(new Runnable() {
						@Override
						public void run() {
							QueryMultiplexer.this.mRides.add(ride);
							QueryMultiplexer.this.sort();
							((RidesActivity)QueryMultiplexer.this.mContext).onRidesChanged(); // TODO <-- BÖÖÖSE
						}
					});
				}
			}
		};

		this.mContext.registerReceiver(this.mPluginResponseReceiver, new IntentFilter("org.teleporter.intent.action.RECEIVE_RESPONSE"));

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

		for (final ITeleporterPlugIn plugin : this.mPlugins) {
			Log.d(DEBUGTAG, "Querying plugin: " + plugin);
			final List<? extends BaseRide> rides = plugin.find(this.mStart, this.mDestination, this.mLastQueryTime);

			this.mUpdateHandler.post(new Runnable() {
				@Override
				public void run() {
					QueryMultiplexer.this.mRides.addAll(rides);
					QueryMultiplexer.this.sort();
					((RidesActivity)QueryMultiplexer.this.mContext).onRidesChanged(); // TODO <-- BÖÖÖSE
				}
			});
		}

		final Intent requestIntent = new Intent("org.teleporter.intent.action.RECEIVE_REQUEST");
		requestIntent.putExtra("origLatitude", this.mStart.getLatitudeE6());
		requestIntent.putExtra("origLongitude", this.mStart.getLongitudeE6());
		requestIntent.putExtra("destLatitude", this.mDestination.getLatitudeE6());
		requestIntent.putExtra("destLongitude", this.mDestination.getLongitudeE6());
		this.mContext.sendBroadcast(requestIntent);
	}

	public void sort() {
		Collections.sort(this.mRides, this.mRideComparator);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class RideComparator implements Comparator<BaseRide>, OnSharedPreferenceChangeListener {
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
					if (pBaseRideA.getDeparture().before(pBaseRideB.getDeparture())) {
						return -1;
					} else if (pBaseRideA.getDeparture().before(pBaseRideB.getDeparture())) {
						return 1;
					} else {
						return 0;
					}
				} else if (pBaseRideA instanceof DurationRide) {
					return -1;
				}  else if (pBaseRideB instanceof DurationRide) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		// ===========================================================
		// Methods
		// ===========================================================

		private void refreshFactors(final SharedPreferences pSharedPreferences) {
			final SharedPreferences factors = QueryMultiplexer.this.mScoreFactorsSharedPreferences;

			this.mFactorFun = ((float)factors.getInt(PREFERENCES_SCOREFACTOR_FUN, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorMoney = ((float)factors.getInt(PREFERENCES_SCOREFACTOR_MONEY, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorSpeed = ((float)factors.getInt(PREFERENCES_SCOREFACTOR_SPEED, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorEcology = ((float)factors.getInt(PREFERENCES_SCOREFACTOR_ECOLOGY, 0)) / PREFERENCES_FACTOR_MAX;
			this.mFactorSocial = ((float)factors.getInt(PREFERENCES_SCOREFACTOR_SOCIAL, 0)) / PREFERENCES_FACTOR_MAX;
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
