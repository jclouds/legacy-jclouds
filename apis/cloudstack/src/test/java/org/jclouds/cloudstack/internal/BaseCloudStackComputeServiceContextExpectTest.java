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
package org.jclouds.cloudstack.internal;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;


/**
 * Base class for writing CloudStack Expect tests with the ComputeService
 * abstraction
 * 
 */
public abstract class BaseCloudStackComputeServiceContextExpectTest<T> extends BaseCloudStackExpectTest<T> {
   
   public BaseCloudStackComputeServiceContextExpectTest() {
      // to match the api key name in listaccountsresponse.json
      identity = "APIKEY";
   }
   
   protected final HttpRequest listTemplates = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listTemplates")
      .addQueryParam("listAll", "true")
      .addQueryParam("templatefilter", "executable")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "Xk6lF%2Fv3SbhrxTKqaC2IWoBPKHo%3D")
      .addHeader("Accept", "application/json")
      .build();

   //TODO: update or add new resource files to have more recent data, ex. ubuntu template
   protected final HttpResponse listTemplatesResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listtemplatesresponse.json"))
      .build();
   
   protected final HttpRequest listOsTypes = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listOsTypes")
      .addQueryParam("listAll", "true")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "8BsE8MsOAhUzo1Q4Y3UD%2Fe96u84%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listOsTypesResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listostypesresponse.json"))
      .build();
   
   protected final HttpRequest listOsCategories = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listOsCategories")
      .addQueryParam("listAll", "true")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "OojW4ssh%2FRQ3CubAzXue4svlofM%3D")
//      .addHeader("Accept", "application/json") //TODO: why are we not passing this?
      .build();

   protected final HttpResponse listOsCategoriesResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listoscategoriesresponse.json"))
      .build();
   
   protected final HttpRequest listZones = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listZones")
      .addQueryParam("listAll", "true")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "GTUgn%2FLHDioJRq48kurOdCAYueo%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listZonesResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listzonesresponse.json"))
      .build();

   protected final HttpRequest listServiceOfferings = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listServiceOfferings")
      .addQueryParam("listAll", "true")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "jUien8oeEan7bjKKQbBlzvFuMjw%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listServiceOfferingsResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listserviceofferingsresponse.json"))
      .build();

   protected final HttpRequest listAccounts = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listAccounts")
      .addQueryParam("listAll", "true")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "E4wuKXCkioaNIiL8hL8FD9K5K2c%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listAccountsResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listaccountsresponse.json"))
      .build();
   
   protected final HttpRequest listNetworks = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listNetworks")
      .addQueryParam("listAll", "true")
      .addQueryParam("account", "jclouds") // account and domain came from above
      .addQueryParam("domainid", "457")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "FDiGGBiG%2FsVj0k6DmZIgMNU8SqI%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listNetworksResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listnetworksresponse.json"))
      .build();   
   
   protected final HttpRequest getZone = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("command", "listZones")
      .addQueryParam("listAll", "true")
      .addQueryParam("id", "1")
      .addQueryParam("apiKey", "APIKEY")
      .addQueryParam("signature", "q5GMO9iUYIFs5S58DdAuYAy8yu0%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse getZoneResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/getzoneresponse.json"))
      .build();
   
   protected final HttpRequest listCapabilities = HttpRequest.builder().method("GET")
      .endpoint("http://localhost:8080/client/api")
      .addQueryParam("response", "json")
      .addQueryParam("listAll", "true")      
      .addQueryParam("command", "listCapabilities")
      .addQueryParam("apiKey", "APIKEY")      
      .addQueryParam("signature", "vVdhtet%2FzG59FXgkYkAzEQQ4q1o%3D")
      .addHeader("Accept", "application/json")
      .build();

   protected final HttpResponse listCapabilitiesResponse = HttpResponse.builder().statusCode(200)
      .payload(payloadFromResource("/listcapabilitiesresponse.json"))
      .build();   
         
}
