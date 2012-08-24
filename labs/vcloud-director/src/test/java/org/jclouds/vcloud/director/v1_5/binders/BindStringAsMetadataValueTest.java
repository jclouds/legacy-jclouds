/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.binders;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.xml.XMLParser;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindStringAsMetadataValue}.
 * 
 */
@Test(groups = "unit", testName = "BindStringAsMetadataValueTest")
public class BindStringAsMetadataValueTest {
   XMLParser xml = new JAXBParser("true");

   @Test
   public void testBindMap() {
      BindStringAsMetadataValue binder = new BindStringAsMetadataValue(xml);

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      request = binder.bindToRequest(request, "foo");
      assertEquals(request.getPayload().getRawContent(), 
                  XMLParser.DEFAULT_XML_HEADER + "\n"+
                  "<MetadataValue xmlns=\"http://www.vmware.com/vcloud/v1.5\">" + "\n"+
                  "    <Value>foo</Value>" + "\n" +
                  "</MetadataValue>" + "\n");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.APPLICATION_XML);
   }

}
