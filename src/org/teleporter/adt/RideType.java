package org.teleporter.adt;

import org.teleporter.R;


/**
 * @author Nicolas Gramlich
 * @since 20:49:32 - 25.05.2010
 */
public enum RideType {
	// ===========================================================
	// Elements
	// ===========================================================

	TELEPORTER(R.drawable.ridetype_teleporter),
	AIRPLANE(R.drawable.ridetype_airplane),
	CAR(R.drawable.ridetype_car),
	PUBLICTRANSIT(R.drawable.ridetype_publictransit),
	TRAIN(R.drawable.ridetype_train),
	BIKE(R.drawable.ridetype_bike),
	PEDESTRIAN(R.drawable.ridetype_pedestrian),
	TAXI(R.drawable.ridetype_taxi),
	RIDESHARE(R.drawable.ridetype_rideshare),
	UNKNOWN(R.drawable.ridetype_selector); // TODO <-- new image

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final int BACKGROUND_RESID;

	// ===========================================================
	// Constructors
	// ===========================================================;

	private RideType(final int pBackgroundResID) {
		this.BACKGROUND_RESID = pBackgroundResID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static RideType fromString(final String pName) {
		final RideType[] rideTypes = RideType.values();
		for(final RideType r : rideTypes) {
			if(r.name().equals(pName)) {
				return r;
			}
		}

		return RideType.UNKNOWN;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
