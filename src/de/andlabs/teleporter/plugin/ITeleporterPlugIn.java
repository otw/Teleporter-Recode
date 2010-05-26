package de.andlabs.teleporter.plugin;

import java.util.List;

import de.andlabs.teleporter.adt.BaseRide;
import de.andlabs.teleporter.adt.Place;

/**
 * @author Nicolas Gramlich
 * @since 21:22:50 - 25.05.2010
 */
public interface ITeleporterPlugIn {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public List<? extends BaseRide> find(final Place pStart, final Place pDestination, final long pTime);
}