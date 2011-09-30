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
package org.jclouds.compute.predicates;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Predicate;

/**
 * 
 * 
 * Not singleton as seconds are mutable
 * 
 * @author Adrian Cole
 * 
 */
public class RetryIfSocketNotYetOpen implements Predicate<IPSocket> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;
   private final SocketOpen socketTester;
   private long timeoutValue;
   private TimeUnit timeoutUnits;


   public RetryIfSocketNotYetOpen(SocketOpen socketTester, Logger logger, long timeoutValue, TimeUnit timeoutUnits) {
      this.socketTester = socketTester;
      this.logger = logger;
      this.timeoutValue = timeoutValue;
      this.timeoutUnits = timeoutUnits;
   }
   
   @Inject
   public RetryIfSocketNotYetOpen(SocketOpen socketTester, Timeouts timeouts) {
      this(socketTester, Logger.NULL, timeouts.portOpen, TimeUnit.MILLISECONDS);
   }

   /** @deprecated in favor of specifying explicit time units */
   @Deprecated
   public RetryIfSocketNotYetOpen(SocketOpen socketTester, Logger logger, long seconds) {
      this(socketTester, logger, seconds, TimeUnit.SECONDS);
   }

   public RetryIfSocketNotYetOpen milliseconds(long milliseconds) {
       this.timeoutValue = milliseconds;
       this.timeoutUnits = TimeUnit.MILLISECONDS;
       return this;
   }
   
   public RetryIfSocketNotYetOpen seconds(long seconds) {
      this.timeoutValue = seconds;
      this.timeoutUnits = TimeUnit.SECONDS;
      return this;
   }

   @Override
   public String toString() {
      return "retryIfSocketNotYetOpen(" + timeoutValue + " "+ timeoutUnits + ")";
   }

   @Override
   public boolean apply(IPSocket socket) {
      logger.debug(">> blocking on socket %s for %d %s", socket, timeoutValue, timeoutUnits);
      RetryablePredicate<IPSocket> tester = new RetryablePredicate<IPSocket>(socketTester, timeoutValue, 1, timeoutUnits);
      boolean passed = tester.apply(socket);
      if (passed)
         logger.debug("<< socket %s opened", socket);
      else
         logger.warn("<< socket %s didn't open after %d %s", socket, timeoutValue, timeoutUnits);
      return passed;
   }
}