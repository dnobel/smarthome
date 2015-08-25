/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.everything.internal;

import org.eclipse.smarthome.binding.everything.EverythingBindingConstants;
import org.eclipse.smarthome.binding.everything.handler.EverythingHandler;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * The {@link EverythingHandlerFactory} is responsible for creating things and thing 
 * handlers.
 * 
 * @author Dennis Nobel - Initial contribution
 */
public class EverythingHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return thingTypeUID.getBindingId().equals(EverythingBindingConstants.BINDING_ID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        return new EverythingHandler(thing);
    }
}

