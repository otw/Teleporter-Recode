package de.andlabs.teleporter.adt;

import java.util.GregorianCalendar;

import android.content.Intent;
import de.andlabs.teleporter.util.constants.TimeConstants;

/**
 * @author Nicolas Gramlich
 * @since 21:08:31 - 25.05.2010
 */
public abstract class BaseRide implements TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final Place mStart;
	public final Place mDestination;

	public final RideType mRideType;
	public final RideScore mRideScore;

	public final int mPriceInCents;

	public final Intent mIntent;
	public final IntentType mIntentType;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseRide(final Place pStart, final Place pDestination, final RideType pRideType, final RideScore pRideScore, final int pPriceInCents, final Intent pIntent, final IntentType pIntentType) {
		this.mStart = pStart;
		this.mDestination = pDestination;
		this.mRideType = pRideType;
		this.mRideScore = pRideScore;
		this.mPriceInCents = pPriceInCents;
		this.mIntent = pIntent;
		this.mIntentType = pIntentType;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Place getStart() {
		return this.mStart;
	}

	public Place getDestination() {
		return this.mDestination;
	}

	public RideType getRideType() {
		return this.mRideType;
	}

	public RideScore getRideScore() {
		return this.mRideScore;
	}

	public int getPriceInCents() {
		return this.mPriceInCents;
	}

	public Intent getIntent() {
		return this.mIntent;
	}
	
	public IntentType getIntentType() {
		return this.mIntentType;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public abstract GregorianCalendar getDeparture();
	public abstract GregorianCalendar getArrival();
	public abstract int getDurationSeconds();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mDestination == null) ? 0 : this.mDestination.hashCode());
		result = prime * result + this.mPriceInCents;
		result = prime * result + ((this.mRideScore == null) ? 0 : this.mRideScore.hashCode());
		result = prime * result + ((this.mRideType == null) ? 0 : this.mRideType.hashCode());
		result = prime * result + ((this.mStart == null) ? 0 : this.mStart.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		final BaseRide other = (BaseRide) obj;
		if(this.mDestination == null) {
			if(other.mDestination != null) {
				return false;
			}
		} else if(!this.mDestination.equals(other.mDestination)) {
			return false;
		}
		if(this.mPriceInCents != other.mPriceInCents) {
			return false;
		}
		if(this.mRideScore == null) {
			if(other.mRideScore != null) {
				return false;
			}
		} else if(!this.mRideScore.equals(other.mRideScore)) {
			return false;
		}
		if(this.mRideType == null) {
			if(other.mRideType != null) {
				return false;
			}
		} else if(!this.mRideType.equals(other.mRideType)) {
			return false;
		}
		if(this.mStart == null) {
			if(other.mStart != null) {
				return false;
			}
		} else if(!this.mStart.equals(other.mStart)) {
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
