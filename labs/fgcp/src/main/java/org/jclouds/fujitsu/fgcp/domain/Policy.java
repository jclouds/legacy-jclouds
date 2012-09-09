/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.fujitsu.fgcp.domain;

import javax.xml.bind.annotation.XmlEnumValue;

import com.google.common.base.Objects;

/**
 * Describes a firewall rule in detail.
 * 
 * @author Dies Koper
 */
public class Policy implements Comparable<Policy> {
    private int id;

    private String src;

    private PolicyType srcType;

    private String srcPort;

    private Service dstService;

    private String dst;

    private PolicyType dstType;

    private String dstPort;

    private Protocol protocol;

    private Action action;

    private Log log;

    enum Service {
        NONE, WSUS, DNS, NTP, @XmlEnumValue("yum")
        YUM, KMS, @XmlEnumValue("Symantec")
        SYMANTEC, RHUI
    }

    enum PolicyType {IP, FQDN, FQDNF}

    enum Protocol {
        @XmlEnumValue("tcp")
        TCP, @XmlEnumValue("udp")
        UDP, @XmlEnumValue("tcp-udp")
        TCP_UDP, @XmlEnumValue("icmp")
        ICMP
    }

    enum Action {
        @XmlEnumValue("Accept")
        ACCEPT, @XmlEnumValue("Deny")
        DENY
    }

    enum Log {
        @XmlEnumValue("On")
        ON, @XmlEnumValue("Off")
        OFF
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @return the srcType
     */
    public PolicyType getSrcType() {
        return srcType;
    }

    /**
     * @return the srcPort
     */
    public String getSrcPort() {
        return srcPort;
    }

    /**
     * @return the dstService
     */
    public Service getDstService() {
        return dstService;
    }

    /**
     * @return the dst
     */
    public String getDst() {
        return dst;
    }

    /**
     * @return the dstType
     */
    public PolicyType getDstType() {
        return dstType;
    }

    /**
     * @return the dstPort
     */
    public String getDstPort() {
        return dstPort;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Policy that = Policy.class.cast(obj);
        return Objects.equal(this.id, that.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("src", src)
                .add("srcType", srcType)
                .add("srcPort", srcPort)
                .add("dstService", dstService)
                .add("dst", dst)
                .add("dstType", dstType)
                .add("dstPort", dstPort)
                .add("protocol", protocol)
                .add("action", action)
                .add("log", log).toString();
    }

    @Override
    public int compareTo(Policy o) {
        return id - o.id;
    }
}
