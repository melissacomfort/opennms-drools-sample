/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2013 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2013 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package com.example.sample.drools;

import org.junit.Test;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

public class BatteryDegradeHoldDownRulesTest extends CorrelationRulesTestCase {

    /**
     * The hold down time for cucsFaultActiveNotifMINOR is 15 minutes
     */
    private static Integer BDG_HOLDDOWN_TIME = 20000; //900000;

    /**
     * resend storage battery downgrade the alarm with increase the severity level .
     */
    private static String CUCS_FAULT_ACTIVE_NOTIF_MINOR__PAST_HOLDDOWN_UEI = "uei.opennms.org/vendor/Cisco/correlator/cucsFaultActiveNotifRAID_BATTERY_DEGRADED";

    /**
     * Test hold down rules.
     *
     * @throws Exception the exception
     */
    @Test
    public void testHoldDownRules() throws Exception  {
        testHoldDownTimerExpiredRules("cucsFaultActiveNotifPastHoldDownTimeRules");
    }
    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testHoldDownTimerExpiredRules(String engineName) throws InterruptedException{
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(CUCS_FAULT_ACTIVE_NOTIF_MINOR__PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.addParam("cucsFaultIndex", "0123456789");
        bldr.addParam("cucsFaultCode", "997");
        bldr.addParam( "holdDownTime", "204" );

        anticipate(bldr.getEvent());

        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarmFailedEvent(1);
        System.err.println("SENDING cucsFaultActiveNotifMINOR EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at substantially zero seconds
        event = createAlarmFailedEvent(2);
        System.err.println("SENDING cucsFaultActiveNotifMINOR EVENT FOR NODE TWO!!");
        engine.correlate(event);


        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (BDG_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(BDG_HOLDDOWN_TIME / 2);

        // Node two comes back up at halftime
        event = createAlarmClearEvent(2);
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (BDG_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(BDG_HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createAlarmClearEvent(1);
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE ONE BUT IT IS TOO LATE!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }

    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createAlarmFailedEvent(int nodeid) {
        return createScvNameEvent("uei.opennms.org/vendor/Cisco/traps/cucsFaultActiveNotifMINOR", nodeid);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createAlarmClearEvent(int nodeid) {
        return createScvNameEvent("uei.opennms.org/vendor/Cisco/traps/cucsFaultClearNotifCLEAR", nodeid);
    }

    /**
     * Creates the service event.
     *
     * @param uei     the event UEI
     * @param nodeid  the Node ID
     * @return the event
     */
    private Event createScvNameEvent(String uei, int nodeid ) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .addParam("cucsFaultIndex", "0123456789")
                .addParam("cucsFaultCode", "997")
                .getEvent();
    }
}
