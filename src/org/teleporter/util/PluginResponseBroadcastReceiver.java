package org.teleporter.util;

import java.util.GregorianCalendar;

import org.teleporter.adt.BaseRide;
import org.teleporter.adt.DurationRide;
import org.teleporter.adt.IntentType;
import org.teleporter.adt.Place;
import org.teleporter.adt.RideScore;
import org.teleporter.adt.RideType;
import org.teleporter.adt.TimedRide;
import org.teleporter.plugin.constants.PluginConstants;
import org.teleporter.util.constants.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Nicolas Gramlich
 * @since 09:08:15 - 29.05.2010
 */
public class PluginResponseBroadcastReceiver extends BroadcastReceiver implements Constants, PluginConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Place mStart;
	private final Place mDestination;
	private final Callback<BaseRide> mCallback;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PluginResponseBroadcastReceiver(final Place pStart, final Place pDestination, final Callback<BaseRide> pCallback) {
		this.mStart = pStart;
		this.mDestination = pDestination;
		this.mCallback = pCallback;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onReceive(final Context pContext, final Intent pIntent) {
		if(pIntent.getAction().equals(ACTION_SEARCH_RESPONSE)) {
			final Bundle extras = pIntent.getExtras();

			final String type = pIntent.getStringExtra(ACTION_SEARCH_RESPONSE_EXTRA_TYPE);
			
			final RideType rideType = RideType.fromString(extras.getString(ACTION_SEARCH_RESPONSE_EXTRA_RIDETYPE));
			final RideScore rideScore = extractRideScorefromExtras(extras);			
			final int priceInCents = extras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_PRICE, -1);
			final Intent intent = extras.getParcelable(ACTION_SEARCH_RESPONSE_EXTRA_INTENT);
			final IntentType intentType = IntentType.fromString(extras.getString(ACTION_SEARCH_RESPONSE_EXTRA_INTENTTYPE));
			
			final BaseRide ride;
			if(ACTION_SEARCH_RESPONSE_EXTRA_TYPE_VALUE_DURATIONDRIDE.equals(type)){
				ride = onReceiveDurationRide(extras, rideType, rideScore, priceInCents, intent, intentType);
			} else if(ACTION_SEARCH_RESPONSE_EXTRA_TYPE_VALUE_TIMEDRIDE.equals(type)){
				ride = onReceiveTimedRide(extras, rideType, rideScore, priceInCents, intent, intentType);
			} else {
				Log.w(DEBUGTAG, "Improper Search Response detected!");
				return;
			}
			
			this.mCallback.onCallback(ride);
		}
	}

	private static RideScore extractRideScorefromExtras(final Bundle pExtras) {
		final int fun = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_FUN);
		final int money = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_MONEY);
		final int speed = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_SPEED);
		final int ecology = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_ECOLOGY);
		final int social = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_SOCIAL);
		
		return new RideScore(fun, money, speed, ecology, social);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private DurationRide onReceiveDurationRide(final Bundle pExtras, final RideType pRideType, final RideScore pRideScore, int pPriceInCents, final Intent pIntent, final IntentType pIntentType) {
		final int durationSeconds = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_DURATION, -1);
		final int offsetSeconds = pExtras.getInt(ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_OFFSET, -1);
		
		return new DurationRide(this.mStart, this.mDestination, pRideType, pRideScore, pPriceInCents, pIntent, pIntentType, offsetSeconds, durationSeconds);
	}
	
	private TimedRide onReceiveTimedRide(final Bundle pExtras, final RideType pRideType, final RideScore pRideScore, int pPriceInCents, final Intent pIntent, final IntentType pIntentType) {
		final long arrivalTimestamp = pExtras.getLong(ACTION_SEARCH_RESPONSE_EXTRA_TIMEDRIDE_ARRIVAL, -1);
		final long departureTimestamp = pExtras.getLong(ACTION_SEARCH_RESPONSE_EXTRA_TIMEDRIDE_DEPARTURE, -1);

		final GregorianCalendar arrival = new GregorianCalendar();
		arrival.setTimeInMillis(arrivalTimestamp);
		final GregorianCalendar departure = new GregorianCalendar();
		departure.setTimeInMillis(departureTimestamp);
		
		return new TimedRide(this.mStart, this.mDestination, pRideType, pRideScore, pPriceInCents, pIntent, pIntentType, arrival, departure);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}