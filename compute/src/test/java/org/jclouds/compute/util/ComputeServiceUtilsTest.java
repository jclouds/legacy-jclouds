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

package org.jclouds.compute.util;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Test the compute utils.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ComputeServiceUtilsTest {

   @Test
   public void testCurlSh() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://adriancolehappy.s3.amazonaws.com/java/install"),
               ImmutableMultimap.of("Host", "adriancolehappy.s3.amazonaws.com", "Date",
                        "Sun, 12 Sep 2010 08:25:19 GMT", "Authorization", "AWS 0ASHDJAS82:JASHFDA="));

      assertEquals(
               ComputeServiceUtils.buildCurlsh(request),
               "curl -s --retry 20 -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |bash\n");

   }
}
