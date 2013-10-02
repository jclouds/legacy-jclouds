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
package org.jclouds.s3.options;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceETagDoesntMatch;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceETagMatches;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceModifiedSince;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.ifSourceUnmodifiedSince;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.overrideAcl;
import static org.jclouds.s3.options.CopyObjectOptions.Builder.overrideMetadataWith;
import static org.jclouds.s3.reference.S3Headers.CANNED_ACL;
import static org.jclouds.s3.reference.S3Headers.COPY_SOURCE_IF_MODIFIED_SINCE;
import static org.jclouds.s3.reference.S3Headers.COPY_SOURCE_IF_NO_MATCH;
import static org.jclouds.s3.reference.S3Headers.DEFAULT_AMAZON_HEADERTAG;
import static org.jclouds.s3.reference.S3Headers.METADATA_DIRECTIVE;
import static org.jclouds.s3.reference.S3Headers.USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/**
 * Tests possible uses of CopyObjectOptions and CopyObjectOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CopyObjectOptionsTest {

   private String etag;
   private Date now;
   private String nowExpected;
   private Map<String, String> goodMeta;

   @BeforeTest
   void setUp() {
      goodMeta = ImmutableMap.of(USER_METADATA_PREFIX + "adrian", "foo");
      Date date = new Date();
      nowExpected = new SimpleDateFormatDateService().rfc822DateFormat(date);
      now = new SimpleDateFormatDateService().rfc822DateParse(nowExpected);
      etag = "mama";
   }

   @Test
   void testGoodMetaStatic() {
      CopyObjectOptions options = overrideMetadataWith(goodMeta);
      options.setMetadataPrefix(USER_METADATA_PREFIX);
      options.setHeaderTag(DEFAULT_AMAZON_HEADERTAG);
      assertGoodMeta(options);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMetaNPE() {
      overrideMetadataWith(null);
   }

   private void assertGoodMeta(CopyObjectOptions options) {
      assert options != null;
      assert options.getMetadata() != null;
      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(headers.size(), 2);
      assertEquals(headers.get(METADATA_DIRECTIVE).iterator().next(), "REPLACE");
      assertEquals(options.getMetadata().size(), 1);
      assertEquals(headers.get(USER_METADATA_PREFIX + "adrian").iterator().next(), "foo");
      assertEquals(options.getMetadata().get(USER_METADATA_PREFIX + "adrian"), "foo");
   }

   @Test
   void testGoodMeta() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.setHeaderTag(DEFAULT_AMAZON_HEADERTAG);
      options.setMetadataPrefix(USER_METADATA_PREFIX);
      options.overrideMetadataWith(goodMeta);
      assertGoodMeta(options);
   }

   @Test
   public void testIfModifiedSince() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.ifSourceModifiedSince(now);
      assertEquals(options.getIfModifiedSince(), nowExpected);
   }

   @Test
   public void testNullIfModifiedSince() {
      CopyObjectOptions options = new CopyObjectOptions();
      assertNull(options.getIfModifiedSince());
   }

   @Test
   public void testIfModifiedSinceStatic() {
      CopyObjectOptions options = ifSourceModifiedSince(now);
      assertEquals(options.getIfModifiedSince(), nowExpected);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfModifiedSinceNPE() {
      ifSourceModifiedSince(null);
   }

   @Test
   public void testIfUnmodifiedSince() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.ifSourceUnmodifiedSince(now);
      isNowExpected(options);
   }

   @Test
   public void testNullIfUnmodifiedSince() {
      CopyObjectOptions options = new CopyObjectOptions();
      assertNull(options.getIfUnmodifiedSince());
   }

   @Test
   public void testIfUnmodifiedSinceStatic() {
      CopyObjectOptions options = ifSourceUnmodifiedSince(now);
      isNowExpected(options);
   }

   private void isNowExpected(CopyObjectOptions options) {
      assertEquals(options.getIfUnmodifiedSince(), nowExpected);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfUnmodifiedSinceNPE() {
      ifSourceUnmodifiedSince(null);
   }

   @Test
   public void testIfETagMatches() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.ifSourceETagMatches(etag);
      matchesHex(options.getIfMatch());
   }

   @Test
   public void testNullIfETagMatches() {
      CopyObjectOptions options = new CopyObjectOptions();
      assertNull(options.getIfMatch());
   }

   @Test
   public void testIfETagMatchesStatic() {
      CopyObjectOptions options = ifSourceETagMatches(etag);
      matchesHex(options.getIfMatch());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfETagMatchesNPE() {
      ifSourceETagMatches(null);
   }

   @Test
   public void testIfETagDoesntMatch() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.ifSourceETagDoesntMatch(etag);
      matchesHex(options.getIfNoneMatch());
   }

   @Test
   public void testNullIfETagDoesntMatch() {
      CopyObjectOptions options = new CopyObjectOptions();
      assertNull(options.getIfNoneMatch());
   }

   @Test
   public void testIfETagDoesntMatchStatic() {
      CopyObjectOptions options = ifSourceETagDoesntMatch(etag);
      matchesHex(options.getIfNoneMatch());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testIfETagDoesntMatchNPE() {
      ifSourceETagDoesntMatch(null);
   }

   private void matchesHex(String match) {
      String expected = "\"" + etag + "\"";
      assertEquals(match, expected);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testIfUnmodifiedAfterModified() {
      ifSourceModifiedSince(now).ifSourceUnmodifiedSince(now);

   }

   public void testIfUnmodifiedAfterETagMatches() {
      ifSourceETagMatches(etag).ifSourceUnmodifiedSince(now);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testIfUnmodifiedAfterETagDoesntMatch() {
      ifSourceETagDoesntMatch(etag).ifSourceUnmodifiedSince(now);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testIfModifiedAfterUnmodified() {
      ifSourceUnmodifiedSince(now).ifSourceModifiedSince(now);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testIfModifiedAfterETagMatches() {
      ifSourceETagMatches(etag).ifSourceModifiedSince(now);

   }

   public void testIfModifiedAfterETagDoesntMatch() {
      ifSourceETagDoesntMatch(etag).ifSourceModifiedSince(now);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testETagMatchesAfterIfModified() {
      ifSourceModifiedSince(now).ifSourceETagMatches(etag);

   }

   public void testETagMatchesAfterIfUnmodified() {
      ifSourceUnmodifiedSince(now).ifSourceETagMatches(etag);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testETagMatchesAfterETagDoesntMatch() {
      ifSourceETagDoesntMatch(etag).ifSourceETagMatches(etag);
   }

   public void testETagDoesntMatchAfterIfModified() {
      ifSourceModifiedSince(now).ifSourceETagDoesntMatch(etag);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testETagDoesntMatchAfterIfUnmodified() {
      ifSourceUnmodifiedSince(now).ifSourceETagDoesntMatch(etag);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testETagDoesntMatchAfterETagMatches() {
      ifSourceETagMatches(etag).ifSourceETagDoesntMatch(etag);
   }

   @Test
   void testBuildRequestHeadersWhenMetadataNull() {
      CopyObjectOptions options = new CopyObjectOptions();
      options.setHeaderTag(DEFAULT_AMAZON_HEADERTAG);

      options.setMetadataPrefix(USER_METADATA_PREFIX);
      assert options.buildRequestHeaders() != null;
   }

   @Test
   void testBuildRequestHeaders() {
      CopyObjectOptions options = ifSourceModifiedSince(now).ifSourceETagDoesntMatch(etag).overrideMetadataWith(
               goodMeta);
      options.setHeaderTag(DEFAULT_AMAZON_HEADERTAG);

      options.setMetadataPrefix(USER_METADATA_PREFIX);

      Multimap<String, String> headers = options.buildRequestHeaders();
      assertEquals(getOnlyElement(headers.get(COPY_SOURCE_IF_MODIFIED_SINCE)), new SimpleDateFormatDateService()
               .rfc822DateFormat(now));
      assertEquals(getOnlyElement(headers.get(COPY_SOURCE_IF_NO_MATCH)), "\"" + etag + "\"");
      for (String value : goodMeta.values())
         assertTrue(headers.containsValue(value));

   }

   @Test
   public void testAclDefault() {
      CopyObjectOptions options = new CopyObjectOptions();
      assertEquals(options.getAcl(), CannedAccessPolicy.PRIVATE);
   }

   @Test
   public void testAclStatic() {
      CopyObjectOptions options = overrideAcl(CannedAccessPolicy.AUTHENTICATED_READ);
      assertEquals(options.getAcl(), CannedAccessPolicy.AUTHENTICATED_READ);
   }

   @Test
   void testBuildRequestHeadersACL() {
      CopyObjectOptions options = overrideAcl(CannedAccessPolicy.AUTHENTICATED_READ);
      options.setHeaderTag(DEFAULT_AMAZON_HEADERTAG);

      options.setMetadataPrefix(USER_METADATA_PREFIX);

      Multimap<String, String> headers = options.buildRequestHeaders();

      assertEquals(headers.get(CANNED_ACL).iterator().next(), CannedAccessPolicy.AUTHENTICATED_READ.toString());
   }
}
