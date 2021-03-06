/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.setup.test.discovery

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService
import org.eclipse.smarthome.config.discovery.DiscoveryResult
import org.eclipse.smarthome.config.discovery.DiscoveryServiceInfo
import org.eclipse.smarthome.core.thing.ThingTypeUID
import org.eclipse.smarthome.core.thing.ThingUID


/**
 * The {@link DiscoveryServiceMock} is a mock for a {@link DiscoveryService}
 * which can simulate a working and faulty discovery.<br>
 * If this mock is configured to be faulty, an exception is thrown if the
 * discovery is enforced or aborted. 
 * 
 * @author Michael Grammling - Initial Contribution
 */
class DiscoveryServiceMock extends AbstractDiscoveryService {

    ThingTypeUID thingType
    int timeout
    boolean faulty

    boolean force


    public DiscoveryServiceMock(thingType, timeout, faulty = false) {
        this.thingType = thingType
        this.timeout = timeout
        this.faulty = faulty
    }

    @Override
    public DiscoveryServiceInfo getInfo() {
        return new DiscoveryServiceInfo([thingType], timeout)
    }

    @Override
    public void forceDiscovery() {
        if (faulty) {
            throw new Exception()
        }

        force = true
        thingDiscovered(new DiscoveryResult(thingType, new ThingUID(thingType, 'abc')))
        discoveryFinished()
        force = false
    }

    @Override
    public void abortForcedDiscovery() {
        if (faulty) {
            throw new Exception()
        }
    }

}
