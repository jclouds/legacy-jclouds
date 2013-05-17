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
package org.jclouds.glesys.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertTrue;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.glesys.GleSYSApi;
import org.jclouds.glesys.features.DomainApi;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code GleSYSApi}
 * 
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "live")
public class BaseGleSYSApiLiveTest extends BaseApiLiveTest<GleSYSApi> {
   protected String hostName = System.getProperty("user.name").replace('.','-').toLowerCase();

   public BaseGleSYSApiLiveTest() {
      provider = "glesys";
   }

   protected void createDomain(String domain) {
      final DomainApi domainApi = api.getDomainApi();
      int before = domainApi.list().size();
      domainApi.create(domain);

      Predicate<Integer> result = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return domainApi.list().size() == value.intValue();
         }
      }, 30, 1, SECONDS);

      assertTrue(result.apply(before + 1));
   }

}
