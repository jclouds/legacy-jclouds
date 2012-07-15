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
import org.jclouds.jenkins.v1.JenkinsClient;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientExpectTest;
import org.jclouds.jenkins.v1.parse.LastBuildTest;
import org.jclouds.jenkins.v1.parse.ParseJobDetailsTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JobClientExpectTest")
public class JobClientExpectTest extends BaseJenkinsClientExpectTest {

   public void testCreateJobStringWhenResponseIs2xx() throws IOException {
      HttpRequest createJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/createItem?name=blagoo")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .payload(payloadFromResourceWithContentType("/sample_job.xml", "text/xml"))
            .build();

      HttpResponse createJobResponse = HttpResponse.builder().statusCode(200).build();
   
      JenkinsClient createJobWhenCreated = requestSendsResponse(createJob, createJobResponse);

      createJobWhenCreated.getJobClient().createFromXML("blagoo", Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   public void testDeleteJobWhenResponseIs2xx() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/blagoo/doDelete")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(200).build();

      JenkinsClient deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobClient().delete("blagoo");
   }
   
   public void testDeleteJobWhenResponseIs404() {
      HttpRequest deleteJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/blagoo/doDelete")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();

      HttpResponse deleteJobResponse = HttpResponse.builder().statusCode(404).build();

      JenkinsClient deleteJobWhenDeleted = requestSendsResponse(deleteJob, deleteJobResponse);

      deleteJobWhenDeleted.getJobClient().delete("blagoo");
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
      JenkinsClient clientWhenJobExists = requestSendsResponse(getJob, getJobResponse);
      assertEquals(clientWhenJobExists.getJobClient().get("ddd").toString(),
               new ParseJobDetailsTest().expected().toString());
   }
   
   public void testGetJobWhenResponseIs404() {
      HttpResponse getJobResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsClient getJobWhenGetd = requestSendsResponse(getJob, getJobResponse);
      assertNull(getJobWhenGetd.getJobClient().get("ddd"));
   }

   HttpRequest buildJob = HttpRequest.builder()
            .method("POST")
            .endpoint("http://localhost:8080/job/ddd/build")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();
   
   public void testBuildJobWhenResponseIs2xx() {
      HttpResponse buildJobResponse = HttpResponse.builder().statusCode(200).build();
      JenkinsClient clientWhenJobExists = requestSendsResponse(buildJob, buildJobResponse);
      clientWhenJobExists.getJobClient().build("ddd");
   }
   
   public void testBuildJobWhenResponseIs404() {
      HttpResponse getJobResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsClient getJobWhenGetd = requestSendsResponse(buildJob, getJobResponse);
      getJobWhenGetd.getJobClient().build("ddd");
   }
   
   HttpRequest fetchConfig = HttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost:8080/job/ddd/config.xml")
            .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
            .build();
   
   public void testFetchConfigXMLWhenResponseIs2xx() {
      HttpResponse fetchConfigResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/sample_job.xml", "text/xml")).build();
      JenkinsClient clientWhenJobExists = requestSendsResponse(fetchConfig, fetchConfigResponse);
      String configXML = clientWhenJobExists.getJobClient().fetchConfigXML("ddd");
      //TODO enable this assertion
      //assertEquals(configXML, Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   public void testFetchConfigXMLWhenResponseIs404() {
      HttpResponse fetchConfigResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsClient getJobWhenGetd = requestSendsResponse(fetchConfig, fetchConfigResponse);
      getJobWhenGetd.getJobClient().fetchConfigXML("ddd");
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
      JenkinsClient clientWhenJobExists = requestSendsResponse(lastBuild, lastBuildResponse);
      assertEquals(clientWhenJobExists.getJobClient().lastBuild("ddd").toString(),
               new LastBuildTest().expected().toString());
   }
   
   public void testLastBuildWhenResponseIs404() {
      HttpResponse lastBuildResponse = HttpResponse.builder().statusCode(404).build();
      JenkinsClient getJobWhenGetd = requestSendsResponse(lastBuild, lastBuildResponse);
      assertNull(getJobWhenGetd.getJobClient().lastBuild("ddd"));
   }
}
