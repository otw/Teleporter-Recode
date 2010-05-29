package org.teleporter.ui.view;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.teleporter.R;
import org.teleporter.adt.BaseRide;
import org.teleporter.adt.DurationRide;
import org.teleporter.adt.TimedRide;
import org.teleporter.util.constants.TimeConstants;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Nicolas Gramlich
 * @since 22:29:47 - 25.05.2010
 */
public class RideView extends RelativeLayout implements TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final SimpleDateFormat DATEFORMAT_hhmm = new SimpleDateFormat(HH:mm");
	private static final DecimalFormat DECIMALFORMAT_PRICE = new DecimalFormat("#,##0.00");

	// ===========================================================
	// Fields
	// ===========================================================

	private TextView mTvDeparture;
	private TextView mTvArrival;
	private TextView mTvDuration;
	private TextView mTvPrice;
	private TextView mTvMinutes;
	private TextView mTvHours;
	private TextView mTvHoursLabel;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RideView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.mTvDeparture = (TextView) this.findViewById(R.id.tv_rideview_departure);
		this.mTvArrival = (TextView) this.findViewById(R.id.tv_rideview_arrival);
		this.mTvPrice = (TextView) this.findViewById(R.id.tv_rideview_price);
		this.mTvHours = (TextView) this.findViewById(R.id.tv_rideview_hours);
		this.mTvMinutes = (TextView) this.findViewById(R.id.tv_rideview_minutes);
		this.mTvDuration = (TextView) this.findViewById(R.id.tv_rideview_duration);
		this.mTvHoursLabel = (TextView) this.findViewById(R.id.tv_rideview_hours_label);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public void setRide(final BaseRide pBaseRide) {
		final int waitingTimeInMinutes;
		final int travelTimeInMinutes;

		if(pBaseRide instanceof DurationRide) {
			waitingTimeInMinutes = ((DurationRide) pBaseRide).getOffsetSeconds() / SECONDSPERMINUTE;
			travelTimeInMinutes = pBaseRide.getDurationSeconds() / SECONDSPERMINUTE;

			final GregorianCalendar calculatedDeparture = new GregorianCalendar();
			calculatedDeparture.add(Calendar.MINUTE, waitingTimeInMinutes);
			this.mTvDeparture.setText(RideView.DATEFORMAT_hhmm.format(calculatedDeparture.getTime()));

			final GregorianCalendar calculatedArrival = new GregorianCalendar();
			calculatedArrival.setTimeInMillis(System.currentTimeMillis() + pBaseRide.getDurationSeconds() * MILLISECONDSPERSECOND + waitingTimeInMinutes * SECONDSPERMINUTE * MILLISECONDSPERSECOND);
			this.mTvArrival.setText(RideView.DATEFORMAT_hhmm.format(calculatedArrival.getTime()));
		} else if(pBaseRide instanceof TimedRide) {
			waitingTimeInMinutes = (int) (pBaseRide.getDeparture().getTimeInMillis() - System.currentTimeMillis()) / (SECONDSPERMINUTE * MILLISECONDSPERSECOND);
			travelTimeInMinutes = (int) (pBaseRide.getArrival().getTimeInMillis() - pBaseRide.getDeparture().getTimeInMillis()) / (SECONDSPERMINUTE * MILLISECONDSPERSECOND);
			this.mTvDeparture.setText(RideView.DATEFORMAT_hhmm.format(pBaseRide.getDeparture().getTime()));
			this.mTvArrival.setText(RideView.DATEFORMAT_hhmm.format(pBaseRide.getArrival().getTime()));
		} else {
			throw new IllegalArgumentException("Unexpected class: " + pBaseRide.getClass().getSimpleName());
		}

		if(waitingTimeInMinutes < MINUTESPERHOUR) {
			this.mTvMinutes.setText(String.valueOf(waitingTimeInMinutes));
			this.mTvMinutes.setTextSize(46); // TODO And what about other resolutions ?
			this.mTvHours.setVisibility(GONE);
			this.mTvHoursLabel.setVisibility(GONE);
		} else if(waitingTimeInMinutes < 10 * MINUTESPERHOUR) {
			this.mTvMinutes.setText(String.valueOf(waitingTimeInMinutes % MINUTESPERHOUR));
			this.mTvMinutes.setTextSize(20); // TODO And what about other resolutions ?
			this.mTvHours.setText(String.valueOf(waitingTimeInMinutes / MINUTESPERHOUR));
			this.mTvHours.setVisibility(VISIBLE);
			this.mTvHoursLabel.setVisibility(VISIBLE);
		} else if(waitingTimeInMinutes < 100 * MINUTESPERHOUR) {
			this.mTvHours.setText(String.valueOf(waitingTimeInMinutes / MINUTESPERHOUR));
			this.mTvHours.setVisibility(VISIBLE);
			this.mTvHoursLabel.setVisibility(VISIBLE);
		} else {
			// TODO
		}

		if(travelTimeInMinutes < MINUTESPERHOUR) {
			this.mTvDuration.setText(travelTimeInMinutes + " min");
		} else if(travelTimeInMinutes < HOURSPERDAY * MINUTESPERHOUR) {
			this.mTvDuration.setText((travelTimeInMinutes / MINUTESPERHOUR) + "h " + travelTimeInMinutes % MINUTESPERHOUR + "min");
		} else {
			this.mTvDuration.setText("toooooo long!!!");
		}

		final int priceInCents = pBaseRide.getPriceInCents();
		this.mTvPrice.setText(DECIMALFORMAT_PRICE.format(priceInCents));

		this.setBackgroundResource(pBaseRide.getRideType().BACKGROUND_RESID);
	}
}
