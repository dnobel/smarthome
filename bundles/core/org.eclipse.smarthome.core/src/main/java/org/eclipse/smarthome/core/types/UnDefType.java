/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.types;

/**
 * There are situations when item states do not have any defined value.
 * This might be because they have not been initialized yet (never
 * received an state update so far) or because their state is ambiguous
 * (e.g. a dimmed light that is treated as a switch (ON/OFF) will have
 * an undefined state if it is dimmed to 50%).
 * 
 * @author Kai Kreuzer - Initial contribution and API
 *
 */
public enum UnDefType implements PrimitiveType, State {
	UNDEF, NULL;
	
	public String toString() {
		switch(this) {
			case UNDEF: return "Undefined";
			case NULL:  return "Uninitialized";
		}
		return "";
	}
	
	public String format(String pattern) {
		return String.format(pattern, this.toString());
	}

}
