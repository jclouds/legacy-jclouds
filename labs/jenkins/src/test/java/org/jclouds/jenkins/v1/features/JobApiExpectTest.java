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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.jenkins.v1.JenkinsApi;
import org.jclouds.jenkins.v1.internal.BaseJenkinsApiExpectTest;
import org.jclouds.jenkins.v1.parse.LastBuildTest;
import org.jclouds.jenkins.v1.parse.ParseJobDetailsTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JobApiExpectTest")
public class JobApiExpectTest extends BaseJenkinsApiExpectTest {

   public void testCreateJobStringWhenResponseIs2xx() throws IOException {
      HttpRequest createJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/createItem?name=blagoo")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .payload(payloadFromResourceWithContentType("/sample_job.xml", "text/xml"))
            .build();

      HttpResponse createJobResponse = HttpResponse.builder().statusCode(200).build();
   
      JenkinsApi createJobWhenCreated = requestSendsResponse(createJob, createJobResponse);

      createJobWhenCreated.getJobApi().createFromXML("blagoo", Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   public void testDeleteJobWhenResponseIs2xx() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/blagoo/doDelete")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(200).build();

      JenkinsApi deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobApi().delete("blagoo");
   }
   
   public void testDeleteJobWhenResponseIs404() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/blagoo/doDelete")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(404).build();

      JenkinsApi deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobApi().delete("blagoo");
   }
   
   HttpRequest getJob = HttpRequest.builder()
         .method("GET")
         .endpoint("http://localhost:8080/job/ddd/api/json")
         .addHeader("Accept", "application/json")
         .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
         .build();

   public void testGetJobWhenResponseIs2xx() {
      HttpResponse getJobResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/job.json")).build();
      JenkinsApi apiWhenJobExists = requestSendsResponse(getJob, getJobResponse);
      assertEquals(apiWhenJobExists.getJobApi().get("ddd").toString(),
               new ParseJobDetailsTest().expected().toString());
   }
   
   public void testGetJobWhenResponseIs404() {
      HttpResponse getJobResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsApi getJobWhenGetd = requestSendsResponse(getJob, getJobResponse);
      assertNull(getJobWhenGetd.getJobApi().get("ddd"));
   }

   HttpRequest buildJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/ddd/build")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();
   
   public void testBuildJobWhenResponseIs2xx() {
      HttpResponse buildJobResponse = HttpResponse.builder().statusCode(200).build();
      JenkinsApi apiWhenJobExists = requestSendsResponse(buildJob, buildJobResponse);
      apiWhenJobExists.getJobApi().build("ddd");
   }
   
   public void testBuildJobWhenResponseIs404() {
      HttpResponse getJobResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsApi getJobWhenGetd = requestSendsResponse(buildJob, getJobResponse);
      getJobWhenGetd.getJobApi().build("ddd");
   }
   
   HttpRequest fetchConfig = HttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost:8080/job/ddd/config.xml")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();
   
   public void testFetchConfigXMLWhenResponseIs2xx() {
      HttpResponse fetchConfigResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/sample_job.xml", "text/xml")).build();
      JenkinsApi apiWhenJobExists = requestSendsResponse(fetchConfig, fetchConfigResponse);
      String configXML = apiWhenJobExists.getJobApi().fetchConfigXML("ddd");
      //TODO enable this assertion
      //assertEquals(configXML, Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   public void testFetchConfigXMLWhenResponseIs404() {
      HttpResponse fetchConfigResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsApi getJobWhenGetd = requestSendsResponse(fetchConfig, fetchConfigResponse);
      getJobWhenGetd.getJobApi().fetchConfigXML("ddd");
   }
   
   
   HttpRequest lastBuild = HttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost:8080/job/ddd/lastBuild/api/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();
   
   public void testLastBuildWhenResponseIs2xx() {
      HttpResponse lastBuildResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/lastBuild.json")).build();
      JenkinsApi apiWhenJobExists = requestSendsResponse(lastBuild, lastBuildResponse);
      assertEquals(apiWhenJobExists.getJobApi().lastBuild("ddd").toString(),
               new LastBuildTest().expected().toString());
   }
   
   public void testLastBuildWhenResponseIs404() {
      HttpResponse lastBuildResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsApi getJobWhenGetd = requestSendsResponse(lastBuild, lastBuildResponse);
      assertNull(getJobWhenGetd.getJobApi().lastBuild("ddd"));
   }
}
