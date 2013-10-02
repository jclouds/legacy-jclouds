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
package org.jclouds.rackspace.cloudloadbalancers.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;

import com.google.common.base.Predicate;

/**
 * Tests to see if loadBalancer has reached status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the LoadBalancer status has reached a desired state.
 * This is useful when your LoadBalancer needs to be 100% ready before you can continue with execution.
 * <br/>
 * <pre>
 * {@code
 * LoadBalancer loadBalancer = loadBalancerApi.create(loadBalancerRequest);
 * 
 * RetryablePredicate<String> awaitAvailable = RetryablePredicate.create(
 *    LoadBalancerPredicates.available(loadBalancerApi), 600, 10, 10, TimeUnit.SECONDS);
 * 
 * if (!awaitAvailable.apply(loadBalancer)) {
 *    throw new TimeoutException("Timeout on loadBalancer: " + loadBalancer); 
 * }    
 * }
 * </pre>
 * 
 * You can also use the static convenience methods as so.
 * <br/>
 * <pre>
 * {@code
 * LoadBalancer loadBalancer = loadBalancerApi.create(loadBalancerRequest);
 * 
 * if (!LoadBalancerPredicates.awaitAvailable(loadBalancerApi).apply(loadBalancer)) {
 *    throw new TimeoutException("Timeout on loadBalancer: " + loadBalancer);     
 * }
 * }
 * </pre>
 * 
 * @author Everett Toews
 */
public class LoadBalancerPredicates {
   /**
    * Wait until a LoadBalancer is Available.
    * 
    * @param loadBalancerApi The LoadBalancerApi in the zone where your LoadBalancer resides.
    * @return RetryablePredicate That will check the status every 3 seconds for a maxiumum of 5 minutes.
    */
   public static Predicate<LoadBalancer> awaitAvailable(LoadBalancerApi loadBalancerApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(loadBalancerApi, LoadBalancer.Status.ACTIVE);
      return retry(statusPredicate, 300, 3, 3, SECONDS);
   }
   
   /**
    * Wait until a LoadBalancer no longer exists.
    * 
    * @param loadBalancerApi The LoadBalancerApi in the zone where your LoadBalancer resides.
    * @return RetryablePredicate That will check the whether the LoadBalancer exists 
    * every 3 seconds for a maxiumum of 5 minutes.
    */
   public static Predicate<LoadBalancer> awaitDeleted(LoadBalancerApi loadBalancerApi) {
      DeletedPredicate deletedPredicate = new DeletedPredicate(loadBalancerApi);
      return retry(deletedPredicate, 300, 3, 3, SECONDS);
   }
   
   public static Predicate<LoadBalancer> awaitStatus(
         LoadBalancerApi loadBalancerApi, LoadBalancer.Status status, long maxWaitInSec, long periodInSec) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(loadBalancerApi, status);
      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }
   
   private static class StatusUpdatedPredicate implements Predicate<LoadBalancer> {
      private LoadBalancerApi loadBalancerApi;
      private LoadBalancer.Status status;

      public StatusUpdatedPredicate(LoadBalancerApi loadBalancerApi, LoadBalancer.Status status) {
         this.loadBalancerApi = checkNotNull(loadBalancerApi, "loadBalancerApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the loadBalancer reaches status, false otherwise
       */
      @Override
      public boolean apply(LoadBalancer loadBalancer) {
         checkNotNull(loadBalancer, "loadBalancer must be defined");
         
         LoadBalancer loadBalancerUpdated = loadBalancerApi.get(loadBalancer.getId());
         checkNotNull(loadBalancerUpdated, "LoadBalancer %s not found.", loadBalancer.getId());
         
         return status.equals(loadBalancerUpdated.getStatus());
      }
   }

   private static class DeletedPredicate implements Predicate<LoadBalancer> {
      private LoadBalancerApi loadBalancerApi;

      public DeletedPredicate(LoadBalancerApi loadBalancerApi) {
         this.loadBalancerApi = checkNotNull(loadBalancerApi, "loadBalancerApi must be defined");
      }

      /**
       * @return boolean Return true when the snapshot is deleted, false otherwise
       */
      @Override
      public boolean apply(LoadBalancer loadBalancer) {
         checkNotNull(loadBalancer, "loadBalancer must be defined");
         LoadBalancer loadBalancerUpdate = loadBalancerApi.get(loadBalancer.getId()); 

         if (loadBalancerUpdate == null) {
            return true;
         }
         else {
            return loadBalancerUpdate.getStatus().equals(LoadBalancer.Status.DELETED);
         }
      }
   }
}
