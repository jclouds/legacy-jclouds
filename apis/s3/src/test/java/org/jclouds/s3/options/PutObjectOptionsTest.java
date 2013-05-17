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
package org.jclouds.s3.options;

import static org.jclouds.s3.options.PutObjectOptions.Builder.withAcl;
import static org.testng.Assert.assertEquals;

import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.reference.S3Headers;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests possible uses of PutObjectOptions and PutObjectOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PutObjectOptionsTest {

   @Test
   public void testAclDefault() {
      PutObjectOptions options = new PutObjectOptions();
      assertEquals(options.getAcl(), CannedAccessPolicy.PRIVATE);
   }

   @Test
   public void testAclStatic() {
      PutObjectOptions options = withAcl(CannedAccessPolicy.AUTHENTICATED_READ);
      assertEquals(options.getAcl(), CannedAccessPolicy.AUTHENTICATED_READ);
   }

   @Test
   void testBuildRequestHeaders() {

      PutObjectOptions options = withAcl(CannedAccessPolicy.AUTHENTICATED_READ);
      options.setHeaderTag(S3Headers.DEFAULT_AMAZON_HEADERTAG);

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(S3Headers.CANNED_ACL).iterator().next(),
               CannedAccessPolicy.AUTHENTICATED_READ.toString());
   }
}
