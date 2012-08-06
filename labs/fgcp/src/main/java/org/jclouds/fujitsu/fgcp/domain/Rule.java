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
}
