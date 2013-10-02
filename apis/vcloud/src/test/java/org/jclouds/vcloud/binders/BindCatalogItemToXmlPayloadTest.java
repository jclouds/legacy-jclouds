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
package org.jclouds.vcloud.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BasePayloadTest;
import org.jclouds.vcloud.options.CatalogItemOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BindCatalogItemToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCatalogItemToXmlPayloadTest extends BasePayloadTest {
   
   public void testDefault() throws IOException {
      String expected = "<CatalogItem xmlns=\"http://www.vmware.com/vcloud/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"myname\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://vcloud.safesecureweb.com/ns/vcloud.xsd\"><Description>mydescription</Description><Entity href=\"http://fooentity\"/><Property key=\"foo\">bar</Property></CatalogItem>";

      CatalogItemOptions options = CatalogItemOptions.Builder.description("mydescription").properties(
            ImmutableMap.of("foo", "bar"));
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(options));

      BindCatalogItemToXmlPayload binder = injector.getInstance(BindCatalogItemToXmlPayload.class);

      Map<String, Object> map = ImmutableMap.<String, Object> of("name", "myname", "Entity", "http://fooentity");

      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
