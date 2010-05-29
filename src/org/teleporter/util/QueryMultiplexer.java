package org.teleporter.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.teleporter.adt.BaseRide;
import org.teleporter.adt.Place;
import org.teleporter.plugin.ITeleporterPlugin;
import org.teleporter.plugin.TeleporterPlugin;
import org.teleporter.plugin.constants.PluginConstants;
import org.teleporter.ui.activity.RidesListActivity;
import org.teleporter.util.constants.Constants;
import org.teleporter.util.constants.TimeConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * @author Nicolas Gramlich
 * @since 08:59:20 - 29.05.2010
 */
public class QueryMultiplexer implements Constants, PluginConstants, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final IntentFilter ACTION_SEARCH_RESPONSE_INTENT_FILTER = new IntentFilter(ACTION_SEARCH_RESPONSE);

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

		/* The TeleporterPlugin is a nice gimmick, so we always add it. */
		this.mPlugins.add(new TeleporterPlugin(pContext));

		this.mPluginResponseReceiver = new PluginResponseBroadcastReceiver(this.mStart, this.mDestination, new Callback<BaseRide>() {
			@Override
			public void onCallback(final BaseRide pCallbackValue) {
				QueryMultiplexer.this.mUpdateHandler.post(new Runnable() {
					@Override
					public void run() {
						QueryMultiplexer.this.mRides.add(pCallbackValue);
						QueryMultiplexer.this.sort();
						((RidesListActivity)QueryMultiplexer.this.mContext).onRidesChanged(); // TODO <-- Evil !!!
					}
				});
			}
		});
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public void onPause() {
		this.mScoreFactorsSharedPreferences.unregisterOnSharedPreferenceChangeListener(this.mRideComparator);
		this.mContext.unregisterReceiver(this.mPluginResponseReceiver);
	}

	public void onResume() {
		this.mScoreFactorsSharedPreferences.registerOnSharedPreferenceChangeListener(this.mRideComparator);
		this.mContext.registerReceiver(this.mPluginResponseReceiver, ACTION_SEARCH_RESPONSE_INTENT_FILTER);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void sort() {
		Collections.sort(this.mRides, this.mRideComparator);
	}

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
				} catch (final InterruptedException e) {
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
