package org.teleporter.adt;

/**
 * @author Nicolas Gramlich
 * @since 20:49:38 - 25.05.2010
 */
public class Place {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final int mLatitudeE6;
	public final int mLongitudeE6;
	public final String mName;
	public final String mAddress;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Place(final int pLatitudeE6, final int pLongitudeE6, final String pName, final String pAddress) {
		this.mLatitudeE6 = pLatitudeE6;
		this.mLongitudeE6 = pLongitudeE6;
		this.mName = pName;
		this.mAddress = pAddress;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public double getLatitude() {
		return getLatitudeE6() / 1E6;
	}
	
	public double getLongitude() {
		return getLongitudeE6() / 1E6;
	}
	
	public int getLatitudeE6() {
		return this.mLatitudeE6;
	}

	public int getLongitudeE6() {
		return this.mLongitudeE6;
	}

	public String getName() {
		return this.mName;
	}

	public String getAddress() {
		return this.mAddress;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mAddress == null) ? 0 : this.mAddress.hashCode());
		result = prime * result + this.mLatitudeE6;
		result = prime * result + this.mLongitudeE6;
		result = prime * result + ((this.mName == null) ? 0 : this.mName.hashCode());
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
		final Place other = (Place) obj;
		if(this.mAddress == null) {
			if(other.mAddress != null) {
				return false;
			}
		} else if(!this.mAddress.equals(other.mAddress)) {
			return false;
		}
		if(this.mLatitudeE6 != other.mLatitudeE6) {
			return false;
		}
		if(this.mLongitudeE6 != other.mLongitudeE6) {
			return false;
		}
		if(this.mName == null) {
			if(other.mName != null) {
				return false;
			}
		} else if(!this.mName.equals(other.mName)) {
			return false;
		}
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
