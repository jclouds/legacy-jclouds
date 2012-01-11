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
package org.jclouds.cloudservers;

import java.net.URI;

import org.jclouds.cloudservers.internal.BaseCloudServersRestClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CloudServersExpectTest")
public class CloudServersExpectTest extends BaseCloudServersRestClientExpectTest {
   
   HttpRequest initialAuth = HttpRequest.builder().method("GET").endpoint(URI.create("https://auth/v1.0"))
            .headers(
            ImmutableMultimap.<String, String> builder()
            .put("X-Auth-User", "identity")
            .put("X-Auth-Key", "credential")
            .put("Accept", "*/*").build()).build();
   
   String authToken = "d6245d35-22a0-47c0-9770-2c5097da25fc";
   
   HttpResponse responseWithUrls = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content")
            .headers(ImmutableMultimap.<String,String>builder()
            .put("Server", "Apache/2.2.3 (Red Hat)")
            .put("vary", "X-Auth-Token,X-Auth-Key,X-Storage-User,X-Storage-Pass")
            .put("X-Storage-Url", "https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
            .put("Cache-Control", "s-maxage=86399")
            .put("Content-Type", "text/xml")
            .put("Date", "Tue, 10 Jan 2012 22:08:47 GMT")
            .put("X-Auth-Token", authToken)
            .put("X-Server-Management-Url","https://servers.api.rackspacecloud.com/v1.0/413274")
            .put("X-Storage-Token", authToken)
            .put("Connection", "Keep-Alive")
            .put("X-CDN-Management-Url", "https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
            .put("Content-Length", "0")
            .build()).build();
   
   public void deleteImageReturnsTrueOn200AndFalseOn404() {
      
      HttpRequest deleteImage11 = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://servers.api.rackspacecloud.com/v1.0/413274/images/11?now=1257695648897")).headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-Token", authToken).build()).build();
      
      HttpResponse imageDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudServersClient clientWhenImageExists = requestsSendResponses(initialAuth, responseWithUrls, deleteImage11, imageDeleted);
      assert clientWhenImageExists.deleteImage(11);

      HttpResponse imageNotFound = HttpResponse.builder().statusCode(404).message("HTTP/1.1 404 Not Found").build();

      CloudServersClient clientWhenImageDoesntExist =  requestsSendResponses(initialAuth, responseWithUrls, deleteImage11, imageNotFound);
      assert !clientWhenImageDoesntExist.deleteImage(11);
      
   }

}