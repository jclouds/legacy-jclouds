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
   
   public void deleteImageReturnsTrueOn200AndFalseOn404() {
      
      HttpRequest deleteImage11 = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/11?now=1257695648897")).headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-Token", authToken).build()).build();
      
      HttpResponse imageDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudServersClient clientWhenImageExists = requestsSendResponses(initialAuth, responseWithAuth, deleteImage11, imageDeleted);
      assert clientWhenImageExists.deleteImage(11);

      HttpResponse imageNotFound = HttpResponse.builder().statusCode(404).message("HTTP/1.1 404 Not Found").build();

      CloudServersClient clientWhenImageDoesntExist =  requestsSendResponses(initialAuth, responseWithAuth, deleteImage11, imageNotFound);
      assert !clientWhenImageDoesntExist.deleteImage(11);
      
   }

}
