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

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.opennms.core.utils.InetAddressUtils.addr;

public class AvayaAuraDusCustomerHoldDownRulesTest extends CorrelationRulesTestCase {

    /**
     * The hold down time for cucsFaultActiveNotifMINOR is 15 minutes
     */
    private static Integer DUST_TIMER_MILLIS = 20000; //900000;

    /**
     * resend storage battery downgrade the alarm with increase the severity level .
     */
    private static String avCmAlmPriCdrWarning_UEI = "uei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrWarning";
    private static String avCmAlmPriCdrResolved_UEI = "uei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrResolved";
    private static String DUST_ALARM_PAST_HOLDDOWN_UEI = "uei.opennms.org/correlator/vendor/Avaya/traps/avCmAlmPriCdrWarning";

    /**
     * Test hold down rules.
     *
     * @throws Exception the exception
     */


    @Test
    public void testHoldDownRules() throws Exception {
        testHoldDownTimeDustCustomerRules("dustCustomerHoldDownTimersRules");
    }


    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testHoldDownTimeDustCustomerRules(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(DUST_ALARM_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("172.20.209.13"));
        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarm(avCmAlmPriCdrWarning_UEI, 1, "172.20.209.13");
        System.err.println("SENDING uei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrWarning EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at substantially zero seconds
        event = event = createAlarm(avCmAlmPriCdrWarning_UEI, 2, "172.20.209.13");
        System.err.println("SENDING ei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrWarning EVENT FOR NODE TWO!!");
        engine.correlate(event);


        // Now wait for the hold-down timer to run out and the delayed alarm already sent out
        System.err.println("SLEEPING FOR " + (DUST_TIMER_MILLIS / 2) + " ms");
        Thread.sleep(DUST_TIMER_MILLIS / 2);

        // Node two comes back up at halftime
        event = event = createAlarm(avCmAlmPriCdrResolved_UEI, 2, "172.20.209.13");
        System.err.println("SENDING ALARM uei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrResolved EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (DUST_TIMER_MILLIS / 2 + 1000) + " ms");
        Thread.sleep(DUST_TIMER_MILLIS / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createAlarm(avCmAlmPriCdrResolved_UEI, 1, "172.20.209.13");
        System.err.println("SENDING uei.opennms.org/translator/vendor/Avaya/traps/avCmAlmPriCdrResolved with situation EVENT FOR NODE ONE AGAIN!!");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE ONE BUT IT IS TOO LATE!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();

    }

    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createAlarm(String uei, int nodeid, String ipaddr) {
        return createScvNameEvent(uei, nodeid, ipaddr);
    }

    private Event createScvNameEvent(String uei, int nodeid, String ipaddr) {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .setInterface(addr(ipaddr))
            .getEvent();
    }
}
