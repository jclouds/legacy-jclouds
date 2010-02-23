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
package org.jclouds.domain;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.CredentialsTest")
public class CredentialsTest {

   public void testAzure() {
      Credentials creds = Credentials.parse(URI
               .create("compute://account:Base64==@azureblob/container-hyphen/prefix"));
      assertEquals(creds.account, "account");
      assertEquals(creds.key, "Base64==");
   }

   public void testAtmos() {
      Credentials creds = Credentials.parse(URI
               .create("compute://domain%2Fuser:Base64%3D%3D@azureblob/container-hyphen/prefix"));
      assertEquals(creds.account, "domain/user");
      assertEquals(creds.key, "Base64==");
   }

   public void testHosting() {
      Credentials creds = Credentials.parse(URI
               .create("compute://user%40domain:pa%24sword@hostingdotcom"));
      assertEquals(creds.account, "user@domain");
      assertEquals(creds.key, "pa$sword");
   }

   public void testTerremark() {
      Credentials creds = Credentials.parse(URI
               .create("compute://user%40domain:password@terremark"));
      assertEquals(creds.account, "user@domain");
      assertEquals(creds.key, "password");
   }

   public void testTerremark2() {
      Credentials creds = Credentials.parse(URI
               .create("compute://user%40domain:passw%40rd@terremark"));
      assertEquals(creds.account, "user@domain");
      assertEquals(creds.key, "passw@rd");
   }

   public void testTerremark3() {
      Credentials creds = Credentials.parse(URI
               .create("compute://user%40domain:AbC%21%40943%21@terremark"));
      assertEquals(creds.account, "user@domain");
      assertEquals(creds.key, "AbC!@943!");
   }

   public void testCloudFiles() {
      Credentials creds = Credentials.parse(URI
               .create("compute://account:h3c@cloudfiles/container-hyphen/prefix"));
      assertEquals(creds.account, "account");
      assertEquals(creds.key, "h3c");

   }

   public void testS3() {

      Credentials creds = Credentials
               .parse(URI.create("compute://0AB:aA%2B%2F0@s3/buck-et/prefix"));
      assertEquals(creds.account, "0AB");
      assertEquals(creds.key, "aA+/0");
   }

   public void testS3Space() {

      Credentials creds = Credentials.parse(URI
               .create("compute://0AB:aA%2B%2F0@s3/buck-et/pre%20fix"));
      assertEquals(creds.account, "0AB");
      assertEquals(creds.key, "aA+/0");
   }

}
