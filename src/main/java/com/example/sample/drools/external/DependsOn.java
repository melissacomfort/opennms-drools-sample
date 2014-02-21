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

/**
 * The Class DependsOn.
 * 
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 */
public class DependsOn {
    
    /** The a. */
    private Object m_a;
    
    /** The b. */
    private Object m_b;

    /**
     * Instantiates a new depends on.
     */
    public DependsOn() {}

    /**
     * Instantiates a new depends on.
     *
     * @param a the a
     * @param b the b
     */
    public DependsOn(Object a, Object b)
    {
        m_a = a;
        m_b = b;
    }

    /**
     * Gets the a.
     *
     * @return the a
     */
    public Object getA() {
        return m_a;
    }

    /**
     * Sets the a.
     *
     * @param a the new a
     */
    public void setA(Object a) {
        m_a = a;
    }

    /**
     * Gets the b.
     *
     * @return the b
     */
    public Object getB() {
        return m_b;
    }

    /**
     * Sets the b.
     *
     * @param b the new b
     */
    public void setB(Object b) {
        m_b = b;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DependsOn[ a=" + m_a + ", b=" + m_b + " ]";
    }

}
