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
package org.jclouds.mezeo.pcs2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.PCSCloudAsyncClient;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler.PCSCloudResponseImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code ParseFlavorListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.CloudXlinkHanderTest")
public class CloudXlinkHanderTest extends BaseHandlerTest {
   Map<String, URI> map = ImmutableMap
            .<String, URI> builder()
            .put(
                     "rootContainer",
                     URI
                              .create("https://pcsbeta.mezeo.net/v2/containers/0B5C8F50-8E72-11DE-A1D4-D73479DA6257"))
            .put("contacts", URI.create("https://pcsbeta.mezeo.net/v2/contacts")).put("shares",
                     URI.create("https://pcsbeta.mezeo.net/v2/shares")).put("projects",
                     URI.create("https://pcsbeta.mezeo.net/v2/projects")).put("metacontainers",
                     URI.create("https://pcsbeta.mezeo.net/v2/metacontainers")).put("account",
                     URI.create("https://pcsbeta.mezeo.net/v2/account")).put("tags",
                     URI.create("https://pcsbeta.mezeo.net/v2/tags")).put("recyclebin",
                     URI.create("https://pcsbeta.mezeo.net/v2/recyclebin")).build();

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/discovery.xml");
      PCSCloudAsyncClient.Response list = new PCSCloudResponseImpl(map);

      PCSCloudAsyncClient.Response result = (PCSCloudAsyncClient.Response) factory.create(
               injector.getInstance(CloudXlinkHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
