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

import static org.junit.Assert.assertEquals;
import static org.opennms.core.utils.InetAddressUtils.addr;

public class AEPSwitchLinkHoldDownRulesTest extends CorrelationRulesTestCase {

    /**
     * The hold down time for cucsFaultActiveNotifMINOR is 15 minutes
     */
    private static Integer AVAES_LINH_DOWN_HOLDDOWN_TIME = 20000; //900000;

    /**
     * resend storage battery downgrade the alarm with increase the severity level .
     */
    private static String AVAES_LINH_DOWN_MAJOR_UEI = "uei.opennms.org/vendor/Avaya/correlator/avAesAepConnLinkDown_Major";
    private static String AVAES_LINH_DOWN_MINOR_UEI = "uei.opennms.org/vendor/Avaya/correlator/avAesAepConnLinkDown_Minor";
    private static String AVAES_LINH_DOWN_WARNING_UEI = "uei.opennms.org/vendor/Avaya/correlator/avAesAepConnLinkDown_Warning";

    /**
     * Test hold down rules.
     *
     * @throws Exception the exception
     */


    @Test
    public void testHoldDownRules() throws Exception  {
        testHoldDownTimerAvAesAepConnLinkDownRules("avAesAepConnLinkDown");
    }
    @Test
    public void testHoldDownExistSituationRules() throws Exception  {
        testHoldDownTimerAvAesAepConnLinkDownWithSituationExistRules("avAesAepConnLinkDown");
    }

    @Test
    public void testClearedNoExistSituationRules() throws Exception  {
        testClearedEventWithNoSituationRules("avAesAepConnLinkDown");
    }

