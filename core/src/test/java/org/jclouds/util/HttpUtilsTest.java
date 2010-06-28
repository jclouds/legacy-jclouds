/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.PerformanceTest;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.Test;

/**
 * This tests the performance of Digest commands.
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", sequential = true, testName = "jclouds.HttpUtils")
public class HttpUtilsTest extends PerformanceTest {

   public void testIsEncoded() {
      assert HttpUtils.isUrlEncoded("/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assert !HttpUtils.isUrlEncoded("/read-tests/ tep");
   }

   public void testNoDoubleEncode() {
      assertEquals(HttpUtils.urlEncode("/read-tests/%73%6f%6d%65%20%66%69%6c%65", '/'),
               "/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assertEquals(HttpUtils.urlEncode("/read-tests/ tep", '/'), "/read-tests/%20tep");
   }
   
   public void testIBM() {
      URI ibm = HttpUtils
               .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram/assetDetail/generalDetails.faces?guid={A31FF849-0E97-431A-0324-097385A46298}&v=1.2");
      assertEquals(ibm.getQuery(), "guid={A31FF849-0E97-431A-0324-097385A46298}&v=1.2");
   }

   public void testAtmos() {
      URI creds = HttpUtils
               .createUri("compute://domain/user:Base64==@azureblob/container-hyphen/prefix");
      assertEquals(creds, URI
               .create("compute://domain%2Fuser:Base64%3D%3D@azureblob/container-hyphen/prefix"));
   }

   public void testAzure() {
      URI creds = HttpUtils
               .createUri("compute://identity:Base64==@azureblob/container-hyphen/prefix");
      assertEquals(creds, URI
               .create("compute://identity:Base64==@azureblob/container-hyphen/prefix"));
   }

   public void testHosting() {
      URI creds = HttpUtils.createUri("compute://user@domain:pa$sword@hostingdotcom");
      assertEquals(creds.getUserInfo(), "user@domain:pa$sword");
      assertEquals(creds, URI.create("compute://user%40domain:pa%24sword@hostingdotcom"));
   }

   public void testTerremark() {
      URI creds = HttpUtils.createUri("compute://user@domain:password@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:password");
      assertEquals(creds, URI.create("compute://user%40domain:password@terremark"));
   }

   public void testTerremark2() {
      URI creds = HttpUtils.createUri("compute://user@domain:passw@rd@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:passw@rd");
      assertEquals(creds, URI.create("compute://user%40domain:passw%40rd@terremark"));
   }

   public void testTerremark3() {
      URI creds = HttpUtils.createUri("compute://user@domain:AbC!@943!@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:AbC!@943!");
      assertEquals(creds, URI.create("compute://user%40domain:AbC%21%40943%21@terremark"));
   }

   public void testCloudFiles() {
      URI creds = HttpUtils.createUri("compute://identity:h3c@cloudfiles/container-hyphen/prefix");
      assertEquals(creds, URI.create("compute://identity:h3c@cloudfiles/container-hyphen/prefix"));
   }

   public void testS3() {

      URI creds = HttpUtils.createUri("compute://0AB:aA+/0@s3/buck-et/prefix");
      assertEquals(creds, URI.create("compute://0AB:aA%2B%2F0@s3/buck-et/prefix"));
   }

   public void testS3Space() {

      URI creds = HttpUtils.createUri("compute://0AB:aA+/0@s3/buck-et/pre fix");

      assertEquals(creds, URI.create("compute://0AB:aA%2B%2F0@s3/buck-et/pre%20fix"));
   }

   public void testPercent() {
      URI creds = HttpUtils
               .createUri("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%.txt");

      assertEquals(
               creds,
               URI
                        .create("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%25.txt"));

   }
}
