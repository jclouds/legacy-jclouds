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
package org.jclouds.softlayer.features;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.jclouds.softlayer.SoftLayerClient;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SoftLayerClientExpectTest")
public class VirtualGuestClientExpectTest extends BaseRestClientExpectTest<SoftLayerClient> {


   public VirtualGuestClientExpectTest() {
      provider = "softlayer";
   }
   
   public void testCancelGuestReturnsTrueOn200AndFalseOn404() {
      
      HttpRequest cancelGuest11 = HttpRequest.builder().method("GET")
               .endpoint("https://api.softlayer.com/rest/v3/SoftLayer_Billing_Item/11/cancelService.json")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse found = HttpResponse.builder().statusCode(200).build();

      SoftLayerClient clientWhenServiceExists = requestSendsResponse(cancelGuest11, found);
      
      assert clientWhenServiceExists.getVirtualGuestClient().cancelService(11l);


      HttpResponse notFound = HttpResponse.builder().statusCode(404).build();

      SoftLayerClient clientWhenServiceDoesntExist = requestSendsResponse(cancelGuest11, notFound);
      
      assert !clientWhenServiceDoesntExist.getVirtualGuestClient().cancelService(11l);

   }
}
