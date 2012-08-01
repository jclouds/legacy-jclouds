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

import java.util.Set;

/**
 * Grouping of firewall rules pertaining to a particular direction in network
 * traffic, e.g. from the Internet to a server in the DMZ zone, or from a server
 * in the SECURE2 zone to the SECURE1 zone, etc.
 * 
 * @author Dies Koper
 */
public class Direction {
    private String from;
    private String to;
    private Set<Policy> policies;
    private String acceptable;
    private String prefix;
    private String maxPolicyNum;

}
