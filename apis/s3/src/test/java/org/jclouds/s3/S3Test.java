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
package org.jclouds.s3;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.collect.PagedIterable;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.internal.ListBucketResponseImpl;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.xml.ListBucketHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code S3}.
 *
 * @author Adrian Cole
 */
public class S3Test {

   /**
    * Tests {@link S3#listBucket(S3Client, String, ListBucketOptions)} where a
    * single response returns all results.
    *
    * @throws Exception
    *            if anything goes wrong
    */
   @Test
   public void testSinglePageResult() throws Exception {
      S3Client api = createMock(S3Client.class);
      ListBucketOptions options = new ListBucketOptions();
      ListBucketResponse response = new ListBucketHandlerTest().expected();

      expect(api.listBucket("bucket", options)).andReturn(response).once();

      EasyMock.replay(api);

      PagedIterable<ObjectMetadata> result = S3.listBucket(api, "bucket", options);

      // number of pages
      assertEquals(result.size(), 1);
      // number of objects
      assertEquals(result.get(0).size(), 10);
   }

   /**
    * Tests {@link S3#listBucket(S3Client, String, ListBucketOptions)} where
    * retrieving all results requires multiple requests.
    *
    * @throws Exception
    *            if anything goes wrong
    */
   @Test
   public void testMultiPageResult() throws Exception {
      String nextMarker = "FOO";
      S3Client api = createMock(S3Client.class);
      ListBucketOptions options = new ListBucketOptions();
      ListBucketResponse response2 = new ListBucketHandlerTest().expected();
      ListBucketResponse response1 = new ListBucketResponseImpl(response2.getName(), response2, response2.getPrefix(),
            null, nextMarker, response2.getMaxKeys(), response2.getDelimiter(), false, response2.getCommonPrefixes());

      expect(api.listBucket("bucket", options)).andReturn(response1).once();
      expect(api.listBucket("bucket", options.afterMarker(nextMarker))).andReturn(response2).once();

      EasyMock.replay(api);

      PagedIterable<ObjectMetadata> result = S3.listBucket(api, "bucket", options);

      // number of objects
      assertEquals(result.concat().size(), 20);
   }

}
