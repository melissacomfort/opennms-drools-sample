package com.example.sample.drools;

import org.junit.Test;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

import java.net.InetAddress;

import static org.opennms.core.utils.InetAddressUtils.addr;

public class DataCollectionFailedHoldDownRulesTest extends CorrelationRulesTestCase {
    /**
     * The hold down time for data collection failures
     */
    private static Integer DC_HOLDDOWN_TIME = 30000; //660000;

    /**
     * The data collection failed hold down UEI.
     */
    private static String DATA_COLLECTION_FAILED_PAST_HOLDDOWN_UEI = "uei.opennms.org/nodes/correlation/dataCollectionFailedPastHoldDownTime";

    /**
     * Test data collection failed hold down rules.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDataCollectionFailedHoldDownRules() throws Exception {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(DATA_COLLECTION_FAILED_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(5);
        bldr.setInterface(InetAddress.getByName("127.0.0.1"));
        bldr.setService("XML Service");
        bldr.addParam("reason", "The URL https://localhost/opennms/index.jsp/cusp_perfmon/cusp_perfmon.groovy contains unknown placeholders.");
        bldr.addParam("holdDownTime", "180");

        anticipate(bldr.getEvent());

        DroolsCorrelationEngine engine = findEngineByName("dataCollectionFailedHolddownRules");

        // Node one goes down at zero seconds
        Event event = createDataCollectionFailedEvent(1, "127.0.0.2", "XML Service");
        System.err.println("SENDING dataCollectionFailed EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node two goes down at substantially zero seconds
        event = createDataCollectionFailedEvent(2, "127.0.0.3", "WMI");
        System.err.println("SENDING dataCollectionFailed EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (DC_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(DC_HOLDDOWN_TIME / 2);

        // Node two comes back up at halftime
        event = createDataCollectionSucceededEvent(2, "127.0.0.3", "WMI");
        System.err.println("SENDING dataCollectionSucceeded EVENT FOR NODE TWO!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (DC_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(DC_HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createDataCollectionSucceededEvent(1, "127.0.0.2", "XML Service");
        System.err.println("SENDING dataCollectionSucceeded EVENT FOR NODE ONE!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }

    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid  the Node ID
     * @param ipAddr  the IP address of the interface of the service that data collection failed on
     * @param svcName the name of the service that data collection failed on
     * @return the event
     */
    public Event createDataCollectionFailedEvent(int nodeid, String ipAddr, String svcName) {
        return createSvcEvent("uei.opennms.org/nodes/dataCollectionFailed", nodeid, ipAddr, svcName);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid  the Node ID
     * @param ipAddr  the IP address of the interface of the service that data collection succeeded on
     * @param svcName the name of the service that data collection succeeded on
     * @return the event
     */
    public Event createDataCollectionSucceededEvent(int nodeid, String ipAddr, String svcName) {
        return createSvcEvent("uei.opennms.org/nodes/dataCollectionSucceeded", nodeid, ipAddr, svcName);
    }

    /**
     * Creates the service event.
     *
     * @param uei     the event UEI
     * @param nodeid  the Node ID
     * @param ipaddr  the IP Address
     * @param svcName the service name
     * @return the event
     */
    private Event createSvcEvent(String uei, int nodeid, String ipaddr, String svcName) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .setInterface(addr(ipaddr))
                .setService(svcName)
                .addParam("reason", "The URL https://localhost/opennms/index.jsp/cusp_perfmon/cusp_perfmon.groovy contains unknown placeholders.")
                .getEvent();

    }
}
