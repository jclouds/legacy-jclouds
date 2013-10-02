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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * @author Everett Toews
 */
public final class LoadBalancerStats {
   private final int connectTimeOut;
   private final int connectError;
   private final int connectFailure;
   private final int dataTimedOut;
   private final int keepAliveTimedOut;
   private final int maxConn;

   @ConstructorProperties({ "connectTimeOut", "connectError", "connectFailure", "dataTimedOut", "keepAliveTimedOut",
         "maxConn" })
   protected LoadBalancerStats(int connectTimeOut, int connectError, int connectFailure, int dataTimedOut,
         int keepAliveTimedOut, int maxConn) {
      this.connectTimeOut = connectTimeOut;
      this.connectError = connectError;
      this.connectFailure = connectFailure;
      this.dataTimedOut = dataTimedOut;
      this.keepAliveTimedOut = keepAliveTimedOut;
      this.maxConn = maxConn;
   }

   /**
    * Connections closed by this load balancer because the 'connect_timeout' interval was exceeded.
    */
   public int getConnectTimeOut() {
      return connectTimeOut;
   }

   /**
    * Number of transaction or protocol errors in this load balancer.
    */
   public int getConnectError() {
      return connectError;
   }

   /**
    * Number of connection failures in this load balancer.
    */
   public int getConnectFailure() {
      return connectFailure;
   }

   /**
    * Connections closed by this load balancer because the 'timeout' interval was exceeded.
    */
   public int getDataTimedOut() {
      return dataTimedOut;
   }

   /**
    * Connections closed by this load balancer because the 'keepalive_timeout' interval was exceeded.
    */
   public int getKeepAliveTimedOut() {
      return keepAliveTimedOut;
   }

   /**
    * Maximum number of simultaneous TCP connections this load balancer has processed at any one time.
    */
   public int getMaxConn() {
      return maxConn;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("connectTimeOut", connectTimeOut)
            .add("connectError", connectError).add("connectFailure", connectFailure).add("dataTimedOut", dataTimedOut)
            .add("keepAliveTimedOut", keepAliveTimedOut).add("maxConn", maxConn).toString();
   }
}