    @Test
    public void testClearedExistSituationRules() throws Exception  {
        testClearedEventWithSituationRules("avAesAepConnLinkDown");
    }

    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testHoldDownTimerAvAesAepConnLinkDownWithSituationExistRules(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(AVAES_LINH_DOWN_WARNING_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "127.0.0.1");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("csAlarmSeverity", "5");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmType", "avAesAlarmType");
        bldr.addParam("avAesSessionId", "111111");
        bldr.addParam("avAesSwitchName", "avAesSwitchName");
        bldr.addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType");
        bldr.addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarmLinkDownMinorEvent(1, "127.0.0.1","5");
        System.err.println("SENDING avAesAepConnLinkDownMinor EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        System.err.println("SLEEPING FOR " + (AVAES_LINH_DOWN_HOLDDOWN_TIME/2 ) + " ms");
        Thread.sleep(AVAES_LINH_DOWN_HOLDDOWN_TIME/2 );

        event = createAlarmLinkDownMajorEvent(1, "127.0.0.1","4");
        System.err.println("SENDING avAesAepConnLinkDownMajor EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out and the delayed alarm already sent out
        System.err.println("SLEEPING FOR " + (AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000) + " ms");
        Thread.sleep(AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000 );

        int unanticipate = getAnticipator().unanticipatedEvents().size();
        assertEquals(unanticipate, 1);

    }
    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testClearedEventWithNoSituationRules(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(AVAES_LINH_DOWN_WARNING_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "127.0.0.1");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("csAlarmSeverity", "6");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmType", "avAesAlarmType");
        bldr.addParam("avAesSessionId", "111111");
        bldr.addParam("avAesSwitchName", "avAesSwitchName");
        bldr.addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType");
        bldr.addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarmLinkDownWarningEvent(1, "127.0.0.1","6");
        System.err.println("SENDING avAesAepConnLinkDownWarning EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node one goes down at zero seconds
        event = createAlarmClearEvent(1, "127.0.0.1","1");
        System.err.println("SENDING avAesAepConnLinkDownCleared with no situation EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out and the delayed alarm already sent out
        System.err.println("SLEEPING FOR " + (AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000) + " ms");
        Thread.sleep(AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000 );

        int anticipate = getAnticipator().getAnticipatedEvents().size();
        assertEquals(anticipate, 1);

    }

    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testClearedEventWithSituationRules(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(AVAES_LINH_DOWN_WARNING_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "127.0.0.1");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("csAlarmSeverity", "6");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmType", "avAesAlarmType");
        bldr.addParam("avAesSessionId", "111111");
        bldr.addParam("avAesSwitchName", "avAesSwitchName");
        bldr.addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType");
        bldr.addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarmLinkDownWarningEvent(1, "127.0.0.1","6");
        System.err.println("SENDING avAesAepConnLinkDownWarning EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);


        // Node one goes down at zero seconds
        event = createAlarmClearEvent(1, "127.0.0.1","1");
        System.err.println("SENDING avAesAepConnLinkDownCleared with no situation EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);


        // Now wait for the hold-down timer to run out and the delayed alarm already sent out
        System.err.println("SLEEPING FOR " + (AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000) + " ms");
        Thread.sleep(AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000 );

        int anticipate = getAnticipator().getAnticipatedEvents().size();
        assertEquals(anticipate, 1);

    }
    /**
     * Test hold down rules.
     *
     * @throws InterruptedException the interrupted exception
     */
    private void testHoldDownTimerAvAesAepConnLinkDownRules(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(AVAES_LINH_DOWN_WARNING_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "127.0.0.1");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("csAlarmSeverity", "6");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmType", "avAesAlarmType");
        bldr.addParam("avAesSessionId", "111111");
        bldr.addParam("avAesSwitchName", "avAesSwitchName");
        bldr.addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType");
        bldr.addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createAlarmLinkDownWarningEvent(1, "127.0.0.1","6");
        System.err.println("SENDING avAesAepConnLinkDownWarning EVENT FOR NODE ONE AGAIN!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);


        // Now wait for the hold-down timer to run out and the delayed alarm already sent out
        System.err.println("SLEEPING FOR " + (AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000) + " ms");
        Thread.sleep(AVAES_LINH_DOWN_HOLDDOWN_TIME + 3000 );

        getAnticipator().verifyAnticipated();

    }
    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createAlarmLinkDownMajorEvent(int nodeid, String ipaddr, String severity) {
        return createScvNameEvent("uei.opennms.org/vendor/Avaya/traps/avAesAepConnLinkDownMAJOR", nodeid, ipaddr, severity);
    }

    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createAlarmLinkDownMinorEvent(int nodeid, String ipaddr, String severity) {
        return createScvNameEvent("uei.opennms.org/vendor/Avaya/traps/avAesAepConnLinkDownMINOR", nodeid, ipaddr, severity);
    }

    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createAlarmLinkDownWarningEvent(int nodeid, String ipaddr, String severity) {
        return createScvNameEvent("uei.opennms.org/vendor/Avaya/traps/avAesAepConnLinkDownWARNING", nodeid, ipaddr, severity);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createAlarmClearEvent(int nodeid, String ipaddr, String severity) {
        return createScvNameEvent("uei.opennms.org/vendor/Avaya/traps/avAesAepConnLinkDownCLEARED", nodeid, ipaddr, severity);
    }

    /**
     * Creates the service event.
     *
     * @param uei     the event UEI
     * @param nodeid  the Node ID
     * @return the event
     */
    private Event createScvNameEvent(String uei, int nodeid , String ipaddr, String severity) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .setInterface(addr(ipaddr))
                .addParam("sysName", "sysName")
                .addParam("avAesServerIpAddressType", "avAesServerIpAddressType")
                .addParam("avAesServerIpAddress", "127.0.0.1")
                .addParam("entPhysicalAssetID", "entPhysicalAssetID")
                .addParam("csAlarmSeverity", severity)
                .addParam("sysObjectID", "sysObjectID")
                .addParam("avAesAlarmType", "avAesAlarmType")
                .addParam("avAesSessionId", "111111")
                .addParam("avAesSwitchName", "avAesSwitchName")
                .addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType")
                .addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress")
                .addParam("avAesLinkStatus", "avAesLinkStatus")
                .addParam("avAesEvtSrvReason", "avAesEvtSrvReason")
                .getEvent();
    }
}
