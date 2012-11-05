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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a software load balancer.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "slb")
public class SLB extends BuiltinServer {
   private String ipAddress;

   private Set<IntermediateCACert> ccacerts = Sets.newLinkedHashSet();

   private Set<ServerCert> servercerts = Sets.newLinkedHashSet();

   private Set<Group> groups;

   private String srcType;

   private String srcPort;

   private String status;

   private ErrorStatistics errorStatistics;

   private LoadStatistics loadStatistics;

   private String category;

   private String latestVersion;

   private String comment;

   private boolean firmUpdateExist;

   private boolean configUpdateExist;

   private boolean backout;

   private String updateDate;

   private String currentVersion;

   private String webAccelerator;

   /**
    * @return the ipAddress
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @return the ccacerts
    */
   public Set<IntermediateCACert> getCcacerts() {
      return ccacerts == null ? ImmutableSet.<IntermediateCACert> of()
            : ImmutableSet.copyOf(ccacerts);
   }

   /**
    * @return the servercerts
    */
   public Set<ServerCert> getServercerts() {
      return servercerts == null ? ImmutableSet.<ServerCert> of()
            : ImmutableSet.copyOf(servercerts);
   }

   /**
    * @return the groups
    */
   public Set<Group> getGroups() {
      return groups == null ? ImmutableSet.<Group> of() : ImmutableSet
            .copyOf(groups);
   }

   /**
    * @return the srcType
    */
   public String getSrcType() {
      return srcType;
   }

   /**
    * @return the srcPort
    */
   public String getSrcPort() {
      return srcPort;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the errorStatistics
    */
   public ErrorStatistics getErrorStatistics() {
      return errorStatistics;
   }

   /**
    * @return the loadStatistics
    */
   public LoadStatistics getLoadStatistics() {
      return loadStatistics;
   }

   /**
    * @return the category
    */
   public String getCategory() {
      return category;
   }

   /**
    * @return the latestVersion
    */
   public String getLatestVersion() {
      return latestVersion;
   }

   /**
    * @return the comment
    */
   public String getComment() {
      return comment;
   }

   /**
    * @return the firmUpdateExist
    */
   public boolean getFirmUpdateExist() {
      return firmUpdateExist;
   }

   /**
    * @return the configUpdateExist
    */
   public boolean getConfigUpdateExist() {
      return configUpdateExist;
   }

   /**
    * @return the backout
    */
   public boolean getBackout() {
      return backout;
   }

   /**
    * @return the updateDate
    */
   public String getUpdateDate() {
      return updateDate;
   }

   /**
    * @return the currentVersion
    */
   public String getCurrentVersion() {
      return currentVersion;
   }

   /**
    * @return the webAccelerator
    */
   public String getWebAccelerator() {
      return webAccelerator;
   }

}
