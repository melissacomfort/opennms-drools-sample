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

import static org.opennms.core.utils.InetAddressUtils.addr;

/**
 * The Class LnUnauthorizedLoginRulesTest.
 *
 */
public class UnauthorizedLoginRulesTest extends CorrelationRulesTestCase {

    /** The post initial event delay. */
    private static Integer POST_INITIAL_EVENT_DELAY = 10000;

    /** The inter subsequent event delay. */
    private static Integer INTER_SUBSEQUENT_EVENT_DELAY = 25000;

    private static String LN_UNAUTH_LOGIN_UEI = "uei.opennms.org/vendor/Avaya/traps/lntUnAuthAccessEvent";

    private static String LN_UNAUTH_LOGIN_CORRELATOR_UEI = "uei.opennms.org/vendor/Avaya/correlator/lntUnAuthAccessEvent";

private static String AV_UNAUTH_LOGIN_UEI = "uei.opennms.org/vendor/Avaya/traps/avUnAuthAccessEvent";

private static String AV_UNAUTH_LOGIN_CORRELATOR_UEI = "uei.opennms.org/vendor/Avaya/correlator/avUnAuthAccessEvent";


    /**
     * Test lntUnAuthAccessEvent rules.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLntUnAuthAccessRules() throws Exception {
        testLntUnAuthAccessRules("lntUnAuthAccessEvent");
    }
    /**
    * Test avUnAuthAccessEvent rules.
    *
    * @throws Exception the exception
    */
    @Test
    public void testAvUnAuthAccessEventRules() throws Exception {
        testAvUnAuthAccessEventRules("avUnAuthAccessEvent");
    }
    /**
     * Test lntUnAuthAccessEvent rules.
     *
     * @param engineName the engine name
     * @throws InterruptedException the interrupted exception
     */
    private void testLntUnAuthAccessRules(String engineName) throws InterruptedException {

        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(LN_UNAUTH_LOGIN_CORRELATOR_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( "127.0.0.1" ) );
        bldr.addParam( "occurrences", "5" );
        bldr.addParam( "timeWindow", "900000" );

        anticipate( bldr.getEvent() );

        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createEvent(LN_UNAUTH_LOGIN_UEI, 1, "127.0.0.1" );
        System.err.println("SENDING INITIAL lntUnAuthAccessEvent EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 1; i <= 5; i++) {
            event = createEvent(LN_UNAUTH_LOGIN_UEI, 1, "127.0.0.1" );
            System.err.println("SENDING lntUnAuthAccessEvent EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createEvent(LN_UNAUTH_LOGIN_UEI, 1, "127.0.0.1" );
        System.err.println("SENDING FINAL TRIGGERING lntUnAuthAccessEvent EVENT!!");
        engine.correlate( event );

        getAnticipator().verifyAnticipated();

    }

    /**
     * Test avUnAuthAccessEvent rules.
     *
     * @param engineName the engine name
     * @throws InterruptedException the interrupted exception
     */
    private void testAvUnAuthAccessEventRules(String engineName) throws InterruptedException {

        getAnticipator().reset();

        EventBuilder bldr = new EventBuilder(AV_UNAUTH_LOGIN_CORRELATOR_UEI, "Drools" );
        bldr.setNodeid( 1 );
        bldr.setInterface( addr( "127.0.0.2" ) );
        bldr.addParam( "occurrences", "5" );
        bldr.addParam( "timeWindow", "900000" );

        anticipate( bldr.getEvent() );

        DroolsCorrelationEngine engine = findEngineByName(engineName);

        Event event = createEvent(AV_UNAUTH_LOGIN_UEI, 1, "127.0.0.2" );
        System.err.println("SENDING INITIAL avUnAuthAccessEvent EVENT!!");
        engine.correlate( event );

        Thread.sleep(POST_INITIAL_EVENT_DELAY);

        for (int i = 1; i <= 5; i++) {
            event = createEvent(AV_UNAUTH_LOGIN_UEI, 1, "127.0.0.2" );
            System.err.println("SENDING avUnAuthAccessEvent EVENT NUMBER " + i);
            engine.correlate( event );
            Thread.sleep(INTER_SUBSEQUENT_EVENT_DELAY);
        }

        event = createEvent(AV_UNAUTH_LOGIN_UEI, 1, "127.0.0.2" );
        System.err.println("SENDING FINAL TRIGGERING avUnAuthAccessEvent EVENT!!");
        engine.correlate( event );

        getAnticipator().verifyAnticipated();

    }
    private Event createEvent(String uei, int nodeid, String ipaddr)
    {
        return new EventBuilder(uei, "Drools")
        .setNodeid(nodeid)
        .setInterface( addr( ipaddr ) )
        .getEvent();
    }
}
