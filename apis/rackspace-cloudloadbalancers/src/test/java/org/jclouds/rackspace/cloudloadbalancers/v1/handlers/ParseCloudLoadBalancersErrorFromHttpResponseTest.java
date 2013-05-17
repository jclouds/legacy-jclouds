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
package org.jclouds.rackspace.cloudloadbalancers.v1.handlers;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;

import org.jclouds.http.handlers.BaseHttpErrorHandlerTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ParseCloudLoadBalancersErrorFromHttpResponseTest extends
         BaseHttpErrorHandlerTest<ParseCloudLoadBalancersErrorFromHttpResponse> {
   @Test
   public void testNotFound() {
      assertCodeMakes(
               GET,
               URI.create("https://ord.loadbalancers.api.rackspacecloud.com/v1.0/1234/loadbalancers/2000"),
               NOT_FOUND.getStatusCode(),
               "Not Found",
               "<itemNotFound code=\"404\" xmlns=\"http://docs.openstack.org/loadbalancers/api/v1.0\">\n    <message>Object not Found</message>\n</itemNotFound>",
               ResourceNotFoundException.class, "loadbalancers 2000 not found");
   }

   @Override
   protected Class<ParseCloudLoadBalancersErrorFromHttpResponse> getClassToTest() {
      return ParseCloudLoadBalancersErrorFromHttpResponse.class;
   }

}
