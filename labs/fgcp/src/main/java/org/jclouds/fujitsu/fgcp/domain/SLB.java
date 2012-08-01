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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a software load balancer.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "slb")
public class SLB {
    private String ipAddress;

    private Set<IntermediateCACert> ccacerts;

    private Set<ServerCert> servercerts;

    private Set<Group> groups;

    private String srcType;

    private String srcPort;

    private String status;

    private ErrorStatistics errorStatistics;

    private LoadStatistics loadStatistics;

    private String category;

    private String latestVersion;

    private String comment;

    private String firmUpdateExist;

    private String configUpdateExist;

    private String backout;

    private String updateDate;

    private String currentVersion;

    private String webAccelerator;

}
