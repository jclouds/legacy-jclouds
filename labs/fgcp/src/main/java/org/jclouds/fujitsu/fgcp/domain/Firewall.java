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
 * Represents a firewall (FW).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "fw")
public class Firewall {
   private NAT nat;
   private Set<Direction> directions = Sets.newLinkedHashSet();
   private String log;
   private String status;
   private String category;
   private String latestVersion;
   private String comment;
   private boolean firmUpdateExist;
   private boolean configUpdateExist;
   private String backout;
   private String updateDate;
   private String currentVersion;

   /**
    * @return the nat
    */
   public NAT getNat() {
      return nat;
   }

   /**
    * @return the directions
    */
   public Set<Direction> getDirections() {
      return directions == null ? ImmutableSet.<Direction> of()
            : ImmutableSet.copyOf(directions);
   }

   /**
    * @return the log
    */
   public String getLog() {
      return log;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
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
   public String getBackout() {
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

}
