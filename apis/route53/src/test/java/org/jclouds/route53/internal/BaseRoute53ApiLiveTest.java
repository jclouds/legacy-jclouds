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
package org.jclouds.route53.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.route53.domain.Change.Status.INSYNC;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.route53.Route53Api;
import org.jclouds.route53.domain.Change;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseRoute53ApiLiveTest extends BaseApiLiveTest<Route53Api> {

   public BaseRoute53ApiLiveTest() {
      provider = "route53";
   }

   protected Predicate<Change> inSync;

   @BeforeClass(groups = "live")
   @Override
   public void setup() {
      super.setup();
      inSync = retry(new Predicate<Change>() {
         public boolean apply(Change input) {
            Change change = api.getChange(input.getId());
            return change != null && change.getStatus() == INSYNC;
         }
      }, 600, 1, 5, SECONDS);
   }
}
