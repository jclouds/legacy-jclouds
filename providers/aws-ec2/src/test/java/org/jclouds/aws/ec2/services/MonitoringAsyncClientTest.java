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
package org.jclouds.aws.ec2.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.aws.ec2.xml.MonitoringStateHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code MonitoringAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "MonitoringAsyncClientTest")
public class MonitoringAsyncClientTest extends BaseAWSEC2AsyncClientTest<MonitoringAsyncClient> {

   public void testUnmonitorInstances() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(MonitoringAsyncClient.class, "unmonitorInstancesInRegion", String.class, String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "instance1", "instance2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      String payload = "Action=UnmonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2";
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, payload, "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testMonitorInstances() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(MonitoringAsyncClient.class, "monitorInstancesInRegion", String.class, String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "instance1", "instance2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=MonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
