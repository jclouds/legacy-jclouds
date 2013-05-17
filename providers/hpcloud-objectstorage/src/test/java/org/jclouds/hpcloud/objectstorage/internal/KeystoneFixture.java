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
package org.jclouds.hpcloud.objectstorage.internal;

import static java.lang.String.format;
import static org.jclouds.rest.internal.BaseRestApiExpectTest.payloadFromStringWithContentType;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;

/**
 * Base class for writing HP Cloud Object Storage Rest Client Expect tests
 * 
 * @author Michael Arnold
 */
public enum KeystoneFixture {
   INSTANCE;

   public String getTenantName(){
      return "12346637803162";
   }

   public HttpRequest initialAuthWithUsernameAndPassword(String username, String password){
      return HttpRequest.builder()
            .method("POST")
            .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/tokens")
            .addHeader(HttpHeaders.ACCEPT, "application/json")
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"passwordCredentials\":{\"username\":\"%s\",\"password\":\"%s\"},\"tenantName\":\"%s\"}}",
                                       username, password, getTenantName()), "application/json")).build();
   }
  
   public HttpRequest initialAuthWithAccessKeyAndSecretKey(String accessKey, String secretKey){
      return HttpRequest.builder()
            .method("POST")
            .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/tokens")
            .addHeader(HttpHeaders.ACCEPT, "application/json")
            .payload(
                     payloadFromStringWithContentType(
                              format(
                                       "{\"auth\":{\"apiAccessKeyCredentials\":{\"accessKey\":\"%s\",\"secretKey\":\"%s\"},\"tenantName\":\"%s\"}}",
                                       accessKey, secretKey, getTenantName()), "application/json")).build();
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
