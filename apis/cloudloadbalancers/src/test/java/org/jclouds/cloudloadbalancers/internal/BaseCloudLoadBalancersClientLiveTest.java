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
package org.jclouds.cloudloadbalancers.internal;

import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseWrapperLiveTest;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersAsyncClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersClient;
import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.predicates.LoadBalancerActive;
import org.jclouds.cloudloadbalancers.predicates.LoadBalancerDeleted;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudLoadBalancersClientLiveTest extends BaseWrapperLiveTest<LoadBalancerServiceContext> {

   public BaseCloudLoadBalancersClientLiveTest() {
      provider = "cloudloadbalancers";
   }

   protected CloudLoadBalancersClient client;
   protected RestContext<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient> lbContext;
   protected String[] regions = {};
   protected Predicate<IPSocket> socketTester;
   protected RetryablePredicate<LoadBalancer> loadBalancerActive;
   protected RetryablePredicate<LoadBalancer> loadBalancerDeleted;

   protected Injector injector;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      lbContext = wrapper.unwrap();

      client = lbContext.getApi();

      injector = Guice.createInjector(new Log4JLoggingModule());
      loadBalancerActive = new RetryablePredicate<LoadBalancer>(new LoadBalancerActive(client), 300, 1, 1,
               TimeUnit.SECONDS);
      injector.injectMembers(loadBalancerActive);
      loadBalancerDeleted = new RetryablePredicate<LoadBalancer>(new LoadBalancerDeleted(client), 300, 1, 1,
               TimeUnit.SECONDS);
      injector.injectMembers(loadBalancerDeleted);
   }

   @Override
   protected TypeToken<LoadBalancerServiceContext> wrapperType() {
      return TypeToken.of(LoadBalancerServiceContext.class);
   }

}
