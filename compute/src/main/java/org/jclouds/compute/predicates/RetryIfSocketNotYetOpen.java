/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
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
   private long seconds;

   public RetryIfSocketNotYetOpen seconds(long seconds) {
      this.seconds = seconds;
      return this;
   }

   @Inject
   public RetryIfSocketNotYetOpen(SocketOpen socketTester, Timeouts timeouts) {
      this.socketTester = socketTester;
      this.seconds = timeouts.portOpen;
   }

   public RetryIfSocketNotYetOpen(SocketOpen socketTester, Logger logger, long seconds) {
      this.socketTester = socketTester;
      this.logger = logger;
      this.seconds = seconds;
   }

   @Override
   public String toString() {
      return "retryIfSocketNotYetOpen(" + seconds + ")";
   }

   @Override
   public boolean apply(IPSocket socket) {
      logger.debug(">> blocking on socket %s for %d seconds", socket, seconds);
      RetryablePredicate<IPSocket> tester = new RetryablePredicate<IPSocket>(socketTester, seconds, 1, TimeUnit.SECONDS);
      boolean passed = tester.apply(socket);
      if (passed)
         logger.debug("<< socket %s opened", socket);
      else
         logger.warn("<< socket %s didn't open after %d seconds", socket, seconds);
      return passed;
   }
}