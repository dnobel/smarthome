/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.everything.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EverythingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 * 
 * @author Dennis Nobel - Initial contribution
 */
public class EverythingHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(EverythingHandler.class);

	public EverythingHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received command: " + command);
	}

    @Override
    public void handleRemoval() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // ignore
                }
                updateStatus(ThingStatus.REMOVED);
            }
        }).start();
    }
}
