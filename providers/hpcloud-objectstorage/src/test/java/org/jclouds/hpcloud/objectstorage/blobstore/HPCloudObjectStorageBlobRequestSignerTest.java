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
package org.jclouds.hpcloud.objectstorage.blobstore;

import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.hpcloud.objectstorage.internal.BaseHPCloudObjectStorageBlobStoreExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code HPCloudObjectStorageBlobRequestSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "HPCloudObjectStorageBlobRequestSignerTest")
public class HPCloudObjectStorageBlobRequestSignerTest extends BaseHPCloudObjectStorageBlobStoreExpectTest {

   Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder().put(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess).build();

   public void testSignGetBlob() {

      BlobRequestSigner signGetBlob = requestsSendResponses(requestResponseMap).getContext().getSigner();

      HttpRequest request = signGetBlob.signGetBlob("container", "name");

      assertEquals(request.getRequestLine(),
               "GET https://objects.jclouds.org/v1.0/40806637803162/container/name HTTP/1.1");
      assertEquals(request.getHeaders(), ImmutableMultimap.of("X-Auth-Token", "Auth_4f173437e4b013bee56d1007"));
   }

   public void testSignRemoveBlob() {
      BlobRequestSigner signRemoveBlob = requestsSendResponses(requestResponseMap).getContext().getSigner();

      HttpRequest request = signRemoveBlob.signRemoveBlob("container", "name");
      assertEquals(request.getRequestLine(),
               "DELETE https://objects.jclouds.org/v1.0/40806637803162/container/name HTTP/1.1");
      assertEquals(request.getHeaders(), ImmutableMultimap.of("X-Auth-Token", "Auth_4f173437e4b013bee56d1007"));

   }

   public void testSignPutBlob() {
      BlobStore blobStore = requestsSendResponses(requestResponseMap);
      BlobRequestSigner signPutBlob = blobStore.getContext().getSigner();
      Blob blob = blobStore.blobBuilder("name").forSigning().contentLength(2l).contentMD5(new byte[] { 0, 2, 4, 8 })
               .contentType("text/plain").expires(new Date(1000)).build();

      HttpRequest request = signPutBlob.signPutBlob("container", blob);

      assertEquals(request.getRequestLine(),
               "PUT https://objects.jclouds.org/v1.0/40806637803162/container/name HTTP/1.1");
      assertEquals(request.getHeaders(), ImmutableMultimap.of("X-Auth-Token", "Auth_4f173437e4b013bee56d1007"));
      // TODO:
      // assertEquals(request.getPayload(), blob);

   }

}
