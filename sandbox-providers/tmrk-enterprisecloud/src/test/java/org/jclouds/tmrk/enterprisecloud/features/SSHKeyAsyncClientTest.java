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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests annotation parsing of {@code SSHKeyAsyncClientTest}
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "SSHKeyAsyncClientTest")
public class SSHKeyAsyncClientTest extends BaseTerremarkEnterpriseCloudAsyncClientTest<SSHKeyAsyncClient> {

   public void testGetSSHKeys() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = SSHKeyAsyncClient.class.getMethod("getSSHKeys", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/admin/sshkeys/organizations/17"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/admin/sshkeys/organizations/17 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.admin.sshKey; type=collection\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetSSHKey() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = SSHKeyAsyncClient.class.getMethod("getSSHKey", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/admin/sshkeys/77"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/admin/sshkeys/77 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.admin.sshKey\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testCreateSSHKey() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = SSHKeyAsyncClient.class.getMethod("createSSHKey", URI.class,String.class,boolean.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/admin/sshkeys/organizations/17/action/createsshkey"),"myKey",true);

      assertRequestLineEquals(httpRequest, "POST https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/admin/sshkeys/organizations/17/action/createsshkey HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.admin.sshKey\nx-tmrk-version: 2011-07-01\n");
      String xml = "<CreateSshKey name='myKey'><Default>true</Default></CreateSshKey>";
      assertPayloadEquals(httpRequest, xml, "application/xml", false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testEditSSHKey() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = SSHKeyAsyncClient.class.getMethod("editSSHKey", URI.class,SSHKey.class);
      
      SSHKey key = SSHKey.builder().type("application/vnd.tmrk.cloud.admin.sshKey")
                                   .href(URI.create("/cloudapi/ecloud/admin/sshkeys/77"))
                                   .name("newName").defaultKey(false).fingerPrint("123").build();
      
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/admin/sshkeys/77"),key);

      assertRequestLineEquals(httpRequest, "PUT https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/admin/sshkeys/77 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.admin.sshKey\nx-tmrk-version: 2011-07-01\n");
      String xml = "<SshKey name=\"newName\"><Default>false</Default><FingerPrint>123</FingerPrint></SshKey>";
      assertPayloadEquals(httpRequest, xml, "application/xml", false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteSSHKey() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = SSHKeyAsyncClient.class.getMethod("deleteSSHKey", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/admin/sshkeys/77"));

      assertRequestLineEquals(httpRequest, "DELETE https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/admin/sshkeys/77 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,"x-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SSHKeyAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SSHKeyAsyncClient>>() {
      };
   }
}
