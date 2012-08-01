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
package org.jclouds.fujitsu.fgcp.compute.strategy;

import java.util.Set;

import org.jclouds.compute.domain.Template;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;

/**
 * Holds metadata on a virtual server, both static (name, id, type, etc.) and
 * dynamic (status, mapped public IPs, etc.).
 * 
 * @author Dies Koper
 */
public class VServerMetadata {

    protected VServer server;
    protected String initialPassword;
    protected VServerStatus status = VServerStatus.UNRECOGNIZED;
    protected Set<PublicIP> ips;

    public VServerMetadata(String id, String name, Template template) {
        this.status = VServerStatus.DEPLOYING;
        server = new VServerWithDetails();
        server.setId(id);
        server.setName(name);
        server.setType(template.getHardware().getId());
        server.setDiskimageId(template.getImage().getId());
    }

    /*
     * public VServerMetadata(String initialPassword, VServerStatus status,
     * Set<PublicIP> ips) { // this(initialPassword, status); this.ips = ips; }
     */

    public VServerMetadata(VServer server) {
        this.server = server;
    }

    public VServer getServer() {
        return server;
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public VServerStatus getStatus() {
        return status;
    }

    public Set<PublicIP> getIps() {
        return ips;
    }

}
