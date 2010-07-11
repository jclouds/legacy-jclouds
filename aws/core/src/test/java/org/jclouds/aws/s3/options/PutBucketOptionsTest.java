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
package org.jclouds.aws.s3.options;

import static org.jclouds.aws.s3.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.reference.S3Headers;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests possible uses of PutBucketOptions and PutBucketOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.PutBucketOptionsTest")
public class PutBucketOptionsTest {

   @Test
   public void testAclDefault() {
      PutBucketOptions options = new PutBucketOptions();
      assertEquals(options.getAcl(), CannedAccessPolicy.PRIVATE);
   }

   @Test
   public void testAclStatic() {
      PutBucketOptions options = withBucketAcl(CannedAccessPolicy.AUTHENTICATED_READ);
      assertEquals(options.getAcl(), CannedAccessPolicy.AUTHENTICATED_READ);
   }

   @Test
   void testBuildRequestHeaders() throws UnsupportedEncodingException {

      PutBucketOptions options = withBucketAcl(CannedAccessPolicy.AUTHENTICATED_READ);

      options.setHeaderTag("amz");
      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.get(S3Headers.CANNED_ACL).iterator().next(),
               CannedAccessPolicy.AUTHENTICATED_READ.toString());
   }
}
