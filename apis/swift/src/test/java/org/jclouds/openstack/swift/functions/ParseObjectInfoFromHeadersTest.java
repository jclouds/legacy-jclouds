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
package org.jclouds.openstack.swift.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.internal.BasePayloadTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ParseObjectInfoFromHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseObjectInfoFromHeadersTest extends BasePayloadTest {

   public void testEtagCaseIssue() {
      assertETagCanBeParsed("feb1",
                            new byte[] { (byte) 0xfe, (byte) 0xb1 }
      );
   }

   public void testParseEtagWithQuotes() {
      assertETagCanBeParsed("\"feb1\"",
                            new byte[] { (byte) 0xfe, (byte) 0xb1 }
      );
   }

   private void assertETagCanBeParsed(String etag, byte[] expectedHash) {
      ParseObjectInfoFromHeaders parser = i.getInstance(ParseObjectInfoFromHeaders.class);

      parser.setContext(requestForArgs(ImmutableList.<Object> of("container", "key")));

      HttpResponse response = HttpResponse.builder().statusCode(200).message("ok").payload("")
                                          .addHeader("Last-Modified", "Fri, 12 Jun 2007 13:40:18 GMT")
                                          .addHeader("Content-Length", "0")
                                          .addHeader("Etag", etag).build();

      response.getPayload().getContentMetadata().setContentType("text/plain");
      MutableObjectInfoWithMetadata md = parser.apply(response);
      assertNotNull(md.getHash());
      assertEquals(md.getHash(), expectedHash);
   }
}
