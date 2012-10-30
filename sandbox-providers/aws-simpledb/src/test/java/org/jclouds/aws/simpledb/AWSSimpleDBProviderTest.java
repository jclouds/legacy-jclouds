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
package org.jclouds.aws.simpledb;

import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

/**
 * The AWSSimpleDBProviderTest tests the org.jclouds.providers.AWSSimpleDBProvider class.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSSimpleDBProviderTest")
public class AWSSimpleDBProviderTest extends BaseProviderMetadataTest {

   public AWSSimpleDBProviderTest() {
      super(new AWSSimpleDBProviderMetadata(), ProviderMetadata.TABLE_TYPE);
   }
}
