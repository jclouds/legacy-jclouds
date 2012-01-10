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
package org.jclouds.cloudservers.handlers;

import com.google.common.base.Function;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.OpenStackAuthAsyncClient;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "RetryOnRenewExpectTest")
public class RetryOnRenewExpectTest extends BaseRestClientExpectTest<OpenStackAuthAsyncClient> {
   public RetryOnRenewExpectTest() {
      provider = "cloudservers-uk";
   }

//   class RetryFunction implements Function<HttpRequest, HttpResponse> {
//
//   }
//
//   Function<HttpRequest, HttpResponse> retryFunction = new RetryFunction();

   @Test
   public void test401ShouldRetry() {
     // this won't work here...?!
   }
}
