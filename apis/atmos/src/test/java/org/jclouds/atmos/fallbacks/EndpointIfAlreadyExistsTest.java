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
package org.jclouds.atmos.fallbacks;

import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EndpointIfAlreadyExistsTest {

   @Test
   public void testFoundIsNullWhenEndpointNotSet() throws Exception {
      assertNull(getUnchecked(new EndpointIfAlreadyExists().create(new KeyAlreadyExistsException())));
   }

   @Test
   public void testFoundIsEndpointWhenSet() throws Exception {
      assertEquals(
            getUnchecked(new EndpointIfAlreadyExists().setEndpoint(URI.create("foo")).create(
                  new KeyAlreadyExistsException())), URI.create("foo"));
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testNotFoundPropagates() throws Exception {
      new EndpointIfAlreadyExists().create(new RuntimeException());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() throws Exception {
      new EndpointIfAlreadyExists().create(null);
   }
}
