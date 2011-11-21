/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
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

import static org.opennms.core.utils.InetAddressUtils.addr;

import org.junit.Ignore;
import org.junit.Test;
import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.correlation.drools.DroolsCorrelationEngine;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Parm;
import org.opennms.netmgt.xml.event.Value;

public class AggregationRulesTest extends CorrelationRulesTestCase {

    private static String MFC_IP_ADDR = "10.13.110.116";
    private static Integer POST_INITIAL_EVENT_DELAY = 1000;
    private static Integer INTER_SUBSEQUENT_EVENT_DELAY = 250;
    
    private static String CONN_RATE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/connectionRateHigh";
    private static String AGG_CONN_RATE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateConnectionRateHigh";
    
    private static String XACT_RATE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/transactionRateHigh";
    private static String AGG_XACT_RATE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateTransactionRateHigh";
    
    private static String AVG_CACHE_BW_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/avgcacheBWusageHigh";
    private static String AGG_AVG_CACHE_BW_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateAvgcacheBWusageHigh";
    
    private static String AVG_ORIGIN_BW_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/avgoriginBWusageHigh";
    private static String AGG_AVG_ORIGIN_BW_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateAvgoriginBWusageHigh";
    
    private static String MEM_UTIL_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/traps/memUtilizationHigh";
    private static String AGG_MEM_UTIL_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/correlation/aggregateMemUtilizationHigh";

    private static String NET_UTIL_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/traps/netUtilizationHigh";
    private static String AGG_NET_UTIL_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/correlation/aggregateNetUtilizationHigh";
    
    private static String PAGING_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/traps/pagingActivityHigh";
    private static String AGG_PAGING_HIGH_UEI = "uei.opennms.org/vendor/TallMaple/TMS/correlation/aggregatePagingActivityHigh";
    
    private static String RP_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/resourcePoolUsageHigh";
    private static String AGG_RP_USAGE_HIGH_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateResourcePoolUsageHigh";
    private static String RP_USAGE_LOW_UEI = "uei.opennms.org/vendor/Juniper/MFC/traps/resourcePoolUsageLow";
    private static String AGG_RP_USAGE_LOW_UEI = "uei.opennms.org/vendor/Juniper/MFC/correlation/aggregateResourcePoolUsageLow";
    
    @Test
    public void testConnectionRateRules() throws Exception {
        testConnectionRateRules("connectionRateRules");
    }
    
