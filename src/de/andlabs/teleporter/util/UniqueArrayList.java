package de.andlabs.teleporter.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nicolas Gramlich
 * @since 01:14:44 - 26.05.2010
 */
public class UniqueArrayList<E> extends ArrayList<E> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 7630386715232125520L;

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
	public boolean add(final E pElement) {
		if(!this.contains(pElement)) {
			return super.add(pElement);
		} else {
			return false;
		}
	}

	@Override
	public boolean addAll(final Collection<? extends E> collection) {
		for(final E element : collection) {
			if(!this.contains(element)) {
				super.add(element);
			}
		}
		return true;
	}

	@Override
	public void add(final int pIndex, final E pElement) {
		if(!this.contains(pElement)) {
			super.add(pIndex, pElement);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
