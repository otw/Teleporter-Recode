package de.andlabs.teleporter.adt;

import de.andlabs.teleporter.R;


/**
 * @author Nicolas Gramlich
 * @since 20:49:32 - 25.05.2010
 */
public enum RideType {
	// ===========================================================
	// Elements
	// ===========================================================

	TELEPORTER(R.drawable.ridetype_teleporter),
	SKATEBOARD(R.drawable.ridetype_skateboard),
	PUBLICTRANSIT(R.drawable.ridetype_publictransit),
	FLIGHT(R.drawable.ridetype_flight),
	TRAIN(R.drawable.ridetype_train),
	DRIVE(R.drawable.ridetype_drive),
	BIKE(R.drawable.ridetype_bike),
	WALK(R.drawable.ridetype_walk),
	TAXI(R.drawable.ridetype_taxi),
	RIDESHARE(R.drawable.ridetype_rideshare);

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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
