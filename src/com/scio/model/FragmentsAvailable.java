package com.scio.model;

/**
 * @author Eduardo Delito
 */
public enum FragmentsAvailable {
	UNKNOWN, CLIENT, CAMERA;

	public boolean shouldAddToBackStack() {
		return true;
	}

	public boolean shouldAnimate() {
		return true;
	}

	public boolean isRightOf(FragmentsAvailable fragment) {

		switch (this) {
		case CLIENT:
			return fragment == CLIENT;
		case CAMERA:
			return fragment == CAMERA;
		default:
			return false;
		}
	}

}
