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
package org.jclouds.rackspace.clouddns.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class CloudDNSApiExpectTest extends BaseCloudDNSApiExpectTest<CloudDNSApi> {
   public void testGetJobDomainExport() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/status/bfbd6ec8-5d4c-49f8-97b5-aa5bfd3e95a4?showDetails=true");
      CloudDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/job.json")).build());

      Job<Domain> job = api.getJob("bfbd6ec8-5d4c-49f8-97b5-aa5bfd3e95a4");
      assertEquals(job.getStatus(), Job.Status.RUNNING);
      assertFalse(job.getResource().isPresent());
   }
}
