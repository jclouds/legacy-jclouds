/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.xml.MonitoringStateHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code MonitoringAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.MonitoringAsyncClientTest")
public class MonitoringAsyncClientTest extends BaseEC2AsyncClientTest<MonitoringAsyncClient> {

   public void testUnmonitorInstances() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = MonitoringAsyncClient.class.getMethod("unmonitorInstancesInRegion",
               Region.class, String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<MonitoringAsyncClient> httpMethod = processor.createRequest(method,
               null, "instance1", "instance2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 67\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=UnmonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testMonitorInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = MonitoringAsyncClient.class.getMethod("monitorInstancesInRegion",
               Region.class, String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<MonitoringAsyncClient> httpMethod = processor.createRequest(method,
               null, "instance1", "instance2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 65\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=MonitorInstances&InstanceId.0=instance1&InstanceId.1=instance2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, MonitoringStateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<MonitoringAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<MonitoringAsyncClient>>() {
      };
   }

}
