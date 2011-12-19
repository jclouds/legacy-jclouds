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
package org.jclouds.glesys.features;

import com.google.inject.TypeLiteral;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

/**
 * Tests annotation parsing of {@code ArchiveAsyncClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ArchiveAsyncClientTest")
public class ArchiveAsyncClientTest extends BaseGleSYSAsyncClientTest<ArchiveAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ArchiveAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ArchiveAsyncClient>>() {
      };
   }
}
