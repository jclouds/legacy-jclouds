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
package org.jclouds.atmos.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.reference.AtmosHeaders;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code ParseObjectFromHeadersAndHttpContent}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseObjectFromHeadersAndHttpContentTest")
public class ParseObjectFromHeadersAndHttpContentTest {

   static final HttpResponse RESPONSE = HttpResponse.builder()
                                                    .statusCode(200)
                                                    .message("ok")
                                                    .payload("")
                                                    .headers(
         ImmutableMultimap.of(AtmosHeaders.TAGS, "tag1, tag2", AtmosHeaders.LISTABLE_TAGS,
               "listabletag1, listabletag2", AtmosHeaders.META,
               "meta1=foo1, content-md5=1f3870be274f6c49b3e31a0c6728957f, atime=2009-10-12T16:09:42Z, mtime=2009-10-19T04:37:00Z,"
                     + " ctime=2009-10-19T04:37:00Z, itime=2009-10-12T16:09:42Z, type=directory, uid=root, "
                     + "gid=rootr, objectid=4980cdb2b010109b04a44f7bb83f5f04ad354c638ae5, "
                     + "objname=e913e09366364e9ba384b8fead643d43, size=4096, nlink=1, policyname=default",
               AtmosHeaders.LISTABLE_META, "listablemeta1=listablefoo1, listablemeta2=listablefoo2")).build();

   public static final AtmosObject EXPECTED;

   static {
      EXPECTED = Guice.createInjector().getInstance(AtmosObject.Factory.class)
            .create(ParseSystemMetadataFromHeadersTest.EXPECTED, ParseUserMetadataFromHeadersTest.EXPECTED);
      EXPECTED.getContentMetadata().setName("e913e09366364e9ba384b8fead643d43");
      EXPECTED.setPayload(RESPONSE.getPayload());
   }

   public void test() {
      ParseObjectFromHeadersAndHttpContent parser = Guice.createInjector().getInstance(
            ParseObjectFromHeadersAndHttpContent.class);
      AtmosObject data = parser.apply(RESPONSE);

      assertEquals(data, EXPECTED);
   }
}
