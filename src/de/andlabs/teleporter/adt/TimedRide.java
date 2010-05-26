package de.andlabs.teleporter.adt;

import java.util.GregorianCalendar;

import android.content.Intent;


/**
 * @author Nicolas Gramlich
 * @since 20:49:24 - 25.05.2010
 */
public class TimedRide extends BaseRide {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final GregorianCalendar mDeparture;
	public final GregorianCalendar mArrival;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TimedRide(final Place pStart, final Place pDestination, final RideType pRideType, final RideScore pRideScore, final int pPriceInCents, final Intent pIntent, final IntentType pIntentType, final GregorianCalendar mArrival, final GregorianCalendar mDeparture) {
		super(pStart, pDestination, pRideType, pRideScore, pPriceInCents, pIntent, pIntentType);
		this.mArrival = mArrival;
		this.mDeparture = mDeparture;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public int getDurationSeconds() {
		return (int)((this.mArrival.getTimeInMillis() - this.mDeparture.getTimeInMillis()) / MILLISECONDSPERSECOND);
	}

	@Override
	public GregorianCalendar getDeparture() {
		return this.mDeparture;
	}

	@Override
	public GregorianCalendar getArrival() {
		return this.mArrival;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.mArrival == null) ? 0 : this.mArrival.hashCode());
		result = prime * result + ((this.mDeparture == null) ? 0 : this.mDeparture.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		final TimedRide other = (TimedRide) obj;
		if(this.mArrival == null) {
			if(other.mArrival != null) {
				return false;
			}
		} else if(!this.mArrival.equals(other.mArrival)) {
			return false;
		}
		if(this.mDeparture == null) {
			if(other.mDeparture != null) {
				return false;
			}
		} else if(!this.mDeparture.equals(other.mDeparture)) {
			return false;
		}
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
