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
package org.jclouds.cloudstack.features;

import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.options.GenerateUsageRecordsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Tests behavior of {@code GlobalUsageAsyncClient}
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalUsageAsyncClientTest")
public class GlobalUsageAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalUsageAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GlobalUsageAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GlobalUsageAsyncClient>>() {
      };
   }
}
