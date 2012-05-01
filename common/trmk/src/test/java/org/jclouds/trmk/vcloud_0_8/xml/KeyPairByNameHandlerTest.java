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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code KeyPairByNameHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "KeyPairByNameHandlerTest")
public class KeyPairByNameHandlerTest extends BaseHandlerTest {

   public void testGood() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/keysList.xml");

      KeyPair result = factory.create(
            addOrgAndNameToHandler(injector.getInstance(KeyPairByNameHandler.class), "org", "default")).parse(is);
      assertEquals(result,
            new KeyPair(URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/key/9"),
                  "default", true, null, "4e:af:8a:9f:e9:d2:72:d7:4b:a0:da:98:72:98:4d:7d"));

   }

   public void testNotFound() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/keysList.xml");

      KeyPair result = factory.create(
            addOrgAndNameToHandler(injector.getInstance(KeyPairByNameHandler.class), "org", "monster")).parse(is);
      assertEquals(result, null);

   }

   private static KeyPairByNameHandler addOrgAndNameToHandler(KeyPairByNameHandler handler, String org, String name) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(org, name)).anyTimes();
      replay(request);
      handler.setContext(request);
      return handler;
   }
}
