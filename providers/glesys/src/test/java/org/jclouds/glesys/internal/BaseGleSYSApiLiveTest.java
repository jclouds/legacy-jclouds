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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.glesys.GleSYSApi;
import org.jclouds.glesys.GleSYSAsyncApi;
import org.jclouds.glesys.features.DomainApi;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code GleSYSApi}
 * 
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "live")
public class BaseGleSYSApiLiveTest extends BaseComputeServiceContextLiveTest {
   protected String hostName = System.getProperty("user.name").replace('.','-').toLowerCase();

   protected RestContext<GleSYSApi, GleSYSAsyncApi> gleContext;

   public BaseGleSYSApiLiveTest() {
      provider = "glesys";
   }

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      gleContext = view.unwrap();
   }

   protected void createDomain(String domain) {
      final DomainApi api = gleContext.getApi().getDomainApi();
      int before = api.list().size();
      api.create(domain);

      Predicate<Integer> result = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return api.list().size() == value;
         }
      }, 30, 1, SECONDS);

      assertTrue(result.apply(before + 1));
   }

}
