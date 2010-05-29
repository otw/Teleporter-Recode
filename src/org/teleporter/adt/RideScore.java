package org.teleporter.adt;

/**
 * @author Nicolas Gramlich
 * @since 20:52:23 - 25.05.2010
 */
public class RideScore {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final int mFun;
	public final int mMoney;
	public final int mSpeed;
	public final int mEcology;
	public final int mSocial;

	// ===========================================================
	// Constructors
	// ===========================================================

	public RideScore(final int pFun, final int pMoney, final int pSpeed, final int pEcology, final int pSocial) {
		this.mFun = pFun;
		this.mMoney = pMoney;
		this.mSpeed = pSpeed;
		this.mEcology = pEcology;
		this.mSocial = pSocial;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getFun() {
		return this.mFun;
	}

	public int getMoney() {
		return this.mMoney;
	}

	public int getSpeed() {
		return this.mSpeed;
	}

	public int getEcology() {
		return this.mEcology;
	}

	public int getSocial() {
		return this.mSocial;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.mEcology;
		result = prime * result + this.mFun;
		result = prime * result + this.mMoney;
		result = prime * result + this.mSocial;
		result = prime * result + this.mSpeed;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		final RideScore other = (RideScore) obj;
		if(this.mEcology != other.mEcology) {
			return false;
		}
		if(this.mFun != other.mFun) {
			return false;
		}
		if(this.mMoney != other.mMoney) {
			return false;
		}
		if(this.mSocial != other.mSocial) {
			return false;
		}
		if(this.mSpeed != other.mSpeed) {
			return false;
		}
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public float calculate(final float pFactorFun, final float pFactorMoney, final float pFactorSpeed, final float pFactorEcology, final float pFactorSocial) {
		return this.mFun * pFactorEcology
		+ this.mMoney * pFactorMoney
		+ this.mSpeed * pFactorSpeed
		+ this.mEcology * pFactorEcology
		+ this.mSocial * pFactorSocial;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
