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
package org.jclouds.sts;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.jclouds.sts.options.AssumeRoleOptions.Builder.externalId;
import static org.jclouds.sts.options.FederatedUserOptions.Builder.policy;
import static org.jclouds.sts.options.SessionCredentialsOptions.Builder.serialNumber;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.sts.internal.BaseSTSApiExpectTest;
import org.jclouds.sts.parse.AssumeRoleResponseTest;
import org.jclouds.sts.parse.GetFederationTokenResponseTest;
import org.jclouds.sts.parse.GetSessionTokenResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "STSApiExpectTest")
public class STSApiExpectTest extends BaseSTSApiExpectTest {

   HttpRequest createTemporaryCredentials = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "GetSessionToken")
         .addFormParam("Signature", "ntC%2BPKAcmYTJ5Py5tjICG4KX5y00Pl2L0XJrLbSgLEs%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   HttpResponse createTemporaryCredentialsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/session_token.xml", "text/xml")).build();

   public void testCreateTemporaryCredentialsWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(createTemporaryCredentials, createTemporaryCredentialsResponse);

      assertEquals(
            apiWhenWithOptionsExist.createTemporaryCredentials().toString(),
            new GetSessionTokenResponseTest().expected().toString());
   }

   HttpRequest createTemporaryCredentialsWithOptions = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "GetSessionToken")
         .addFormParam("DurationSeconds", "900")
         .addFormParam("SerialNumber", "YourMFADeviceSerialNumber")
         .addFormParam("Signature", "e4HEkfKrw7EuLEQhe4/lK1l7ZmaynO3snsIMU/cdarI%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("TokenCode", "1234")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateTemporaryCredentialsWithOptionsWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(createTemporaryCredentialsWithOptions, createTemporaryCredentialsResponse);

      assertEquals(
            apiWhenWithOptionsExist.createTemporaryCredentials(
                  serialNumber("YourMFADeviceSerialNumber").tokenCode("1234").durationSeconds(TimeUnit.MINUTES.toSeconds(15))).toString(),
            new GetSessionTokenResponseTest().expected().toString());
   }

   HttpRequest assumeRole = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "AssumeRole")
         .addFormParam("RoleArn", "arn:aws:iam::123456789012:role/demo")
         .addFormParam("RoleSessionName", "Bob")
         .addFormParam("Signature", "0G1%2B6GX4cSU9Tjf2SyQ9oW5ivFri4BQPif/24FoRiWY%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   HttpResponse assumeRoleResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/assume_role.xml", "text/xml")).build();

   public void testAssumeRoleWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(assumeRole, assumeRoleResponse);

      assertEquals(apiWhenWithOptionsExist.assumeRole("arn:aws:iam::123456789012:role/demo", "Bob").toString(),
            new AssumeRoleResponseTest().expected().toString());
   }

   String policy = "{\"Statement\":[{\"Sid\":\"Stmt1\",\"Effect\":\"Allow\",\"Action\":\"s3:*\",\"Resource\":\"*\"}]}";

   HttpRequest assumeRoleWithOptions = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "AssumeRole")
         .addFormParam("DurationSeconds", "900")
         .addFormParam("ExternalId", "123ABC")
         .addFormParam("Policy", policy)
         .addFormParam("RoleArn", "arn:aws:iam::123456789012:role/demo")
         .addFormParam("RoleSessionName", "Bob")
         .addFormParam("Signature", "9qffV6zHRbTX8E9IYbEFeQPWrHEdSbwUfjJpg1SMaBo%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAssumeRoleWithOptionsWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(assumeRoleWithOptions, assumeRoleResponse);

      assertEquals(
            apiWhenWithOptionsExist.assumeRole("arn:aws:iam::123456789012:role/demo", "Bob",
                  externalId("123ABC").policy(policy).durationSeconds(TimeUnit.MINUTES.toSeconds(15))).toString(),
            new AssumeRoleResponseTest().expected().toString());
   }

   HttpRequest createFederatedUser = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "GetFederationToken")
         .addFormParam("Name", "Bob")
         .addFormParam("Signature", "Z7AtGK4X9IAx/zMtLD7baNiyltNl%2BF%2BSHqjIGUidzOc%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   HttpResponse createFederatedUserResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/federation_token.xml", "text/xml")).build();

   public void testCreateFederatedUserWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(createFederatedUser, createFederatedUserResponse);

      assertEquals(apiWhenWithOptionsExist.createFederatedUser("Bob").toString(), new GetFederationTokenResponseTest()
            .expected().toString());
   }

   HttpRequest createFederatedUserWithOptions = HttpRequest.builder().method(POST)
         .endpoint("https://sts.amazonaws.com/")
         .addHeader(HOST, "sts.amazonaws.com")
         .addFormParam("Action", "GetFederationToken")
         .addFormParam("DurationSeconds", "900")
         .addFormParam("Name", "Bob")
         .addFormParam("Policy", policy)
         .addFormParam("Signature", "%2BWGvCNtmb1UPmQHxXPMvcK6vH/TJ9r/wCuxdz03n/2w%3D")
         .addFormParam("SignatureMethod", "HmacSHA256")
         .addFormParam("SignatureVersion", "2")
         .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
         .addFormParam("Version", "2011-06-15")
         .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateFederatedUserWithOptionsWhenResponseIs2xx() {

      STSApi apiWhenWithOptionsExist = requestSendsResponse(createFederatedUserWithOptions, createFederatedUserResponse);

      assertEquals(
            apiWhenWithOptionsExist.createFederatedUser("Bob",
                  policy(policy).durationSeconds(TimeUnit.MINUTES.toSeconds(15))).toString(),
            new GetFederationTokenResponseTest().expected().toString());
   }
}
