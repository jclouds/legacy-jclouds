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
package org.jclouds.cloudwatch.binders;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link AlarmNamesBinder}.
 *
 * @author Jeremy Whitlock
 */
@Test(groups = "unit", testName = "AlarmNamesBinderTest")
public class AlarmNamesBinderTest {

   Injector injector = Guice.createInjector();
   AlarmNamesBinder binder = injector.getInstance(AlarmNamesBinder.class);

   HttpRequest request() {
      return HttpRequest.builder().method("POST").endpoint("http://localhost").build();
   }

   public void testAlarmNamesBinder() throws Exception {
      HttpRequest request = binder.bindToRequest(request(), ImmutableSet.of("TestAlarmName1", "TestAlarmName2"));

      Assert.assertEquals(request.getPayload().getRawContent(),
                          "AlarmNames.member.1=TestAlarmName1&AlarmNames.member.2=TestAlarmName2");
   }

}
