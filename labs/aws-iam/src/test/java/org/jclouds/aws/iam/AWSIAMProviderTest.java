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
package org.jclouds.aws.iam;

import org.jclouds.aws.iam.AWSIAMProviderMetadata;
import org.jclouds.iam.IAMApiMetadata;
import org.jclouds.providers.internal.BaseProviderMetadataTest;
import org.testng.annotations.Test;

/**
 * The AWSIAMProviderTest tests the org.jclouds.providers.AWSIAMProvider class.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSIAMProviderTest")
public class AWSIAMProviderTest extends BaseProviderMetadataTest {

   public AWSIAMProviderTest() {
      super(new AWSIAMProviderMetadata(), new IAMApiMetadata());
   }
}
