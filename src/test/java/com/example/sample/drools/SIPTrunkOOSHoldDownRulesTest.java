package com.example.sample.drools;

import org.junit.Test;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

import java.net.InetAddress;

import static org.opennms.core.utils.InetAddressUtils.addr;

public class SIPTrunkOOSHoldDownRulesTest extends CorrelationRulesTestCase {

    private static Integer SIP_TRUNK_OOS_HOLDDOWN_TIME = 20000; //660000;
    private static String SIP_TRUNK_OOS_PAST_HOLDDOWN_UEI = "uei.opennms.org/correlator/syslog/cucm/SIPTrunkOOS";

    @Test
    public void sipPTrunkOOSHoldDownRules() throws Exception {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(SIP_TRUNK_OOS_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.addParam("DeviceName", "Test DeviceName");
        bldr.addParam("AvailableRemotePeers", "true");
        bldr.addParam("AppID", "App123");
        bldr.addParam("ClusterID", "ClusterID123");
        bldr.addParam("NodeID", "NodeID123");
        bldr.addParam("FullText", "Test FullText");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName("SIPTrunkOOSHolddownRules");

        // Node one goes down at zero seconds
        Event event = createSipTrunkOSSEvent(1);
        System.err.println("SENDING SipTrunkOSSEvent EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at zero seconds
        event = createSipTrunkOSSEvent(2);
        System.err.println("SENDING SipTrunkOSSEvent EVENT NUMBER ONE FOR NODE TWO!!");
        System.err.println("MAKE IT SO, NUMBER TWO!!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SIP_TRUNK_OOS_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(SIP_TRUNK_OOS_HOLDDOWN_TIME / 2);

        // Clear alarm is send out with the existing situation for node 2 at half time
        event = createSIPTrunkISVEvent(2);
        System.err.println("SENDING SIPTrunkISVEvent EVENT FOR NODE TWO SITUATION EXIST!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (SIP_TRUNK_OOS_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(SIP_TRUNK_OOS_HOLDDOWN_TIME / 2 + 1000);

        //Clear alarm for node 1 but it too late, the correlator event already send out
        event = createSIPTrunkISVEvent(1);
        System.err.println("SENDING SipTrunkOSSEvent EVENT FOR NODE ONE AFTER TIME EXPIRED ON THE SITUATION!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }
    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createSipTrunkOSSEvent(int nodeid) {
        return createSIPTrunkOSSEvent("uei.opennms.org/syslog/cucm/SIPTrunkOOS", nodeid);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createSIPTrunkISVEvent(int nodeid) {
        return createSIPTrunkOSSEvent("uei.opennms.org/syslog/cucm/SIPTrunkISV", nodeid);
    }

    /**
     * Creates the service event.
     *
     * @param uei    the event UEI
     * @param nodeid the Node ID
     * @return the event
     */
    private Event createSIPTrunkOSSEvent(String uei, int nodeid) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .addParam("DeviceName", "Test DeviceName")
                .addParam("AvailableRemotePeers", "true")
                .addParam("AppID", "App123")
                .addParam("ClusterID", "ClusterID123")
                .addParam("NodeID", "NodeID123")
                .addParam("FullText", "Test FullText")
                .getEvent();

    }
}
