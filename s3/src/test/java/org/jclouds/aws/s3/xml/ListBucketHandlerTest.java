/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.http.HttpException;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ListBucketHandlerTest extends BaseHandlerTest {
    public static final String listBucketWithPrefixAppsSlash = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Name>adriancole.org.jclouds.aws.s3.amazons3testdelimiter</Name><Prefix>apps/</Prefix><Marker></Marker><MaxKeys>1000</MaxKeys><IsTruncated>false</IsTruncated><Contents><Key>apps/0</Key><LastModified>2009-05-07T18:27:08.000Z</LastModified><ETag>&quot;c82e6a0025c31c5de5947fda62ac51ab&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/1</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;944fab2c5a9a6bacf07db5e688310d7a&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/2</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;a227b8888045c8fd159fb495214000f0&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/3</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;c9caa76c3dec53e2a192608ce73eef03&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/4</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;1ce5d0dcc6154a647ea90c7bdf82a224&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/5</Key><LastModified>2009-05-07T18:27:09.000Z</LastModified><ETag>&quot;79433524d87462ee05708a8ef894ed55&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/6</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;dd00a060b28ddca8bc5a21a49e306f67&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/7</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;8cd06eca6e819a927b07a285d750b100&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/8</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;174495094d0633b92cbe46603eee6bad&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents><Contents><Key>apps/9</Key><LastModified>2009-05-07T18:27:10.000Z</LastModified><ETag>&quot;cd8a19b26fea8a827276df0ad11c580d&quot;</ETag><Size>8</Size><Owner><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID><DisplayName>ferncam</DisplayName></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>";
    public static final String listBucketWithSlashDelimiterAndCommonPrefixApps = "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"> <Delimiter>/</Delimiter> <CommonPrefixes><Prefix>apps/</Prefix></CommonPrefixes></ListBucketResult>";
    ParseSax<S3Bucket> parser;

    @BeforeMethod
    void setUpParser() {
	parser = parserFactory.createListBucketParser();
	((ListBucketHandler) parser.getHandler()).setBucketName("test");
    }

    @Test
    public void testListMyBucketsWithDelimiterSlashAndCommonPrefixesAppsSlash()
	    throws HttpException {

	S3Bucket bucket = parser.parse(IOUtils
		.toInputStream(listBucketWithSlashDelimiterAndCommonPrefixApps));
	assertEquals(bucket.getCommonPrefixes().iterator().next(), "apps/");
	assertEquals(bucket.getDelimiter(), "/");
	assert bucket.getMarker() == null;
    }

    @Test
    public void testListMyBucketsWithPrefixAppsSlash() throws HttpException {

	S3Bucket bucket = parser.parse(IOUtils
		.toInputStream(listBucketWithPrefixAppsSlash));
	assertEquals(bucket.getPrefix(), "apps/");
	assertEquals(bucket.getMaxKeys(), 1000);
	assert bucket.getMarker() == null;

    }

}
