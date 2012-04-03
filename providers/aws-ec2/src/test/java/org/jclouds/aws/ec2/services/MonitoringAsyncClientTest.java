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
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.aws.ec2.xml.MonitoringStateHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code MonitoringAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "MonitoringAsyncClientTest")
public class MonitoringAsyncClientTest extends BaseAWSEC2AsyncClientTest<MonitoringAsyncClient> {

   public void testUnmonitorInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = MonitoringAsyncClient.class.getMethod("unmonitorInstancesInRegion", String.class, String.class,
            Array.newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "instance1", "instance2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      String payload = "Action=UnmonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2";
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, payload, "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testMonitorInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = MonitoringAsyncClient.class.getMethod("monitorInstancesInRegion", String.class, String.class,
            Array.newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "instance1", "instance2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=MonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<MonitoringAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<MonitoringAsyncClient>>() {
      };
   }

}
