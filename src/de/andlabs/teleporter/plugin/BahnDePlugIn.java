package de.andlabs.teleporter.plugin;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import de.andlabs.teleporter.adt.IntentType;
import de.andlabs.teleporter.adt.Place;
import de.andlabs.teleporter.adt.RideScore;
import de.andlabs.teleporter.adt.RideType;
import de.andlabs.teleporter.adt.TimedRide;
import de.andlabs.teleporter.util.constants.Constants;
import de.andlabs.teleporter.util.constants.TimeConstants;

/**
 * @author Nicolas Gramlich
 * @since 21:20:55 - 25.05.2010
 */
public class BahnDePlugIn implements ITeleporterPlugIn, Constants, TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final SimpleDateFormat DATEFORMAT_ddMMyy = new SimpleDateFormat("ddMMyy");
	private static final SimpleDateFormat DATEFORMAT_HHmm = new SimpleDateFormat("HHmm");

	private static final RideScore DB_RIDESCORE = new RideScore(3, 3, 1, 2, 4);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public List<TimedRide> find(final Place pStart, final Place pDestination, final long pTime) {
		final List<TimedRide> ridesFound = new ArrayList<TimedRide>();

		final Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("http");
		uriBuilder.authority("mobile.bahn.de");
		uriBuilder.path("/bin/mobil/query.exe/dox");

		uriBuilder.appendQueryParameter("n", "1");
		switch(pStart.mPlaceType) {
			case ADDRESS:
				uriBuilder.appendQueryParameter("f", "2").appendQueryParameter("s", pStart.getAddress());
				break;
			case STATION:
				uriBuilder.appendQueryParameter("f", "1").appendQueryParameter("s", pStart.getName() + ", " + pStart.getAddress());
				break;
		}
		switch(pDestination.mPlaceType) {
			case ADDRESS:
				uriBuilder.appendQueryParameter("o", "2").appendQueryParameter("z", pDestination.getAddress());
				break;
			case STATION:
				uriBuilder.appendQueryParameter("o", "1").appendQueryParameter("z", pDestination.getName() + ", " + pDestination.getAddress());
				break;
		}
		uriBuilder.appendQueryParameter("d", DATEFORMAT_ddMMyy.format(new Date(pTime)));
		uriBuilder.appendQueryParameter("t", DATEFORMAT_HHmm.format(new Date(pTime)));
		uriBuilder.appendQueryParameter("start", "Suchen");
		final String uri = uriBuilder.build().toString();
		Log.d(DEBUGTAG, "url: " + uri);

		try {
			final AbstractHttpClient httpClient = new DefaultHttpClient();
			final InputStream content = httpClient.execute(new HttpGet(uri)).getEntity().getContent();

			final Scanner scanner = new Scanner(content, "iso-8859-1");

			while(scanner.findWithinHorizon("<a href=\"([^\"]*)\">(\\d\\d):(\\d\\d)<br />(\\d\\d):(\\d\\d)", 10000) != null) {
				final MatchResult m = scanner.match();
				Log.d(DEBUGTAG, "Found match: " + m);
				final GregorianCalendar departure = parseDate(m.group(2), m.group(3));
				final GregorianCalendar arrival = parseDate(m.group(4), m.group(5));
				final int price = 240; // TODO Actual price

				if(departure.getTimeInMillis() - pTime > 100000) { // TODO Was sind die 100000 ?
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http:" + m.group(1).replace("&amp;", "&")));
					final TimedRide r = new TimedRide(pStart, pDestination, RideType.PUBLICTRANSIT, DB_RIDESCORE, price, intent, IntentType.STARTACTIVITY, arrival, departure);
					ridesFound.add(r);
				}
			}
		} catch (final Exception e) {
			Log.e(DEBUGTAG, "Mist!", e);
		}
		return ridesFound;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private static GregorianCalendar parseDate(final String pHours, final String pMinutes) {
		final GregorianCalendar out = new GregorianCalendar();
		out.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pHours));
		out.set(Calendar.MINUTE, Integer.parseInt(pMinutes));
		out.set(Calendar.SECOND, 0);
		out.set(Calendar.MILLISECOND, (out.get(Calendar.MILLISECOND) / MILLISECONDSPERSECOND) * MILLISECONDSPERSECOND);

		// TODO Überprüfen
		// TODO Was sind die 36000000 ? --> Use TimeConstants
		if(System.currentTimeMillis() - out.getTimeInMillis() > 36000 * MILLISECONDSPERSECOND) { // Mitternacht..
			out.add(Calendar.DAY_OF_MONTH, 1);
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
