package org.teleporter.adt;

import java.util.GregorianCalendar;

import android.content.Intent;

/**
 * @author Nicolas Gramlich
 * @since 21:09:01 - 25.05.2010
 */
public class DurationRide extends BaseRide {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final int mOffsetSeconds;
	private final int mDurationSeconds;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DurationRide(final Place pStart, final Place pDestination, final RideType pRideType, final RideScore pRideScore, final int pPriceInCents, final Intent pIntent, final IntentType pIntentType, final int pOffsetSeconds, final int pDurationSeconds) {
		super(pStart, pDestination, pRideType, pRideScore, pPriceInCents, pIntent, pIntentType);
		this.mOffsetSeconds = pOffsetSeconds;
		this.mDurationSeconds = pDurationSeconds;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	public int getOffsetSeconds() {
		return this.mOffsetSeconds;
	}

	@Override
	public int getDurationSeconds() {
		return this.mDurationSeconds;
	}

	@Override
	public GregorianCalendar getDeparture() {
		return null;
	}

	@Override
	public GregorianCalendar getArrival() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.mDurationSeconds;
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
		final DurationRide other = (DurationRide) obj;
		if(this.mDurationSeconds != other.mDurationSeconds) {
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
