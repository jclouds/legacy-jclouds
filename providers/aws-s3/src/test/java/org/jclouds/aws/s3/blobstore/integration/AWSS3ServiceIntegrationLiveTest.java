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

package org.jclouds.aws.s3.blobstore.integration;

import java.util.Set;

import org.jclouds.s3.blobstore.integration.S3ServiceIntegrationLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSS3ServiceIntegrationLiveTest")
public class AWSS3ServiceIntegrationLiveTest extends S3ServiceIntegrationLiveTest {
   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US", "US-CA", "IE", "SG");
   }

}
