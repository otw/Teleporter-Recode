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

	TELEPORTER(R.drawable.ridetype_teleporter, 		new RideScore(5, 5, 5, 5, 5)),
	AIRPLANE(R.drawable.ridetype_airplane, 			new RideScore(2, 0, 5, 0, 1)),
	CAR(R.drawable.ridetype_car, 					new RideScore(1, 2, 4, 2, 2)),
	PUBLICTRANSIT(R.drawable.ridetype_publictransit, new RideScore(1, 3, 3, 4, 2)),
	TRAIN(R.drawable.ridetype_train, 				new RideScore(2, 2, 4, 4, 2)),
	BIKE(R.drawable.ridetype_bike, 					new RideScore(3, 5, 2, 5, 1)),
	PEDESTRIAN(R.drawable.ridetype_pedestrian, 		new RideScore(3, 5, 1, 5, 1)),
	TAXI(R.drawable.ridetype_taxi, 					new RideScore(2, 1, 4, 2, 2)),
	RIDESHARE(R.drawable.ridetype_rideshare, 		new RideScore(3, 4, 3, 4, 5)),
	UNKNOWN(R.drawable.ridetype_selector, 			new RideScore(0, 0, 0, 0, 0)); // TODO <-- new image

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final int BACKGROUND_RESID;
	public final RideScore RIDESCORE_DEFAULT;

	// ===========================================================
	// Constructors
	// ===========================================================;

	private RideType(final int pBackgroundResID, final RideScore pDefaultRideScore) {
		this.BACKGROUND_RESID = pBackgroundResID;
		this.RIDESCORE_DEFAULT = pDefaultRideScore;
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
