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

import javax.xml.bind.annotation.XmlEnumValue;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Describes attributes of a software load balancer's (SLB) configuration.
 * 
 * @author Dies Koper
 */
public class Group {
   private int id;

   private String protocol;

   private int port1;

   private int port2;

   private String balanceType;

   private String uniqueType;

   private String monitorType;

   private int maxConnection;

   private int uniqueRetention;

   private int interval;

   private int timeout;

   private int retryCount;

   private int certNum;

   private Set<Cause> causes;

   private RecoveryAction recoveryAction;

   private Set<Target> targets = Sets.newLinkedHashSet();

   private String validity;

   enum RecoveryAction {
      @XmlEnumValue("switch-back")
      SWITCH_BACK, @XmlEnumValue("maintenance")
      MAINTENANCE
   }

   /**
    * @return the id
    */
   public int getId() {
      return id;
   }

   /**
    * @return the protocol
    */
   public String getProtocol() {
      return protocol;
   }

   /**
    * @return the port1
    */
   public int getPort1() {
      return port1;
   }

   /**
    * @return the port2
    */
   public int getPort2() {
      return port2;
   }

   /**
    * @return the balanceType
    */
   public String getBalanceType() {
      return balanceType;
   }

   /**
    * @return the uniqueType
    */
   public String getUniqueType() {
      return uniqueType;
   }

   /**
    * @return the monitorType
    */
   public String getMonitorType() {
      return monitorType;
   }

   /**
    * @return the maxConnection
    */
   public int getMaxConnection() {
      return maxConnection;
   }

   /**
    * @return the uniqueRetention
    */
   public int getUniqueRetention() {
      return uniqueRetention;
   }

   /**
    * @return the interval
    */
   public int getInterval() {
      return interval;
   }

   /**
    * @return the timeout
    */
   public int getTimeout() {
      return timeout;
   }

   /**
    * @return the retryCount
    */
   public int getRetryCount() {
      return retryCount;
   }

   /**
    * @return the certNum
    */
   public int getCertNum() {
      return certNum;
   }

   /**
    * @return the causes
    */
   public Set<Cause> getCauses() {
      return causes == null ? ImmutableSet.<Cause> of() : ImmutableSet
            .copyOf(causes);
   }

   /**
    * @return the recoveryAction
    */
   public RecoveryAction getRecoveryAction() {
      return recoveryAction;
   }

   /**
    * @return the targets
    */
   public Set<Target> getTargets() {
      return targets == null ? ImmutableSet.<Target> of() : ImmutableSet
            .copyOf(targets);
   }

   /**
    * @return the validity
    */
   public String getValidity() {
      return validity;
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
      Group that = Group.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("protocol", protocol).add("port1", port1)
            .add("port2", port2).add("balanceType", balanceType)
            .add("uniqueType", uniqueType).add("monitorType", monitorType)
            .add("maxConnection", maxConnection)
            .add("uniqueRetention", uniqueRetention)
            .add("interval", interval).add("timeout", timeout)
            .add("retryCount", retryCount).add("certNum", certNum)
            .add("causes", causes).add("recoveryAction", recoveryAction)
            .add("targets", targets).add("validity", validity).toString();
   }
}
