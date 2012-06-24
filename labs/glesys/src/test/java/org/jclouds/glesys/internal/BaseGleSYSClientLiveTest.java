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
package org.jclouds.glesys.internal;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.features.DomainClient;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code GleSYSClient}
 * 
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "live")
public class BaseGleSYSClientLiveTest extends BaseComputeServiceContextLiveTest {

   protected RestContext<GleSYSClient, GleSYSAsyncClient> gleContext;

   public BaseGleSYSClientLiveTest() {
      provider = "glesys";
   }

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      gleContext = view.unwrap();
   }

   protected void createDomain(String domain) {
      final DomainClient client = gleContext.getApi().getDomainClient();
      int before = client.listDomains().size();
      client.addDomain(domain);
      RetryablePredicate<Integer> result = new RetryablePredicate<Integer>(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return client.listDomains().size() == value;
         }
      }, 30, 1, TimeUnit.SECONDS);

      assertTrue(result.apply(before + 1));
   }

}
