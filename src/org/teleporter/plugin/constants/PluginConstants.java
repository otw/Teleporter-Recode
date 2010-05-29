package org.teleporter.plugin.constants;


/**
 * @author Nicolas Gramlich
 * @since 16:51:47 - 27.05.2010
 */
public interface PluginConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String ACTION_SEARCH_REQUEST = "org.teleporter.intent.action.SEARCH_REQUEST";
	public static final String ACTION_SEARCH_RESPONSE = "org.teleporter.intent.action.SEARCH_RESPONSE";

	public static final String ACTION_SEARCH_REQUEST_EXTRA_SEARCHTIME = ACTION_SEARCH_REQUEST + ".extra.searchtime";
	
	public static final String ACTION_SEARCH_REQUEST_EXTRA_START_NAME = ACTION_SEARCH_REQUEST + ".extra.start.name";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_START_ADDRESS = ACTION_SEARCH_REQUEST + ".extra.start.address";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_START_LATITUDE = ACTION_SEARCH_REQUEST + ".extra.start.latitude";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_START_LONGITUDE = ACTION_SEARCH_REQUEST + ".extra.start.longitude";
	
	public static final String ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_NAME = ACTION_SEARCH_REQUEST + ".extra.destination.name";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_ADDRESS = ACTION_SEARCH_REQUEST + ".extra.destination.address";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_LATITUDE = ACTION_SEARCH_REQUEST + ".extra.destination.latitude";
	public static final String ACTION_SEARCH_REQUEST_EXTRA_DESTINATION_LONGITUDE = ACTION_SEARCH_REQUEST + ".extra.destination.longitude";


	public static final String ACTION_SEARCH_RESPONSE_EXTRA_PRICE = ACTION_SEARCH_RESPONSE + ".extra.price";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_INTENT = ACTION_SEARCH_RESPONSE + ".extra.intent";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDETYPE = ACTION_SEARCH_RESPONSE + ".extra.ridetype";
	
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_FUN = ACTION_SEARCH_RESPONSE + ".extra.ridescore.fun";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_MONEY = ACTION_SEARCH_RESPONSE + ".extra.ridescore.money";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_SPEED = ACTION_SEARCH_RESPONSE + ".extra.ridescore.speed";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_ECOLOGY = ACTION_SEARCH_RESPONSE + ".extra.ridescore.ecology";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_RIDESCORE_SOCIAL = ACTION_SEARCH_RESPONSE + ".extra.ridescore.social";
	
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_DURATION = ACTION_SEARCH_RESPONSE + ".extra.durationride.duration";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_DURATIONRIDE_OFFSET = ACTION_SEARCH_RESPONSE + ".extra.durationride.offset";
	
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_TIMEDRIDE_DEPARTURE = ACTION_SEARCH_RESPONSE + ".extra.timedride.departure";
	public static final String ACTION_SEARCH_RESPONSE_EXTRA_TIMEDRIDE_ARRIVAL = ACTION_SEARCH_RESPONSE + ".extra.timedride.arrival";

	// ===========================================================
	// Methods
	// ===========================================================
}
