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

package org.jclouds.aws.s3.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.s3.domain.BucketLogging;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code BucketLoggingHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.BucketLoggingHandlerTest")
public class BucketLoggingHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/s3/bucket_logging.xml");

      BucketLogging expected = new BucketLogging("mylogs", "access_log-", ImmutableSet
               .<Grant> of(new Grant(new EmailAddressGrantee("adrian@jclouds.org"),
                        Permission.FULL_CONTROL)));
      BucketLoggingHandler handler = injector.getInstance(BucketLoggingHandler.class);
      BucketLogging result = factory.create(handler).parse(is);

      assertEquals(result.getTargetBucket(), expected.getTargetBucket());
      assertEquals(result.getTargetGrants(), expected.getTargetGrants());
      assertEquals(result.getTargetPrefix(), expected.getTargetPrefix());

   }

}