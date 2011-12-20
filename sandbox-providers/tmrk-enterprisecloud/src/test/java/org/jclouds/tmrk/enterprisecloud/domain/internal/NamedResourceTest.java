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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.functions.URISource;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "BaseResourceTest")
public class NamedResourceTest {

   /**
    * Tests getURI on BaseResource via NamedResource subclass
    */
   public void testGetURI() throws URISyntaxException {
      URI uri = URI.create("/dev/null");
      URISource source = NamedResource.builder().href(uri).build();
      assertEquals(uri,source.getURI());
   }
}
