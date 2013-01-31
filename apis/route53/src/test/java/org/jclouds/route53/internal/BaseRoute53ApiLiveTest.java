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
package org.jclouds.route53.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.route53.domain.Change.Status.INSYNC;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.route53.Route53ApiMetadata;
import org.jclouds.route53.Route53AsyncApi;
import org.jclouds.route53.Route53Api;
import org.jclouds.route53.domain.Change;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseRoute53ApiLiveTest extends
         BaseContextLiveTest<RestContext<? extends Route53Api, ? extends Route53AsyncApi>> {

   public BaseRoute53ApiLiveTest() {
      provider = "route53";
   }

   protected Predicate<Change> inSync;

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
      inSync = retry(new Predicate<Change>() {
         public boolean apply(Change input) {
            return context.getApi().getChange(input.getId()).getStatus() == INSYNC;
         }
      }, 600, 1, 5, SECONDS);
   }

   @Override
   protected TypeToken<RestContext<? extends Route53Api, ? extends Route53AsyncApi>> contextType() {
      return Route53ApiMetadata.CONTEXT_TOKEN;
   }
}
