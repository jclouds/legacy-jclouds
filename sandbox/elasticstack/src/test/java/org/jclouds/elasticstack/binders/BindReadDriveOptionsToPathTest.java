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

package org.jclouds.elasticstack.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.elasticstack.options.ReadDriveOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.config.RestModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BindReadDriveOptionsToPathTest {

   private static final BindReadDriveOptionsToPath FN = Guice.createInjector(new RestModule(), new MockModule(),
         new NullLoggingModule()).getInstance(BindReadDriveOptionsToPath.class);

   public void testSimple() {
      HttpRequest request = new HttpRequest("POST", URI.create("https://drives/read"));
      FN.bindToRequest(request, new ReadDriveOptions().offset(1024l).size(2048l));
      assertEquals(request.getEndpoint().getPath(), "/read/1024/2048");
   }

}