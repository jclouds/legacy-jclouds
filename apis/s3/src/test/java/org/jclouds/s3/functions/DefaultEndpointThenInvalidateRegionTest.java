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
package org.jclouds.s3.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Test(testName = "DefaultEndpointThenInvalidateRegionTest")
public class DefaultEndpointThenInvalidateRegionTest {

   @SuppressWarnings("unchecked")
   @Test
   void testInvalidate() throws Exception {
      AssignCorrectHostnameForBucket r2 = createMock(AssignCorrectHostnameForBucket.class);
      LoadingCache<String, Optional<String>> bucketToRegionCache = createMock(LoadingCache.class);

      expect(r2.apply("mybucket")).andReturn(URI.create("http://east-url"));
      bucketToRegionCache.invalidate("mybucket");

      replay(r2, bucketToRegionCache);

      new DefaultEndpointThenInvalidateRegion(r2, bucketToRegionCache).apply("mybucket");
      verify(r2, bucketToRegionCache);

   }

}
