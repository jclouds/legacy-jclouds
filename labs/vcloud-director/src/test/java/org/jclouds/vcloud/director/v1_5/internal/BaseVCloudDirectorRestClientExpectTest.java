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
package org.jclouds.vcloud.director.v1_5.internal;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.ImmutableMultimap;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseVCloudDirectorRestClientExpectTest extends BaseRestClientExpectTest<VCloudDirectorClient> {

   public static final String user = "adrian@jclouds.org";
   public static final String org = "JClouds";
   public static final String password = "password";
   public static final String token = "mIaR3/6Lna8DWImd7/JPR5rK8FcUHabt+G/UCJV5pJQ=";

   protected HttpRequest loginRequest = HttpRequest.builder().method("POST").endpoint(
            URI.create("http://localhost/api/sessions")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "*/*").put("Authorization",
                     "Basic YWRyaWFuQGpjbG91ZHMub3JnQEpDbG91ZHM6cGFzc3dvcmQ=").build()).build();

   protected HttpResponse sessionResponse = HttpResponse.builder().statusCode(200).headers(
            ImmutableMultimap.<String, String> builder().put("x-vcloud-authorization", token).put("Set-Cookie",
                     String.format("vcloud-token=%s; Secure; Path=/", token)).build()).payload(
            payloadFromResourceWithContentType("/session.xml", VCloudDirectorMediaType.SESSION_XML + ";version=1.5"))
            .build();

   public BaseVCloudDirectorRestClientExpectTest() {
      provider = "vcloud-director";
      identity = String.format("%s@%s", user, org);
      credential = password;
   }
   
   protected HttpRequest getStandardRequest(String method, URI uri) {
      return HttpRequest.builder().method(method).endpoint(uri).headers(
            ImmutableMultimap.<String, String> builder()
               .put("Accept", "*/*")
               .put("x-vcloud-authorization",token)
            .build()).build();
   }

   protected HttpResponse getStandardPaylodResponse(String relativeFilePath) {
      return HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType(relativeFilePath, VCloudDirectorMediaType.ORGLIST_XML
                  + ";version=1.5")).build();
   }

}
