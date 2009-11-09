/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.aws.s3.domain.internal.BucketListObjectMetadata;
import org.jclouds.aws.s3.domain.internal.TreeSetListBucketResponse;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "s3.ListBucketHandlerTest")
public class ListBucketHandlerTest extends BaseHandlerTest {
   public static final String listBucketWithPrefixAppsSlash = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Name>adriancole.org.jclouds.aws.s3.amazons3testdelimiter</Name><Prefix>apps/</Prefix><Marker></Marker><MaxKeys>1000</MaxKeys><IsTruncated>false</IsTruncated><Contents><Key>apps/0</Key><LastModified>2009-05-07T18:27:08.000Z</LastModified><ETag>&quot;c82e6a0025c31c5de5947fda62ac51ab&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/1</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;944fab2c5a9a6bacf07db5e688310d7a&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/2</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;a227b8888045c8fd159fb495214000f0&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/3</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;c9caa76c3dec53e2a192608ce73eef03&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/4</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;1ce5d0dcc6154a647ea90c7bdf82a224&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/5</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;79433524d87462ee05708a8ef894ed55&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/6</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;dd00a060b28ddca8bc5a21a49e306f67&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/7</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;8cd06eca6e819a927b07a285d750b100&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/8</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;174495094d0633b92cbe46603eee6bad&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/9</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;cd8a19b26fea8a827276df0ad11c580d&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>";
   public static final String listBucketWithSlashDelimiterAndCommonPrefixApps = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"> <Delimiter>/</Delimiter> <CommonPrefixes><Prefix>apps/</Prefix></CommonPrefixes></ListBucketResult>";
   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/s3/list_bucket.xml");
      CanonicalUser owner = new CanonicalUser(
               "e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0", "ferncam");
      ListBucketResponse expected = new TreeSetListBucketResponse(
               "adriancole.org.jclouds.aws.s3.amazons3testdelimiter", ImmutableList.of(
                        (ObjectMetadata) new BucketListObjectMetadata("apps/0", dateService
                                 .iso8601DateParse("2009-05-07T18:27:08.000Z"),
                                 "\"c82e6a0025c31c5de5947fda62ac51ab\"", HttpUtils
                                          .fromHexString("c82e6a0025c31c5de5947fda62ac51ab"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/1", dateService
                                 .iso8601DateParse("2009-05-07T18:27:09.000Z"),
                                 "\"944fab2c5a9a6bacf07db5e688310d7a\"", HttpUtils
                                          .fromHexString("944fab2c5a9a6bacf07db5e688310d7a"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/2", dateService
                                 .iso8601DateParse("2009-05-07T18:27:09.000Z"),
                                 "\"a227b8888045c8fd159fb495214000f0\"", HttpUtils
                                          .fromHexString("a227b8888045c8fd159fb495214000f0"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/3", dateService
                                 .iso8601DateParse("2009-05-07T18:27:09.000Z"),
                                 "\"c9caa76c3dec53e2a192608ce73eef03\"", HttpUtils
                                          .fromHexString("c9caa76c3dec53e2a192608ce73eef03"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/4", dateService
                                 .iso8601DateParse("2009-05-07T18:27:09.000Z"),
                                 "\"1ce5d0dcc6154a647ea90c7bdf82a224\"", HttpUtils
                                          .fromHexString("1ce5d0dcc6154a647ea90c7bdf82a224"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/5", dateService
                                 .iso8601DateParse("2009-05-07T18:27:09.000Z"),
                                 "\"79433524d87462ee05708a8ef894ed55\"", HttpUtils
                                          .fromHexString("79433524d87462ee05708a8ef894ed55"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/6", dateService
                                 .iso8601DateParse("2009-05-07T18:27:10.000Z"),
                                 "\"dd00a060b28ddca8bc5a21a49e306f67\"", HttpUtils
                                          .fromHexString("dd00a060b28ddca8bc5a21a49e306f67"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/7", dateService
                                 .iso8601DateParse("2009-05-07T18:27:10.000Z"),
                                 "\"8cd06eca6e819a927b07a285d750b100\"", HttpUtils
                                          .fromHexString("8cd06eca6e819a927b07a285d750b100"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/8", dateService
                                 .iso8601DateParse("2009-05-07T18:27:10.000Z"),
                                 "\"174495094d0633b92cbe46603eee6bad\"", HttpUtils
                                          .fromHexString("174495094d0633b92cbe46603eee6bad"), 8,
                                 owner, StorageClass.STANDARD),
                        (ObjectMetadata) new BucketListObjectMetadata("apps/9", dateService
                                 .iso8601DateParse("2009-05-07T18:27:10.000Z"),
                                 "\"cd8a19b26fea8a827276df0ad11c580d\"", HttpUtils
                                          .fromHexString("cd8a19b26fea8a827276df0ad11c580d"), 8,
                                 owner, StorageClass.STANDARD)), "apps/", null, 1000, null, false,
               new TreeSet<String>());

      ListBucketResponse result = (ListBucketResponse) factory.create(
               injector.getInstance(ListBucketHandler.class)).parse(is);

      assertEquals(result, expected);
   }

   ParseSax<ListBucketResponse> createParser() {
      ParseSax<ListBucketResponse> parser = (ParseSax<ListBucketResponse>) factory.create(injector
               .getInstance(ListBucketHandler.class));
      return parser;
   }

   @Test
   public void testListMyBucketsWithDelimiterSlashAndCommonPrefixesAppsSlash() throws HttpException {

      ListBucketResponse bucket = createParser().parse(
               IOUtils.toInputStream(listBucketWithSlashDelimiterAndCommonPrefixApps));
      assertEquals(bucket.getCommonPrefixes().iterator().next(), "apps/");
      assertEquals(bucket.getDelimiter(), "/");
      assert bucket.getMarker() == null;
   }

   @Test
   public void testListMyBucketsWithPrefixAppsSlash() throws HttpException {

      ListBucketResponse bucket = createParser().parse(
               IOUtils.toInputStream(listBucketWithPrefixAppsSlash));
      assertEquals(bucket.getPrefix(), "apps/");
      assertEquals(bucket.getMaxKeys(), 1000);
      assert bucket.getMarker() == null;

   }

}
