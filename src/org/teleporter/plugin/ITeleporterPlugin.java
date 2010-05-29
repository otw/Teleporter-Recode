package org.teleporter.plugin;

import java.util.List;

import org.teleporter.adt.BaseRide;
import org.teleporter.adt.Place;


/**
 * @author Nicolas Gramlich
 * @since 21:22:50 - 25.05.2010
 */
public interface ITeleporterPlugin {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public List<? extends BaseRide> find(final Place pStart, final Place pDestination, final long pTime);
}