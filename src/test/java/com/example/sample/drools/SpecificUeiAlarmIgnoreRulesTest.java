package com.example.sample.drools;

import org.junit.Test;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.opennms.core.utils.InetAddressUtils.addr;

public class SpecificUeiAlarmIgnoreRulesTest extends CorrelationRulesTestCase {

    /**
     * The data collection failed hold down UEI.
     */
    private static String ALARM_RESTART_WITHIN_SUPPRESSION_TIME_UEI = "uei.opennms.org/vendor/Avaya/traps/alarmRestartNotWithinSuppressionTimePeriod";
    private static String AVAES_AEP_CONN_LINK_DOWN_MAJOR = "uei.opennms.org/vendor/Avaya/traps/avAesWebLMConnLinkDownMAJOR";
    private static String AVAES_AEP_CONN_LINK_DOWN_MINOR = "uei.opennms.org/vendor/Avaya/traps/avAesWebLMConnLinkDownMINOR";
    private static String AVAES_AEP_CONN_LINK_DOWN_WARNING = "uei.opennms.org/vendor/Avaya/traps/avAesWebLMConnLinkDownWARNING";
    private static String AVAES_AEP_CONN_LINK_DOWN_CLEARED = "uei.opennms.org/vendor/Avaya/traps/avAesWebLMConnLinkDownCLEAR";
    private Integer SUPPRESSION_PERIOD_TIME_WINDOW = 1000;

    @Test
    public void testAlarmRestartRules() throws Exception {
        testAlarmRestartNotWithinSuppressionTimePeriod("specificAlarmIgnoreRules");
        testAvAesAepConnLinkDownMajor("specificAlarmIgnoreRules");
        testAvAesAepConnLinkDownMinor("specificAlarmIgnoreRules");
        testAvAesAepConnLinkDownWarning("specificAlarmIgnoreRules");
        testAvAesAepConnLinkDownCleared("specificAlarmIgnoreRules");
    }

    /**
     * @param engineName the engine name
     * @throws InterruptedException the interrupted exception
     */
    private void testAlarmRestartNotWithinSuppressionTimePeriod(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(ALARM_RESTART_WITHIN_SUPPRESSION_TIME_UEI, "Drools");
        bldr.setNodeid(1);
        bldr.setUei(ALARM_RESTART_WITHIN_SUPPRESSION_TIME_UEI);
        bldr.addParam("g3clientExternalName", "g3clientExternalName");
        bldr.addParam("g3alarmsProductID", 1);
        bldr.addParam("g3alarmsAlarmNumber", 1);
        bldr.addParam("g3restartDateTime", new Date().toString());
        bldr.addParam("g3restartLevel", 1);
        bldr.addParam("g3restartCarrier", "g3restartCarrier");
        bldr.addParam("g3restartInterchange", "g3restartInterchange");
        bldr.addParam("g3restartUnavailable", "g3restartUnavailable");
        bldr.addParam("g3restartCause", "g3restartCause");
        bldr.addParam("g3vintageSpeArelease", "g3vintageSpeArelease");
        bldr.addParam("g3vintageSpeBrelease", "g3vintageSpeBrelease");
        bldr.addParam("g3vintageSpeAupID", 1);
        bldr.addParam("g3vintageSpeBupID", 1);


        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createAlarmRestartEvent(1);
        System.err.println("SENDING INITIAL alarmRestart EVENT!!");
        engine.correlate(event);

        System.err.println("SLEEPING FOR " + (SUPPRESSION_PERIOD_TIME_WINDOW / 2) + " ms");
        Thread.sleep(SUPPRESSION_PERIOD_TIME_WINDOW / 2);

        getAnticipator().verifyAnticipated();
    }

