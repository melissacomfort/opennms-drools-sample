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

import static org.opennms.core.utils.InetAddressUtils.addr;

import java.net.InetAddress;

/**
 * The Class Service.
 * 
 * @author <a href="mailto:jeffg@opennms.org">Jeff Gehlbach</a>
 */
public class Service {
    
    /** The node. */
    private long m_node;
    
    /** The address. */
    private InetAddress m_addr;
    
    /** The service. */
    private String m_svc;

    /**
     * Instantiates a new service.
     */
    public Service() {}

    /**
     * Instantiates a new service.
     *
     * @param node the node
     * @param addrString the address string
     * @param svc the service
     */
    public Service(long node, String addrString, String svc) {
        m_node = node;
        m_addr = addr( addrString );
        m_svc = svc;
    }

    /**
     * Instantiates a new service.
     *
     * @param node the node
     * @param addr the address
     * @param svc the service
     */
    public Service(long node, InetAddress addr, String svc) {
        m_node = node;
        m_addr = addr;
        m_svc = svc;
    }

    /**
     * Gets the node.
     *
     * @return the node
     */
    public long getNode() {
        return m_node;
    }
    
    /**
     * Sets the node.
     *
     * @param node the new node
     */
    public void setNode(long node) {
        m_node = node;
    }
    
    /**
     * Gets the address.
     *
     * @return the address
     */
    public InetAddress getAddr() {
        return m_addr;
    }
    
    /**
     * Sets the address.
     *
     * @param addr the new address
     */
    public void setAddr(InetAddress addr) {
        m_addr = addr;
    }
    
    /**
     * Gets the service.
     *
     * @return the service
     */
    public String getSvc() {
        return m_svc;
    }
    
    /**
     * Sets the service.
     *
     * @param svc the new service
     */
    public void setSvc(String svc) {
        m_svc = svc;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Service[ node=" + m_node + ", addr=" + m_addr + ", svc=" + m_svc + " ]";
    }

}
