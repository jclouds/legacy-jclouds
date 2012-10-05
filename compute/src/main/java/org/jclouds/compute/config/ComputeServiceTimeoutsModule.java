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
package org.jclouds.compute.config;
import static com.google.common.base.Predicates.not;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.AtomicImageAvailable;
import org.jclouds.compute.predicates.AtomicImageDeleted;
import org.jclouds.compute.predicates.AtomicNodeRunning;
import org.jclouds.compute.predicates.AtomicNodeSuspended;
import org.jclouds.compute.predicates.AtomicNodeTerminated;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ComputeServiceTimeoutsModule extends AbstractModule {

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<AtomicReference<NodeMetadata>> nodeRunning(
         AtomicNodeRunning statusRunning, Timeouts timeouts, PollPeriod period) {
      return timeouts.nodeRunning == 0 ? 
             statusRunning : 
             new RetryablePredicateGuardingNull<NodeMetadata>(
                   statusRunning, timeouts.nodeRunning, period.pollInitialPeriod, period.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<AtomicReference<NodeMetadata>> serverTerminated(
         AtomicNodeTerminated statusTerminated, Timeouts timeouts, PollPeriod period) {
      return timeouts.nodeTerminated == 0 ? 
             statusTerminated : 
             new RetryablePredicate<AtomicReference<NodeMetadata>>(
                   statusTerminated, timeouts.nodeTerminated, period.pollInitialPeriod, period.pollMaxPeriod);
   }
   

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<AtomicReference<NodeMetadata>> serverSuspended(
         AtomicNodeSuspended statusSuspended, Timeouts timeouts, PollPeriod period) {
      return timeouts.nodeSuspended == 0 ? 
             statusSuspended : 
             new RetryablePredicateGuardingNull<NodeMetadata>(
                   statusSuspended, timeouts.nodeSuspended, period.pollInitialPeriod, period.pollMaxPeriod);
   }
   
   @Provides
   @Singleton
   @Named(TIMEOUT_SCRIPT_COMPLETE)
   protected Predicate<CommandUsingClient> runScriptRunning(ScriptStatusReturnsZero statusRunning, Timeouts timeouts) {
      return timeouts.scriptComplete == 0 ? not(statusRunning) : new RetryablePredicate<CommandUsingClient>(
            not(statusRunning), timeouts.scriptComplete);
   }
   
   @Provides
   @Singleton
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<AtomicReference<Image>> imageAvailable(
         AtomicImageAvailable statusAvailable, Timeouts timeouts, PollPeriod period) {
      return timeouts.imageAvailable == 0 ? 
             statusAvailable : 
             new RetryablePredicateGuardingNull<Image>(
                   statusAvailable, timeouts.imageAvailable, period.pollInitialPeriod, period.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_IMAGE_DELETED)
   protected Predicate<AtomicReference<Image>> serverDeleted(
         AtomicImageDeleted statusDeleted, Timeouts timeouts, PollPeriod period) {
      return timeouts.imageDeleted == 0 ? 
             statusDeleted : 
             new RetryablePredicate<AtomicReference<Image>>(
                   statusDeleted, timeouts.imageDeleted, period.pollInitialPeriod, period.pollMaxPeriod);
   }
   
   @Override
   protected void configure() {

   }

   /**
    * Avoids "losing" the ComputeMetadata if client returns null temporarily (issue #989).
    * Ensures we always pass a non-null to the wrapped predicate, but will propagate the null to
    * the caller qt the end.
    * 
    * @author Aled Sage
    */
   private static class RetryablePredicateGuardingNull<T> implements Predicate<AtomicReference<T>> {
      private class AtomicRefAndOrig {
         private final T orig;
         private final AtomicReference<T> ref;
         
         AtomicRefAndOrig(T orig, AtomicReference<T> ref) {
            this.orig = orig;
            this.ref = ref;
         }
      }
      
      private final RetryablePredicate<AtomicRefAndOrig> retryablePredicate;
      
      public RetryablePredicateGuardingNull(final Predicate<AtomicReference<T>> predicate, long maxWait, long period, long maxPeriod) {
         Predicate<AtomicRefAndOrig> nonNullThingPredicate = new Predicate<AtomicRefAndOrig>() {
            @Override
            public boolean apply(AtomicRefAndOrig input) {
               AtomicReference<T> ref = (input.ref.get() != null) ? input.ref : new AtomicReference<T>(input.orig);
               try {
                  return predicate.apply(ref);
               } finally {
                  input.ref.set(ref.get());
               }
            }
         };
         retryablePredicate = new RetryablePredicate<AtomicRefAndOrig>(nonNullThingPredicate, maxWait, period, maxPeriod);
      }

      @Override
      public boolean apply(AtomicReference<T> input) {
         AtomicRefAndOrig refAndOrig = new AtomicRefAndOrig(input.get(), input);
         return retryablePredicate.apply(refAndOrig);
      }
   }
}
