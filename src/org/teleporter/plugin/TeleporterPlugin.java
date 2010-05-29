package org.teleporter.plugin;

import java.util.ArrayList;
import java.util.List;

import org.teleporter.adt.DurationRide;
import org.teleporter.adt.IntentType;
import org.teleporter.adt.Place;
import org.teleporter.adt.RideScore;
import org.teleporter.adt.RideType;
import org.teleporter.ui.activity.TeleporterActivity;
import org.teleporter.util.constants.Constants;
import org.teleporter.util.constants.TimeConstants;

import android.content.Context;
import android.content.Intent;

/**
 * @author Nicolas Gramlich
 * @since 21:20:55 - 25.05.2010
 */
public class TeleporterPlugin implements ITeleporterPlugin, Constants, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final RideScore TELEPORTER_RIDESCORE = new RideScore(5, 5, 5, 5, 5);
	private final Context mContext;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public TeleporterPlugin(final Context pContext) {
		this.mContext = pContext;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public List<DurationRide> find(final Place pStart, final Place pDestination, final long pTime) {
		final List<DurationRide> ridesFound = new ArrayList<DurationRide>();

		final Intent intent = new Intent(this.mContext, TeleporterActivity.class);
		ridesFound.add(new DurationRide(pStart, pDestination, RideType.TELEPORTER, TELEPORTER_RIDESCORE, 1, intent, IntentType.STARTACTIVITY, 0, 1));
		
		return ridesFound;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
