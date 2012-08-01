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
 * Describes attributes of a software load balancer's (SLB) configuration.
 * 
 * @author Dies Koper
 */
public class Group {
    private String id;

    private String protocol;

    private String port1;

    private String port2;

    private String balanceType;

    private String uniqueType;

    private String monitorType;

    private String maxConnection;

    private String uniqueRetention;

    private String interval;

    private String timeout;

    private String retryCount;

    private String certNum;

    private Set<Cause> causes;

    private String recoveryAction;

    private Set<Target> targets;

    private String validity;
}
