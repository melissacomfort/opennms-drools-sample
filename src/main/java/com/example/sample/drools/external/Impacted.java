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
package com.example.sample.drools.external;

import org.opennms.netmgt.xml.event.Event;

import java.awt.*;

/**
 * The Class Impacted.
 * 
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 */
public class Impacted {

    /** The target. */
    private Object m_target;
    
    /** The cause. */
    private Event m_cause;

    /**
     * Instantiates a new impacted.
     */
    public Impacted() {}

    /**
     * Instantiates a new impacted.
     *
     * @param target the target
     * @param cause the cause
     */
    public Impacted(Object target, Event cause)
    {
        m_target = target;
        m_cause = cause;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public Object getTarget() {
        return m_target;
    }
    
    /**
     * Sets the target.
     *
     * @param target the new target
     */
    public void setTarget(Object target) {
        m_target = target;
    }
    
    /**
     * Gets the cause.
     *
     * @return the cause
     */
    public Event getCause() {
        return m_cause;
    }
    
    /**
     * Sets the cause.
     *
     * @param cause the new cause
     */
    public void setCause(Event cause) {
        m_cause = cause;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Impacted[ target=" + m_target + ", cause=" + m_cause + " ]";
    }

}