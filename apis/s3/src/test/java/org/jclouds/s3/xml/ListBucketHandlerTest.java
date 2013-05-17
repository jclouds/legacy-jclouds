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
package org.jclouds.s3.xml;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.TreeSet;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.s3.domain.CanonicalUser;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.s3.domain.internal.ListBucketResponseImpl;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListBucketHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListBucketHandlerTest")
public class ListBucketHandlerTest extends BaseHandlerTest {
   public static final String listBucketWithPrefixAppsSlash = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Name>adriancole.org.jclouds.s3.amazons3testdelimiter</Name><Prefix>apps/</Prefix><Marker></Marker><MaxKeys>1000</MaxKeys><IsTruncated>false</IsTruncated><Contents><Key>apps/0</Key><LastModified>2009-05-07T18:27:08.000Z</LastModified><ETag>&quot;c82e6a0025c31c5de5947fda62ac51ab&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/1</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;944fab2c5a9a6bacf07db5e688310d7a&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/2</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;a227b8888045c8fd159fb495214000f0&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/3</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;c9caa76c3dec53e2a192608ce73eef03&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/4</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;1ce5d0dcc6154a647ea90c7bdf82a224&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/5</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;79433524d87462ee05708a8ef894ed55&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/6</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;dd00a060b28ddca8bc5a21a49e306f67&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/7</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;8cd06eca6e819a927b07a285d750b100&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/8</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;174495094d0633b92cbe46603eee6bad&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/9</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;cd8a19b26fea8a827276df0ad11c580d&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>";
   public static final String listBucketWithSlashDelimiterAndCommonPrefixApps = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"> <Delimiter>/</Delimiter> <CommonPrefixes><Prefix>apps/</Prefix></CommonPrefixes></ListBucketResult>";
   private DateService dateService = new SimpleDateFormatDateService();

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/list_bucket.xml");

      ListBucketResponse result = createParser().parse(is);

      ListBucketResponse expected = expected();

      assertEquals(result.toString(), expected.toString());
   }

   public ListBucketResponse expected() {
      CanonicalUser owner = new CanonicalUser("e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0",
               "ferncam");
      String bucket = "adriancole.org.jclouds.aws.s3.amazons3testdelimiter";
      ListBucketResponse expected = new ListBucketResponseImpl(bucket, ImmutableList.<ObjectMetadata> of(
               new ObjectMetadataBuilder().key("apps/0").bucket(bucket).uri(URI.create("http://bucket.com/apps/0"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:08.000Z")).eTag(
                                 "\"c82e6a0025c31c5de5947fda62ac51ab\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("c82e6a0025c31c5de5947fda62ac51ab")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/1").bucket(bucket).uri(URI.create("http://bucket.com/apps/1"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:09.000Z")).eTag(
                                 "\"944fab2c5a9a6bacf07db5e688310d7a\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("944fab2c5a9a6bacf07db5e688310d7a")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/2").bucket(bucket).uri(URI.create("http://bucket.com/apps/2"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:09.000Z")).eTag(
                                 "\"a227b8888045c8fd159fb495214000f0\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("a227b8888045c8fd159fb495214000f0")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/3").bucket(bucket).uri(URI.create("http://bucket.com/apps/3"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:09.000Z")).eTag(
                                 "\"c9caa76c3dec53e2a192608ce73eef03\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("c9caa76c3dec53e2a192608ce73eef03")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/4").bucket(bucket).uri(URI.create("http://bucket.com/apps/4"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:09.000Z")).eTag(
                                 "\"1ce5d0dcc6154a647ea90c7bdf82a224\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("1ce5d0dcc6154a647ea90c7bdf82a224")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/5").bucket(bucket).uri(URI.create("http://bucket.com/apps/5"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:09.000Z")).eTag(
                                 "\"79433524d87462ee05708a8ef894ed55\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("79433524d87462ee05708a8ef894ed55")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/6").bucket(bucket).uri(URI.create("http://bucket.com/apps/6"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:10.000Z")).eTag(
                                 "\"dd00a060b28ddca8bc5a21a49e306f67\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("dd00a060b28ddca8bc5a21a49e306f67")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/7").bucket(bucket).uri(URI.create("http://bucket.com/apps/7"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:10.000Z")).eTag(
                                 "\"8cd06eca6e819a927b07a285d750b100\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("8cd06eca6e819a927b07a285d750b100")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/8").bucket(bucket).uri(URI.create("http://bucket.com/apps/8"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:10.000Z")).eTag(
                                 "\"174495094d0633b92cbe46603eee6bad\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("174495094d0633b92cbe46603eee6bad")).contentLength(8l).build(),
               new ObjectMetadataBuilder().key("apps/9").bucket(bucket).uri(URI.create("http://bucket.com/apps/9"))
                        .lastModified(dateService.iso8601DateParse("2009-05-07T18:27:10.000Z")).eTag(
                                 "\"cd8a19b26fea8a827276df0ad11c580d\"").owner(owner).contentMD5(
                                 base16().lowerCase().decode("cd8a19b26fea8a827276df0ad11c580d")).contentLength(8l).build()),
               "apps/", null, null, 1000, null, false, new TreeSet<String>());
      return expected;
   }

   ParseSax<ListBucketResponse> createParser() {
      return factory.create(injector.getInstance(ListBucketHandler.class)).setContext(
               HttpRequest.builder().method("GET").endpoint("http://bucket.com").build());
   }

   @Test
   public void testListMyBucketsWithDelimiterSlashAndCommonPrefixesAppsSlash() throws HttpException {

      ListBucketResponse bucket = createParser().parse(
               Strings2.toInputStream(listBucketWithSlashDelimiterAndCommonPrefixApps));
      assertEquals(bucket.getCommonPrefixes().iterator().next(), "apps/");
      assertEquals(bucket.getDelimiter(), "/");
      assert bucket.getMarker() == null;
   }

   @Test
   public void testListMyBucketsWithPrefixAppsSlash() throws HttpException {

      ListBucketResponse bucket = createParser().parse(Strings2.toInputStream(listBucketWithPrefixAppsSlash));
      assertEquals(bucket.getPrefix(), "apps/");
      assertEquals(bucket.getMaxKeys(), 1000);
      assert bucket.getMarker() == null;

   }

}
