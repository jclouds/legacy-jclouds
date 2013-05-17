/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public final class LoadBalancerUsage {
   private final int id;
   private final float averageNumConnections;
   private final float averageNumConnectionsSsl;
   private final int incomingTransferInBytes;
   private final int outgoingTransferInBytes;
   private final int incomingTransferSslInBytes;
   private final int outgoingTransferSslInBytes;
   private final int numVIPs;
   private final int numPolls;
   private final Date startTime;
   private final Date endTime;
   private final VirtualIP.Type vipType;
   private final String sslMode;
   private final Optional<String> eventType;

   @ConstructorProperties({ "id", "averageNumConnections", "averageNumConnectionsSsl", "incomingTransfer",
         "outgoingTransfer", "incomingTransferSsl", "outgoingTransferSsl", "numVips", "numPolls", "startTime",
         "endTime", "vipType", "sslMode", "eventType" })
   protected LoadBalancerUsage(int id, float averageNumConnections, float averageNumConnectionsSsl,
         int incomingTransferInBytes, int outgoingTransferInBytes, int incomingTransferSslInBytes,
         int outgoingTransferSslInBytes, int numVIPs, int numPolls, Date startTime, Date endTime, Type vipType,
         String sslMode, String eventType) {
      this.id = id;
      this.averageNumConnections = averageNumConnections;
      this.averageNumConnectionsSsl = averageNumConnectionsSsl;
      this.incomingTransferInBytes = incomingTransferInBytes;
      this.outgoingTransferInBytes = outgoingTransferInBytes;
      this.incomingTransferSslInBytes = incomingTransferSslInBytes;
      this.outgoingTransferSslInBytes = outgoingTransferSslInBytes;
      this.numVIPs = numVIPs;
      this.numPolls = numPolls;
      this.startTime = checkNotNull(startTime, "startTime");
      this.endTime = checkNotNull(endTime, "endTime");
      this.vipType = checkNotNull(vipType, "vipType");
      this.sslMode = checkNotNull(sslMode, "sslMode");
      this.eventType = Optional.fromNullable(eventType);
   }

   public int getId() {
      return id;
   }

   public float getAverageNumConnections() {
      return averageNumConnections;
   }

   public float getAverageNumConnectionsSsl() {
      return averageNumConnectionsSsl;
   }

   public int getIncomingTransferInBytes() {
      return incomingTransferInBytes;
   }

   public int getOutgoingTransferInBytes() {
      return outgoingTransferInBytes;
   }

   public int getIncomingTransferSslInBytes() {
      return incomingTransferSslInBytes;
   }

   public int getOutgoingTransferSslInBytes() {
      return outgoingTransferSslInBytes;
   }

   public int getNumVIPs() {
      return numVIPs;
   }

   public int getNumPolls() {
      return numPolls;
   }

   public Date getStartTime() {
      return startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public VirtualIP.Type getVIPType() {
      return vipType;
   }

   public String getSSLMode() {
      return sslMode;
   }

   public Optional<String> getEventType() {
      return eventType;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      LoadBalancerUsage that = LoadBalancerUsage.class.cast(obj);

      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("averageNumConnections", averageNumConnections)
            .add("averageNumConnectionsSsl", averageNumConnectionsSsl)
            .add("incomingTransferInBytes", incomingTransferInBytes)
            .add("outgoingTransferInBytes", outgoingTransferInBytes)
            .add("incomingTransferSslInBytes", incomingTransferSslInBytes)
            .add("outgoingTransferSslInBytes", outgoingTransferSslInBytes).add("numVIPs", numVIPs)
            .add("numPolls", numPolls).add("startTime", startTime).add("endTime", endTime).add("vipType", vipType)
            .add("sslMode", sslMode).add("eventType", eventType.orNull()).toString();
   }
}
