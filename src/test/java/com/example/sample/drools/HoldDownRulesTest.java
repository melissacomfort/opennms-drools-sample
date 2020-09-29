package com.example.sample.drools;

import static org.opennms.core.utils.InetAddressUtils.addr;

import java.net.InetAddress;
import org.junit.Test;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

public class HoldDownRulesTest extends CorrelationRulesTestCase {

    /** The hold down time. */
    private static Integer HOLDDOWN_TIME = 20000;

    /** The node down past hold down UEI. */
    private static String NODE_DOWN_PAST_HOLDDOWN_UEI = "uei.opennms.org/nodes/correlation/nodeDownPastHoldDownTime";

    /** The interface down past hold down UEI. */
    private static String IFACE_DOWN_PAST_HOLDDOWN_UEI = "uei.opennms.org/nodes/correlation/interfaceDownPastHoldDownTime";

    /** The Node Lost Service past hold down UEI. */
    private static String NODE_LOST_SERVICE_HOLDDOWN_TIME_UEI = "uei.opennms.org/nodes/correlation/nodeLostServicePastHoldDownTime";


    /**
     * Test hold down rules.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNodeDownHolddownRules() throws Exception {
        testNodeDownHolddownRules("nodeDownHolddownRules");
    }

    /**
     * Test hold down rules.
     *
     * @param engineName the engine name
     * @throws Exception the interrupted exception
     */
    private void testNodeDownHolddownRules(String engineName) throws Exception {

        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder( NODE_DOWN_PAST_HOLDDOWN_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.addParam( "holdDownTime", "30" );

        anticipate( bldr.getEvent() );

        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node one goes down at zero seconds
        Event event = createNodeDownEvent( 1 );
        System.err.println("SENDING nodeDown EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate( event );

        // Node two goes down at substantially zero seconds
        event = createNodeDownEvent( 2 );
        System.err.println("SENDING nodeDown EVENT FOR NODE TWO!!");
        engine.correlate( event );

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(HOLDDOWN_TIME / 2);

        // Node two comes back up at halftime
        event = createNodeUpEvent( 2 );
        System.err.println("SENDING nodeUp EVENT FOR NODE TWO!!");
        engine.correlate( event );

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep( HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createNodeUpEvent( 1 );
        System.err.println("SENDING nodeUp EVENT FOR NODE ONE!!");
        engine.correlate( event );

        getAnticipator().verifyAnticipated();
    }

    /**
     * Test iface down holddown rules.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIfaceDownHolddownRules() throws Exception {
        testIfaceDownHolddownRules("interfaceDownHolddownRules");
    }

    /**
     * Test iface down holddown rules.
     *
     * @param engineName the engine name
     * @throws Exception the interrupted exception
     */
    private void testIfaceDownHolddownRules(String engineName) throws Exception {

        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for interface 1.1.1.1
        EventBuilder bldr = new EventBuilder( IFACE_DOWN_PAST_HOLDDOWN_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface(InetAddress.getByName("1.1.1.1"));
        bldr.addParam( "holdDownTime", "30" );

        anticipate( bldr.getEvent() );

        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Interface one goes down at zero seconds
        Event event = createIfEvent(EventConstants.INTERFACE_DOWN_EVENT_UEI, 1, "1.1.1.1");
        System.err.println("SENDING interfaceDown EVENT FOR IFACE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate( event );

        // Interface two goes down at substantially zero seconds
        event = createIfEvent(EventConstants.INTERFACE_DOWN_EVENT_UEI, 1, "2.2.2.2");
        System.err.println("SENDING interfaceDown EVENT FOR IFACE TWO!!");
        engine.correlate( event );

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(HOLDDOWN_TIME / 2);

        // Interface two comes back up at halftime
        event = createIfEvent(EventConstants.INTERFACE_UP_EVENT_UEI, 1, "2.2.2.2");
        System.err.println("SENDING interfaceUp EVENT FOR IFACE TWO!!");
        engine.correlate( event );

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep( HOLDDOWN_TIME / 2 + 1000);

        // Node one comes back up, but it's too late for him
        event = createIfEvent(EventConstants.INTERFACE_UP_EVENT_UEI, 1, "1.1.1.1");
        System.err.println("SENDING interfaceUp EVENT FOR IFACE ONE!!");
        engine.correlate( event );

        getAnticipator().verifyAnticipated();
    }


    @Test
    public void testNodeLostServiceHoldDownRules() throws Exception {
        testNodeLostServiceHoldDownRules("nodeLostServiceHolddownRules");
    }
    /**
     * Test node Lost Service down holddown rules.
     * @throws Exception the interrupted exception
     */

    private void testNodeLostServiceHoldDownRules(String engineName) throws Exception {
        getAnticipator().reset();
        // In the end, we expect to see a down-past-holddown-time event only for node three
        EventBuilder bldr = new EventBuilder(NODE_LOST_SERVICE_HOLDDOWN_TIME_UEI, "Drools");
        bldr.setNodeid(3);
        bldr.setInterface(InetAddress.getByName("127.3.3.3"));
        bldr.setService("THREE");
        bldr.addParam("holdDownTime", "30");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        // Node three goes down at zero seconds
        Event event = createSvcEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 3, "127.3.3.3", "THREE");
        System.err.println("SENDING SERVICE THREE EVENT FOR NODE THREE!!");
        System.err.println("MAKE IT SO, NUMBER THREE!!!");
        engine.correlate(event);

        // Node four goes down at substantially zero seconds
        event = createSvcEvent(EventConstants.NODE_LOST_SERVICE_EVENT_UEI, 4, "127.4.4.4", "FOUR");
        System.err.println("SENDING SERVICE FOUR EVENT FOR NODE FOUR!!");
        System.err.println("MAKE IT SO, NUMBER FOUR!!!");
        engine.correlate(event);


        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(HOLDDOWN_TIME / 2);

        // Node four comes back up at halftime
        event = createSvcEvent( EventConstants.NODE_REGAINED_SERVICE_EVENT_UEI, 4, "127.4.4.4", "FOUR");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE FOUR!!");
        System.err.println("MAKE IT ALARM CLEAR, NUMBER FOUR!!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node three
        System.err.println("SLEEPING FOR " + (HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep( HOLDDOWN_TIME / 2 + 1000);

        // Node three comes back up, but it's too late for him
        event = createSvcEvent(EventConstants.NODE_REGAINED_SERVICE_EVENT_UEI, 3, "127.3.3.3", "THREE");
        System.err.println("SENDING ALARM CLEAR EVENT FOR NODE THREE BUT IT IS TOO LATE!!");
        System.err.println("MAKE IT ALARM CLEAR, NUMBER THREE BUT TOO LATE!!!");
        engine.correlate(event);

        getAnticipator().verifyAnticipated();
    }
    /**
     * Creates the node down event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createNodeDownEvent(int nodeid) {
        return createNodeEvent(EventConstants.NODE_DOWN_EVENT_UEI, nodeid);
    }

    /**
     * Creates the node up event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createNodeUpEvent(int nodeid) {
        return createNodeEvent(EventConstants.NODE_UP_EVENT_UEI, nodeid);
    }


    /**
     * Creates the service event.
     *
     * @param uei the event UEI
     * @param nodeid the Node ID
     * @param ipaddr the IP Address
     * @param svcName the service name
     * @return the event
     */
    private Event createSvcEvent(String uei, int nodeid, String ipaddr, String svcName) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .setInterface(InetAddressUtils.addr(ipaddr))
                .setService(svcName)
                .getEvent();
    }

    /**
     * Creates the if event.
     *
     * @param uei the event UEI
     * @param nodeid the Node ID
     * @param ipaddr the IP Address
     * @return the event
     */
    private Event createIfEvent(String uei, int nodeid, String ipaddr) {
        return new EventBuilder(uei, "Drools")
                .setNodeid(nodeid)
                .setInterface( addr( ipaddr ) )
                .getEvent();
    }


    /**
     * Creates the node event.
     *
     * @param uei the event UEI
     * @param nodeid the Node ID
     * @return the event
     */
    private Event createNodeEvent(String uei, int nodeid) {
        return new EventBuilder(uei, "test")
                .setNodeid(nodeid)
                .getEvent();
    }

}
