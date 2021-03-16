package com.example.sample.drools;

import org.junit.Test;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

public class SyncSignalFaultRulesTest extends CorrelationRulesTestCase {

    private static Integer SIP_TRUNK_OOS_HOLDDOWN_TIME = 20000; //660000;
    private static String SYNC_SIGNAL_FAULT_PAST_HOLDDOWN_UEI = "uei.opennms.org/correlator/vendor/Avaya/traps/cmgSyncSignalFault";

    @Test
    public void sipPTrunkOOSHoldDownRules() throws Exception {
        getAnticipator().reset();

        // In the end, we expect to see a down-past-holddown-time event only for node one
        EventBuilder bldr = new EventBuilder(SYNC_SIGNAL_FAULT_PAST_HOLDDOWN_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.addParam("cmgSyncSignalFault", "SignalFault");
        bldr.addParam("cmgTrapSubsystem", "Subsystem");
        bldr.addParam("cmgTrapOnBoard", "TrapOnBoard");
        bldr.addParam("cmgTrapLocation", "TrapLocation");
        bldr.addParam("cmgHardwareFaultMask", "HardwareFaultMask");
        bldr.addParam("cmgPrimaryClockSource", "ClockSource");
        bldr.addParam("cmgSecondaryClockSource", "SecondaryClockSource");
        bldr.addParam("cmgActiveClockSource", "ActiveClockSource");

        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName("syncSignalFaultHolddownRules");

        // Node one goes down at zero seconds
        Event event = createCmgSyncSignalFaultEvent(1);
        System.err.println("SENDING SipTrunkOSSEvent EVENT FOR NODE ONE!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate(event);

        // Node one goes down at zero seconds
        event = createCmgSyncSignalFaultEvent(2);
        System.err.println("SENDING SipTrunkOSSEvent EVENT NUMBER ONE FOR NODE TWO!!");
        System.err.println("MAKE IT SO, NUMBER TWO!!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SIP_TRUNK_OOS_HOLDDOWN_TIME / 2) + " ms");
        Thread.sleep(SIP_TRUNK_OOS_HOLDDOWN_TIME / 2);

        // Clear alarm is send out with the existing situation for node 2 at half time
        event = createCmgSyncSignalClearEvent(2);
        System.err.println("SENDING SIPTrunkISVEvent EVENT FOR NODE TWO SITUATION EXIST!!");
        engine.correlate(event);

        // Now wait for the hold-down timer to run out on node one
        System.err.println("SLEEPING FOR " + (SIP_TRUNK_OOS_HOLDDOWN_TIME / 2 + 1000) + " ms");
        Thread.sleep(SIP_TRUNK_OOS_HOLDDOWN_TIME / 2 + 1000);

        //Clear alarm for node 1 but it too late, the correlator event already send out
        event = createCmgSyncSignalClearEvent(1);
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
    public Event createCmgSyncSignalFaultEvent(int nodeid) {
        return createSIPTrunkOSSEvent("uei.opennms.org/vendor/Avaya/traps/cmgSyncSignalFault", nodeid);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid  the Node ID
     * @return the event
     */
    public Event createCmgSyncSignalClearEvent(int nodeid) {
        return createSIPTrunkOSSEvent("uei.opennms.org/vendor/Avaya/traps/cmgSyncSignalClear", nodeid);
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
        .addParam("cmgSyncSignalFault", "SignalFault")
        .addParam("cmgTrapSubsystem", "Subsystem")
        .addParam("cmgTrapOnBoard", "TrapOnBoard")
        .addParam("cmgTrapLocation", "TrapLocation")
        .addParam("cmgHardwareFaultMask", "HardwareFaultMask")
        .addParam("cmgPrimaryClockSource", "ClockSource")
        .addParam("cmgSecondaryClockSource", "SecondaryClockSource")
        .addParam("cmgActiveClockSource", "ActiveClockSource")
                .getEvent();

    }
}
