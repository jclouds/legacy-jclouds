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
package org.jclouds.ec2.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.ec2.parse.GetPasswordDataResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "WindowsApiExpectTest")
public class WindowsApiExpectTest extends BaseEC2ApiExpectTest<EC2Api> {

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                .payload(
                                   payloadFromStringWithContentType(
                                         "Action=GetPasswordData" +
                                               "&InstanceId=i-2574e22a" +
                                               "&Signature=vX1Tskc4VuBUWPqsJ%2BzcjEj6/2iMCKzqjWnKFXRkDdA%3D" +
                                               "&SignatureMethod=HmacSHA256" +
                                               "&SignatureVersion=2" +
                                               "&Timestamp=2012-04-16T15%3A54%3A08.897Z" +
                                               "&Version=2010-06-15" +
                                               "&AWSAccessKeyId=identity",
                                         "application/x-www-form-urlencoded"))
                                .build();
  
   
   public void testGetPasswordDataWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/get_passworddata.xml", "text/xml")).build();

      EC2Api apiWhenExist = requestSendsResponse(get, getResponse);

      assertEquals(apiWhenExist.getWindowsApi().get().getPasswordDataForInstance("i-2574e22a").toString(), new GetPasswordDataResponseTest().expected().toString());
   }

   public void testGetPasswordDataWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      EC2Api apiWhenDontExist = requestSendsResponse(get, getResponse);

      assertNull(apiWhenDontExist.getWindowsApi().get().getPasswordDataForInstance("i-2574e22a"));
   }
}
