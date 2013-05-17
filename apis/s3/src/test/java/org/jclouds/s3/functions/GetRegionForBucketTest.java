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
import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetRegionForBucketTest")
public class GetRegionForBucketTest {

   @SuppressWarnings("unchecked")
   @Test
   void test() throws Exception {
      LoadingCache<String, Optional<String>> bucketToRegionCache = createMock(LoadingCache.class);

      expect(bucketToRegionCache.get("bucket")).andReturn(Optional.of("us-east-1"));

      replay(bucketToRegionCache);
      GetRegionForBucket fn = new GetRegionForBucket(bucketToRegionCache);

      assertEquals(fn.apply("bucket"), Optional.of("us-east-1"));

      verify(bucketToRegionCache);

   }

   @SuppressWarnings("serial")
   @DataProvider(name = "exceptions")
   public Object[][] createExceptions() {
      return new Object[][] { { new ExecutionException() {
      } }, { new UncheckedExecutionException() {
      } }, { new InvalidCacheLoadException("foo") } };
   }

   @SuppressWarnings("unchecked")
   @Test(dataProvider = "exceptions")
   void testGracefulOnException(Exception exception) throws Exception {
      LoadingCache<String, Optional<String>> bucketToRegionCache = createMock(LoadingCache.class);

      expect(bucketToRegionCache.get("bucket")).andThrow(exception);

      replay(bucketToRegionCache);
      GetRegionForBucket fn = new GetRegionForBucket(bucketToRegionCache);

      assertEquals(fn.apply("bucket"), Optional.absent());

      verify(bucketToRegionCache);
   }
}
