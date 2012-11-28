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
package org.jclouds.googlecompute;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import org.jclouds.View;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.testng.annotations.Test;

/**
 * Tests that GoogleComputeApiMetadata is properly registered in ServiceLoader
 * <p/>
 * <pre>
 * META-INF/services/org.jclouds.apis.ApiMetadata
 * </pre>
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GoogleComputeApiMetadataTest")
public class GoogleComputeApiMetadataTest extends BaseApiMetadataTest {
   public GoogleComputeApiMetadataTest() {
      super(new GoogleComputeApiMetadata(), ImmutableSet.<TypeToken<? extends View>>of());
   }
}
