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
package org.jclouds.openstack.keystone.v2_0.internal;
import static java.lang.String.format;
import static org.jclouds.rest.internal.BaseRestClientExpectTest.payloadFromStringWithContentType;

import java.io.IOException;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public enum KeystoneFixture {
   INSTANCE;

   public String getTenantId(){
      return "12346637803162";
   }
   
   public String getTenantName(){
	      return "adrian@jclouds.org";
	   }

   public HttpRequest initialAuthWithUsernameAndPassword(String username, String password){
      return HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"}}}",
                                       username, password), "application/json")).build();
   }
  
   public HttpRequest initialAuthWithUsernameAndPasswordAndTenantName(String username, String password){
      return HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"},\"tenantName\":\"%s\"}}",
                                       username, password, getTenantName()), "application/json")).build();
   }
  
   public HttpRequest initialAuthWithAccessKeyAndSecretKeyAndTenantName(String accessKey, String secretKey){
      return HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"%s\",\"secretKey\":\"%s\"},\"tenantName\":\"%s\"}}",
                                       accessKey, secretKey, getTenantName()), "application/json")).build();
   }
   
   public HttpRequest initialAuthWithAccessKeyAndSecretKeyAndTenantId(String accessKey, String secretKey){
	      return HttpRequest
	            .builder()
	            .method("POST")
	            .endpoint(URI.create("http://localhost:5000/v2.0/tokens"))
	            .headers(ImmutableMultimap.of(HttpHeaders.ACCEPT, "application/json"))
	            .payload(
	                     payloadFromStringWithContentType(
	                              format(
	                                       "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"%s\",\"secretKey\":\"%s\"},\"tenantId\":\"%s\"}}",
	                                       accessKey, secretKey, getTenantId()), "application/json")).build();
	   }

   public String getAuthToken(){
      return  "Auth_4f173437e4b013bee56d1007";
   }

   public HttpResponse responseWithAccess(){
      return HttpResponse.builder().statusCode(200).message("HTTP/1.1 200").payload(
            payloadFromResourceWithContentType("/keystoneAuthResponse.json", "application/json")).build();
   }


   public Payload payloadFromResourceWithContentType(String resource, String contentType) {
      try {
         return payloadFromStringWithContentType(Strings2.toStringAndClose(getClass().getResourceAsStream(resource)),
                  contentType);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }

   }
}
