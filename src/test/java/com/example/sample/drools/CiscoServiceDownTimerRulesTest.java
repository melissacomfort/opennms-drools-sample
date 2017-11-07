package com.example.sample.drools; /*******************************************************************************
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

import java.net.InetAddress;

import org.junit.Test;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

public class CiscoServiceDownTimerRulesTest extends CorrelationRulesTestCase {

    private static Integer ACTUAL_SERVICE_HOLDDOWN_TIME = 30000;//1200000;
    private static Integer MANAGEMENT_SERVICE_HOLDDOWN_TIME = 20000;//1200000;

    private static String CISCO_SERVICE_PAST_HOLDDOWN_UEI = "uei.opennms.org/nodes/correlation/DownTimeHoldDownTime";

    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testCiscoActualServiceHoldDownTimerExpiredRules() throws Exception {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(CISCO_SERVICE_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.setService("Cisco RTMT Reporter Servlet");
        bldr.addParam("holdDownTime", "30");

        anticipate(bldr.getEvent());

        DroolsCorrelationEngine engine = findEngineByName("ciscoActualService");

        // Node one goes down at zero seconds
        Event event = createActualServiceLostEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 1, "127.0.0.1", "Cisco RTMT Reporter Servlet");
        System.err.println("SENDING Cisco RTMT Reporter Servlet EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at substantially zero seconds
        event = createActualServiceLostEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 2, "127.0.0.2", "WMI");
        System.err.println("SENDING WMI EVENT FOR NODE TWO!!");
        engine.correlate(event);


        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (ACTUAL_SERVICE_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(ACTUAL_SERVICE_HOLDDOWN_TIME / 2);

        // Node two comes back up at halftime
        event = createActualServiceLostEvent(EventConstants.NODE_GAINED_SERVICE_EVENT_UEI, 2, "127.0.0.2", "WMI");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (ACTUAL_SERVICE_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(ACTUAL_SERVICE_HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createActualServiceLostEvent(EventConstants.NODE_GAINED_SERVICE_EVENT_UEI, 1, "127.0.0.1", "Cisco RTMT Reporter Servlet");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE ONE BUT IT IS TOO LATE!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }
    @Test
    public void testManagementServiceHoldDownTimerExpiredRules() throws Exception {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(CISCO_SERVICE_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.setService("SSH");
        bldr.addParam("holdDownTime", "20");

        anticipate(bldr.getEvent());

        DroolsCorrelationEngine engine = findEngineByName("ciscoActualService");

        // Node one goes down at zero seconds
        Event event = createActualServiceLostEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 1, "127.0.0.1", "SSH");
        System.err.println("SENDING SSH EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at substantially zero seconds
        event = createActualServiceLostEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 2, "127.0.0.2", "WMIM");
        System.err.println("SENDING WMIM EVENT FOR NODE TWO!!");
        engine.correlate(event);


        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (MANAGEMENT_SERVICE_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(MANAGEMENT_SERVICE_HOLDDOWN_TIME / 2);

        // Node two comes back up at halftime
        event = createActualServiceLostEvent(EventConstants.NODE_GAINED_SERVICE_EVENT_UEI, 2, "127.0.0.2", "WMIM");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (MANAGEMENT_SERVICE_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(MANAGEMENT_SERVICE_HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createActualServiceLostEvent(EventConstants.NODE_GAINED_SERVICE_EVENT_UEI, 1, "127.0.0.1", "SSH");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE ONE BUT IT IS TOO LATE!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }

    public Event createActualServiceLostEvent(String uei, int nodeid, String ipAddr, String svcName) {
        return createSvcEvent(uei, nodeid, ipAddr, svcName);
    }

    private Event createSvcEvent(String uei, int nodeid, String ipaddr, String svcName) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .setInterface(InetAddressUtils.addr(ipaddr))
                .setService(svcName)
                .getEvent();
    }
}