    private void testAvAesAepConnLinkDownMajor(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(AVAES_AEP_CONN_LINK_DOWN_MAJOR, "Drools");
        bldr.setNodeid(1);
        bldr.setUei(AVAES_AEP_CONN_LINK_DOWN_MAJOR);
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "avAesServerIpAddress");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("csAlarmSeverity", "3");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesWeblmUrl", "avAesWeblmUrl");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");
        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createAvAesAepConnLinkDownEvent(1, "3");
        System.err.println("SENDING INITIAL avAesAepConnLinkDown EVENT!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SUPPRESSION_PERIOD_TIME_WINDOW / 2) + " ms");
        Thread.sleep(SUPPRESSION_PERIOD_TIME_WINDOW / 2);

        getAnticipator().verifyAnticipated();
    }
    private void testAvAesAepConnLinkDownMinor(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(AVAES_AEP_CONN_LINK_DOWN_MINOR, "Drools");
        bldr.setNodeid(1);
        bldr.setUei(AVAES_AEP_CONN_LINK_DOWN_MINOR);
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "avAesServerIpAddress");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmSeverity", "5");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesWeblmUrl", "avAesWeblmUrl");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");
        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createAvAesAepConnLinkDownEvent(1, "5");
        System.err.println("SENDING INITIAL avAesAepConnLinkDown EVENT!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SUPPRESSION_PERIOD_TIME_WINDOW / 2) + " ms");
        Thread.sleep(SUPPRESSION_PERIOD_TIME_WINDOW / 2);

        getAnticipator().verifyAnticipated();
    }
    private void testAvAesAepConnLinkDownWarning(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(AVAES_AEP_CONN_LINK_DOWN_WARNING, "Drools");
        bldr.setNodeid(1);
        bldr.setUei(AVAES_AEP_CONN_LINK_DOWN_WARNING);
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "avAesServerIpAddress");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmSeverity", "6");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesWeblmUrl", "avAesWeblmUrl");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");
        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createAvAesAepConnLinkDownEvent(1,"6");
        System.err.println("SENDING INITIAL avAesAepConnLinkDown EVENT!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SUPPRESSION_PERIOD_TIME_WINDOW / 2) + " ms");
        Thread.sleep(SUPPRESSION_PERIOD_TIME_WINDOW / 2);

        getAnticipator().verifyAnticipated();
    }
    private void testAvAesAepConnLinkDownCleared(String engineName) throws InterruptedException, UnknownHostException {
        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(AVAES_AEP_CONN_LINK_DOWN_CLEARED, "Drools");
        bldr.setNodeid(1);
        bldr.setUei(AVAES_AEP_CONN_LINK_DOWN_CLEARED);
        bldr.addParam("sysName", "sysName");
        bldr.addParam("avAesServerIpAddressType", "avAesServerIpAddressType");
        bldr.addParam("avAesServerIpAddress", "avAesServerIpAddress");
        bldr.addParam("entPhysicalAssetID", "entPhysicalAssetID");
        bldr.addParam("sysObjectID", "sysObjectID");
        bldr.addParam("avAesAlarmSeverity", "1");
        bldr.addParam("avAesLinkStatus", "avAesLinkStatus");
        bldr.addParam("avAesWeblmUrl", "avAesWeblmUrl");
        bldr.addParam("avAesEvtSrvReason", "avAesEvtSrvReason");
        anticipate(bldr.getEvent());
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createAvAesAepConnLinkDownEventCLEARED(1, "1");
        System.err.println("SENDING INITIAL avAesAepConnLinkDown EVENT!!");
        engine.correlate(event);

        // Wait for half the hold-down time length
        System.err.println("SLEEPING FOR " + (SUPPRESSION_PERIOD_TIME_WINDOW / 2) + " ms");
        Thread.sleep(SUPPRESSION_PERIOD_TIME_WINDOW / 2);

        getAnticipator().verifyAnticipated();
    }


    /**
     * Create a dataCollectionFailed event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createAlarmRestartEvent(int nodeid) {
        return createEventAlarmRestart("uei.opennms.org/vendor/Avaya/traps/alarmRestart", nodeid);
    }

    /**
     * Create a dataCollectionSucceeded event.
     *
     * @param nodeid the Node ID
     * @return the event
     */
    public Event createAvAesAepConnLinkDownEvent(int nodeid, String severity) {
        return createEventcreateAvAesAepConnLinkDown("uei.opennms.org/vendor/Avaya/traps/avAesAepConnLinkDown", nodeid, severity);
    }
    public Event createAvAesAepConnLinkDownEventCLEARED(int nodeid, String severity) {
        return createEventcreateAvAesAepConnLinkDown("uei.opennms.org/vendor/Avaya/traps/avAesWebLMConnLinkDownCLEARED", nodeid, severity);
    }

    /**
     * @param uei    the event UEI
     * @param nodeid the Node ID
     * @return the event
     */
    private Event createEventAlarmRestart(String uei, int nodeid) {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .addParam("g3clientExternalName", "g3clientExternalName")
            .addParam("g3alarmsProductID", 1)
            .addParam("g3alarmsAlarmNumber", 1)
            .addParam("g3restartDateTime", new Date().toString())
            .addParam("g3restartLevel", 1)
            .addParam("g3restartCarrier", "g3restartCarrier")
            .addParam("g3restartInterchange", "g3restartInterchange")
            .addParam("g3restartUnavailable", "g3restartUnavailable")
            .addParam("g3restartCause", "g3restartCause")
            .addParam("g3vintageSpeArelease", "g3vintageSpeArelease")
            .addParam("g3vintageSpeBrelease", "g3vintageSpeBrelease")
            .addParam("g3vintageSpeAupID", 1)
            .addParam("g3vintageSpeBupID", 1)

            .getEvent();

    }
    private Event createEventcreateAvAesAepConnLinkDown(String uei, int nodeid, String severity) {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .addParam("sysName", "sysName")
            .addParam("avAesServerIpAddressType", "avAesServerIpAddressType")
            .addParam("avAesServerIpAddress", "avAesServerIpAddress")
            .addParam("entPhysicalAssetID", "entPhysicalAssetID")
            .addParam("csAlarmSeverity", severity)
            .addParam("sysObjectID", "sysObjectID")
            .addParam("avAesAlarmType", "avAesAlarmType")
            .addParam("avAesSessionId", "avAesSessionId")
            .addParam("avAesSwitchName", "avAesSwitchName")
            .addParam("avAesAepConnIpAddressType", "avAesAepConnIpAddressType")
            .addParam("avAesAepConnIpAddress", "avAesAepConnIpAddress")
            .addParam("avAesLinkStatus", "avAesLinkStatus")
            .getEvent();

    }
    private OffsetTime timeEventCreateOffset() {
        OffsetTime eventTime;
        int offSetTime;
        String offset = getCurrentTimezoneOffset();
        if (!offset.equals("UTC")) {
            offSetTime = Integer.parseInt(offset);
            System.out.println(offset);
        } else {
            offSetTime = -4;
        }
        String timeEventCreated = getTime(new Date());
        System.out.println(timeEventCreated);
        OffsetTime time = OffsetTime.parse(timeEventCreated);
        System.out.println(time);
        if (offSetTime < 0) {
            eventTime = time.minusHours(Math.abs(offSetTime));
        } else {
            eventTime = time.plusHours(Math.abs(offSetTime));
        }
        return eventTime;
    }

    private Boolean isEventSuppressEventPeriod() {
        boolean isSuppressEventPeriod = true;
        OffsetTime fromTime = OffsetTime.parse("21:50+00:00");
        OffsetTime endTime = OffsetTime.parse("22:10+00:00");
        OffsetTime eventTime = timeEventCreateOffset();
        if (eventTime.isBefore(fromTime) || eventTime.isAfter(endTime)) {
            isSuppressEventPeriod = false;
            System.out.println("Time is not in  suppress period");
        } else {
            System.out.println("Time is in  suppress period");
        }
        return isSuppressEventPeriod;
    }

    private String getTime(Date date) {
        String strDateFormat = "HH:mm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        if (date.getTime() == 24) {
            date.setTime(00);
        }
        String formattedDate = dateFormat.format(date) + "+00:00";
        System.out.println("Current time of the day using Date - 24 hour format: " + formattedDate);
        return formattedDate;
    }

    private String getCurrentTimezoneOffset() {
        String offset = "";
        try {
            TimeZone tz = TimeZone.getDefault();
            Calendar cal = GregorianCalendar.getInstance(tz);
            int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
            offset = String.format("%d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
            offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
            System.out.println("Offset Time: " + offset);
        } catch (Exception e) {
            offset = "UTC";
            e.printStackTrace();
            throw e;
        }
        return offset;
    }

}
