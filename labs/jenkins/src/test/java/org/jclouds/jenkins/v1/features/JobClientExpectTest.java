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
package org.jclouds.jenkins.v1.features;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.jenkins.v1.JenkinsClient;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JobClientExpectTest")
public class JobClientExpectTest extends BaseJenkinsClientExpectTest {

   public void testCreateJobWhenResponseIs2xx() {
      HttpRequest createJob = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:8080/createItem?name=blagoo"))
            .headers(ImmutableMultimap.<String, String> builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
            .payload(payloadFromResourceWithContentType("/sample_job.xml", "text/xml"))
            .build();

      HttpResponse createJobResponse = HttpResponse.builder().statusCode(200).build();

      JenkinsClient createJobWhenCreated = requestSendsResponse(createJob, createJobResponse);

      createJobWhenCreated.getJobClient().createFromXML("blagoo", payloadFromResource("/sample_job.xml"));
   }
   
   public void testDeleteJobWhenResponseIs2xx() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:8080/job/blagoo/doDelete"))
            .headers(ImmutableMultimap.<String, String> builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(200).build();

      JenkinsClient deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobClient().delete("blagoo");
   }
   
   public void testDeleteJobWhenResponseIs404() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:8080/job/blagoo/doDelete"))
            .headers(ImmutableMultimap.<String, String> builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(404).build();

      JenkinsClient deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobClient().delete("blagoo");
   }
}
