/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.internal;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.blobstore.internal.LocationAndCredentials;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "blobstore.LocationAndCredentialsTest")
public class LocationAndCredentialsTest {

   public void testAzure() {
      LocationAndCredentials creds = LocationAndCredentials
               .parse("blobstore://account:Base64==@azureblob/container-hyphen/prefix");
      assertEquals(creds.acccount, "account");
      assertEquals(creds.key, "Base64==");
      assertEquals(creds.uri, URI
               .create("blobstore://account:Base64==@azureblob/container-hyphen/prefix"));
   }

   public void testCloudFiles() {
      LocationAndCredentials creds = LocationAndCredentials
               .parse("blobstore://account:h3c@cloudfiles/container-hyphen/prefix");
      assertEquals(creds.acccount, "account");
      assertEquals(creds.key, "h3c");
      assertEquals(creds.uri, URI
               .create("blobstore://account:h3c@cloudfiles/container-hyphen/prefix"));
   }

   public void testS3() {

      LocationAndCredentials creds = LocationAndCredentials
               .parse("blobstore://0AB:aA+/0@s3/buck-et/prefix");
      assertEquals(creds.acccount, "0AB");
      assertEquals(creds.key, "aA+/0");
      assertEquals(creds.uri, URI.create("blobstore://s3/buck-et/prefix"));
   }

   public void testS3Space() {

      LocationAndCredentials creds = LocationAndCredentials
               .parse("blobstore://0AB:aA+/0@s3/buck-et/pre fix");
      assertEquals(creds.acccount, "0AB");
      assertEquals(creds.key, "aA+/0");
      assertEquals(creds.uri, URI.create("blobstore://s3/buck-et/pre%20fix"));
   }

   public void testPercent() {
      LocationAndCredentials creds = LocationAndCredentials
               .parse("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%.txt");
      assertEquals(creds.acccount, null);
      assertEquals(creds.key, null);
      assertEquals(
               creds.uri,
               URI
                        .create("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%25.txt"));

   }
}
