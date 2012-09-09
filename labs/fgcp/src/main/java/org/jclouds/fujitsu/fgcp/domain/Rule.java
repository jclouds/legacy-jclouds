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

import com.google.common.base.Objects;

/**
 * Represents a network address translation or network address port translation
 * rule.
 * <p>
 * A rule either maps a public IP address to the NIC of a virtual server for
 * incoming network traffic, or specifies the public IP address used as source
 * address for traffic from all servers in the virtual system.
 * 
 * @author Dies Koper
 */
public class Rule {
    private String publicIp;
    private String privateIp;
    private boolean snapt;

    /**
     * @return the publicIp
     */
    public String getPublicIp() {
        return publicIp;
    }

    /**
     * @return the privateIp
     */
    public String getPrivateIp() {
        return privateIp;
    }

    /**
     * @return the snapt
     */
    public boolean isSnapt() {
        return snapt;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(privateIp, publicIp, snapt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rule that = Rule.class.cast(obj);
        return Objects.equal(this.privateIp, that.privateIp)
                && Objects.equal(this.publicIp, that.publicIp)
                && Objects.equal(this.snapt, that.snapt);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("privateIp", privateIp).add("publicIp", publicIp)
                .add("snapt", snapt).toString();
    }
}