    private void testConnectionRateRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        EventBuilder bldr = new EventBuilder( AGG_CONN_RATE_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( CONN_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL connectionRateHigh EVENT!!");
        System.err.println("MAKE IT SO, NUMBER ONE!!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( CONN_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING connectionRateHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( CONN_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING connectionRateHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    public void testTransactionRateRules() throws Exception {
        testTransactionRateRules("transactionRateRules");
    }
    
    private void testTransactionRateRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_XACT_RATE_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( XACT_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL transactionRateHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( XACT_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING transactionRateHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( XACT_RATE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING transactionRateHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    public void testAvgCacheBwUsageRules() throws Exception {
    	testAvgCacheBwUsageRules("avgCacheBwUsageRules");
    }
    
    private void testAvgCacheBwUsageRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_AVG_CACHE_BW_USAGE_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( AVG_CACHE_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL avgcacheBWusageHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( AVG_CACHE_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING avgcacheBWusageHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( AVG_CACHE_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING avgcacheBWusageHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }

    @Test
    public void testAvgOriginBwUsageRules() throws Exception {
    	testAvgOriginBwUsageRules("avgOriginBwUsageRules");
    }
    
    private void testAvgOriginBwUsageRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_AVG_ORIGIN_BW_USAGE_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( AVG_ORIGIN_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL avgoriginBWusageHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( AVG_ORIGIN_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING avgoriginBWusageHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( AVG_ORIGIN_BW_USAGE_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING avgoriginBWusageHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    public void testMemUtilRules() throws Exception {
    	testMemUtilRules("memUtilizationRules");
    }
    
    private void testMemUtilRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_MEM_UTIL_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( MEM_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL memUtilizationHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( MEM_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING memUtilizationHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( MEM_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING memUtilizationHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    public void testNetUtilRules() throws Exception {
    	testNetUtilRules("netUtilizationRules");
    }
    
    private void testNetUtilRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_NET_UTIL_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( NET_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL netUtilizationHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( NET_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING netUtilizationHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( NET_UTIL_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING netUtilizationHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    public void testPagingActivityRules() throws Exception {
    	testPagingActivityRules("pagingActivityRules");
    }
    
    private void testPagingActivityRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_PAGING_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );
        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createIfEvent( PAGING_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING INITIAL pagingActivityHigh EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createIfEvent( PAGING_HIGH_UEI, 1, MFC_IP_ADDR );
            System.err.println("SENDING pagingActivityHigh EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createIfEvent( PAGING_HIGH_UEI, 1, MFC_IP_ADDR );
        System.err.println("SENDING FINAL TRIGGERING pagingActivityHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    @Test
    @Ignore
    public void testResourcePoolUtilRules() throws Exception {
    	testResourcePoolUtilRules("resourcePoolUtilRules");
    }
    
    private void testResourcePoolUtilRules(String engineName) throws InterruptedException {
        
        getAnticipator().reset();
        
        // This event is the one we anticipate to be spat out of the ruleset
        EventBuilder bldr = new EventBuilder( AGG_RP_USAGE_HIGH_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );
        bldr.addParam( "resourcePoolName", "facebook" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );

        bldr = new EventBuilder( AGG_RP_USAGE_LOW_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( MFC_IP_ADDR ) );
        bldr.addParam( "occurrences", "10" );
        bldr.addParam( "timeWindow", "3600000" );
        bldr.addParam( "resourcePoolName", "myspace" );

        // Prime the event anticipator to expect this output event
        anticipate( bldr.getEvent() );

        
        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createOneParmIfEvent( RP_USAGE_HIGH_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "facebook" );
        System.err.println("SENDING INITIAL resourcePoolUsageHigh EVENT!!");
        engine.correlate( event );
        
        event = createOneParmIfEvent( RP_USAGE_LOW_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "myspace" );
        System.err.println("SENDING INITIAL resourcePoolUsageLow EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 2; i <= 10; i++) {
            event = createOneParmIfEvent( RP_USAGE_HIGH_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "facebook" );
            System.err.println("SENDING resourcePoolUsageHigh EVENT NUMBER " + i);
            engine.correlate( event );
            event = createOneParmIfEvent( RP_USAGE_LOW_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "myspace" );
            System.err.println("SENDING resourcePoolUsageLow EVENT NUMBER " + i);
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createOneParmIfEvent( RP_USAGE_HIGH_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "myspace" );
        System.err.println("SENDING FINAL TRIGGERING resourcePoolUsageHigh EVENT!!");
        engine.correlate( event );
        event = createOneParmIfEvent( RP_USAGE_LOW_UEI, 1, MFC_IP_ADDR, ".1.3.6.1.4.1.35000.xxx", "myspace" );
        System.err.println("SENDING FINAL TRIGGERING resourcePoolUsageHigh EVENT!!");
        engine.correlate( event );
        
        getAnticipator().verifyAnticipated();
        
    }
    
    public Event createNodeDownEvent(int nodeid) {
        return createNodeEvent(EventConstants.NODE_DOWN_EVENT_UEI, nodeid);
    }
    
    public Event createNodeUpEvent(int nodeid) {
        return createNodeEvent(EventConstants.NODE_UP_EVENT_UEI, nodeid);
    }
    
    public Event createNodeLostServiceEvent(int nodeid, String ipAddr, String svcName)
    {
        return createSvcEvent("uei.opennms.org/nodes/nodeLostService", nodeid, ipAddr, svcName);
    }
    
    public Event createNodeRegainedServiceEvent(int nodeid, String ipAddr, String svcName)
    {
        return createSvcEvent("uei.opennms.org/nodes/nodeRegainedService", nodeid, ipAddr, svcName);
    }
    
    private Event createSvcEvent(String uei, int nodeid, String ipaddr, String svcName)
    {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .setInterface( addr( ipaddr ) )
            .setService( svcName )
            .getEvent();
            
    }

    private Event createIfEvent(String uei, int nodeid, String ipaddr)
    {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .setInterface( addr( ipaddr ) )
            .getEvent();
    }

    private Event createOneParmIfEvent(String uei, int nodeid, String ipaddr, String parmName, String parmValue)
    {
        return new EventBuilder(uei, "Drools")
            .setNodeid(nodeid)
            .setInterface( addr( ipaddr ) )
            .addParam(parmName, parmValue)
            .getEvent();
    }

    
    private Event createNodeEvent(String uei, int nodeid) {
        return new EventBuilder(uei, "test")
            .setNodeid(nodeid)
            .getEvent();
    }

}
