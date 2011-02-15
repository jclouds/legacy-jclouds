/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.s3.binders;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.jclouds.s3.BaseS3AsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindAsHostPrefixIfConfigured}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindAsHostPrefixIfConfiguredTest")
public class BindAsHostPrefixIfConfiguredTest extends BaseS3AsyncClientTest {

   public void testBucket() throws IOException {

      HttpRequest request = new HttpRequest("GET", URI.create("http://euc/services/Walrus"));
      BindAsHostPrefixIfConfigured binder = injector.getInstance(BindAsHostPrefixIfConfigured.class);

      request = binder.bindToRequest(request, "bucket");
      assertEquals(request.getRequestLine(), "GET http://euc/services/Walrus/bucket HTTP/1.1");

   }

   @Test(dataProvider = "objects")
   public void testObject(String key) throws InterruptedException {

      HttpRequest request = new HttpRequest("GET", URI.create("http://euc/services/Walrus/object"));
      BindAsHostPrefixIfConfigured binder = injector.getInstance(BindAsHostPrefixIfConfigured.class);

      request = binder.bindToRequest(request, "bucket");
      assertEquals(request.getRequestLine(), "GET http://euc/services/Walrus/bucket/object HTTP/1.1");

   }

   @DataProvider(name = "objects")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" }, { "path/foo" },
            { "colon:" }, { "asteri*k" }, { "quote\"" }, { "{great<r}" }, { "lesst>en" }, { "p|pe" } };
   }

   @Override
   protected Properties getProperties() {
      Properties properties = super.getProperties();
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/services/Walrus");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return properties;
   }

}
