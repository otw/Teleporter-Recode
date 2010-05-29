package org.teleporter.util;

import java.util.Comparator;

import org.teleporter.adt.BaseRide;
import org.teleporter.adt.DurationRide;
import org.teleporter.adt.TimedRide;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * @author Nicolas Gramlich
 * @since 08:57:12 - 29.05.2010
 */
public class RideComparator implements Comparator<BaseRide>, OnSharedPreferenceChangeListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private float mFactorFun;
	private float mFactorMoney;
	private float mFactorSpeed;
	private float mFactorEcology;
	private float mFactorSocial;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RideComparator(final SharedPreferences pSharedPreferences){
		this.refreshFactors(pSharedPreferences);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences pSharedPreferences, final String pKey) {
		this.refreshFactors(pSharedPreferences);
	}

	@Override
	public int compare(final BaseRide pBaseRideA, final BaseRide pBaseRideB) {
		final float scoreA = pBaseRideA.getRideScore().calculate(this.mFactorFun, this.mFactorMoney, this.mFactorSpeed, this.mFactorEcology, this.mFactorSocial);
		final float scoreB = pBaseRideB.getRideScore().calculate(this.mFactorFun, this.mFactorMoney, this.mFactorSpeed, this.mFactorEcology, this.mFactorSocial);

		if (scoreA > scoreB) {
			return -1;
		} else if (scoreA < scoreB) {
			return 1;
		} else {
			if(pBaseRideA instanceof TimedRide && pBaseRideB instanceof TimedRide) {
				return (int)Math.signum(pBaseRideA.getDeparture().getTimeInMillis() - pBaseRideB.getDeparture().getTimeInMillis());
			} else if (pBaseRideA instanceof DurationRide && pBaseRideB instanceof TimedRide) {
				return (int)Math.signum(System.currentTimeMillis() + ((DurationRide)pBaseRideA).getOffsetSeconds() * QueryMultiplexer.MILLISECONDSPERSECOND - pBaseRideB.getDeparture().getTimeInMillis());
			}  else if (pBaseRideA instanceof TimedRide && pBaseRideB instanceof DurationRide) {
				return (int)Math.signum(pBaseRideA.getDeparture().getTimeInMillis() - (System.currentTimeMillis() + ((DurationRide)pBaseRideB).getOffsetSeconds() * QueryMultiplexer.MILLISECONDSPERSECOND));
			} else {
				return (int)Math.signum(((DurationRide)pBaseRideA).getOffsetSeconds() - ((DurationRide)pBaseRideB).getOffsetSeconds());
			}
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void refreshFactors(final SharedPreferences pScoreFactorsSharedPreferences) {
		this.mFactorFun = ((float)pScoreFactorsSharedPreferences.getInt(QueryMultiplexer.PREFERENCES_SCOREFACTOR_FUN, 0)) / QueryMultiplexer.PREFERENCES_FACTOR_MAX;
		this.mFactorMoney = ((float)pScoreFactorsSharedPreferences.getInt(QueryMultiplexer.PREFERENCES_SCOREFACTOR_MONEY, 0)) / QueryMultiplexer.PREFERENCES_FACTOR_MAX;
		this.mFactorSpeed = ((float)pScoreFactorsSharedPreferences.getInt(QueryMultiplexer.PREFERENCES_SCOREFACTOR_SPEED, 0)) / QueryMultiplexer.PREFERENCES_FACTOR_MAX;
		this.mFactorEcology = ((float)pScoreFactorsSharedPreferences.getInt(QueryMultiplexer.PREFERENCES_SCOREFACTOR_ECOLOGY, 0)) / QueryMultiplexer.PREFERENCES_FACTOR_MAX;
		this.mFactorSocial = ((float)pScoreFactorsSharedPreferences.getInt(QueryMultiplexer.PREFERENCES_SCOREFACTOR_SOCIAL, 0)) / QueryMultiplexer.PREFERENCES_FACTOR_MAX;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}