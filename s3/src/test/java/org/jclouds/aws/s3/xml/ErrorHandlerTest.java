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

import static org.testng.Assert.*;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.http.HttpException;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class ErrorHandlerTest {
    private S3ParserFactory parserFactory = null;
    private Injector injector;

    @BeforeMethod
    protected void setUpInjector() {
	injector = Guice.createInjector(new S3ParserModule());
	parserFactory = injector.getInstance(S3ParserFactory.class);
	assert parserFactory != null;
    }

    @AfterMethod
    protected void tearDownInjector() {
	parserFactory = null;
	injector = null;
    }

    public static final String errorFromAmazonIfYouDontRemoveTransferEncodingHeader = "<Error><Code>NotImplemented</Code><Message>A header you provided implies functionality that is not implemented</Message><Header>Transfer-Encoding</Header><RequestId>7C59925D75D15561</RequestId><HostId>fbskVU51OZJg2yZS/wNIxoE2PmCf0ZqFd0iH6Vrzw0uKG3KmokswBytL/Bfp/GWb</HostId></Error>";

    @Test
    public void testErrorFromAmazonIfYouDontRemoveTransferEncodingHeader()
	    throws HttpException {
	ParseSax<S3Error> parser = parserFactory.createErrorParser();
	S3Error error = parser
		.parse(IOUtils
			.toInputStream(errorFromAmazonIfYouDontRemoveTransferEncodingHeader));
	assertEquals(error.getCode(), "NotImplemented");
	assertEquals(error.getMessage(),
		"A header you provided implies functionality that is not implemented");
	assertEquals(error.getHeader(), "Transfer-Encoding");
	assertEquals(error.getRequestId(), "7C59925D75D15561");
	assertEquals(error.getHostId(),
		"fbskVU51OZJg2yZS/wNIxoE2PmCf0ZqFd0iH6Vrzw0uKG3KmokswBytL/Bfp/GWb");
    }

    public static final String badRequestWhenSourceIsDestBucketOnCopy400 = "<Error><Code>InvalidRequest</Code><Message>The Source and Destination may not be the same when the MetadataDirective is Copy.</Message><RequestId>54C77CAF4D42474B</RequestId><HostId>SJecknEUUUx88/65VAKbCdKSOCkpuVTeu7ZG9in9x9NTNglGnoxdbALCfS4k/DUZ</HostId></Error>";
    public static final String noSuchSourceKeyOrBucketOnCopy404 = "<Error><Code>NoSuchKey</Code><Message>The specified key does not exist.</Message><Key>null</Key><RequestId>9CCDF1DACA78B36F</RequestId><HostId>63cqk9YsTFBVfBfks840JVGsepPEdQM42mU+r7HN35sF4Nk5xAcWDEUPaQpK2eFU</HostId></Error>";
    public static final String noSuchDestinationBucketOnCopy404 = "<Error><Code>NoSuchBucket</Code><Message>The specified bucket does not exist</Message><BucketName>copydestination</BucketName><RequestId>4F0CF319C5535975</RequestId><HostId>hdZyHOm7VK+JI2UCdye3d6TVkKhRBIoWflldXVDTKbgipYlamy8HgPBzHrUAVQNJ</HostId></Error>";
    public static final String badSign403 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    + "<Error><Code>SignatureDoesNotMatch</Code><Message>The operation signature we calculated does not match the signature you provided. Check your key and signing method.</Message><StringToSignBytes>47 45 54 0a 0a 0a 54 68 75 2c 20 31 39 20 4d 61 72 20 32 30 30 39 20 31 37 3a 34 38 3a 30 31 20 47 4d 54 0a 2f 61 64 72 69 61 6e 63 6f 6c 65 2e 73 33 2e 61 6d 61 7a 6f 6e 73 33 74 65 73 74 2e 66 69 6c 65 74 65 73 74 73 66 6f 72 61 64 72 69 61 6e 2f 66 69 6c 65</StringToSignBytes><RequestId>514AA22EB75A6E42</RequestId><HostId>H5nqnZkGjuKvB+seutvx5hnp1P+WAuC9c3Y7MdQCcYDr1TGwNX/mt+FHstK0pVld</HostId><SignatureProvided>Qm6Wss7e5e/eNXV50AxChH+xkLI=</SignatureProvided><StringToSign>GET\n"
	    + "\n"
	    + "\n"
	    + "Thu, 19 Mar 2009 17:48:01 GMT\n"
	    + "/adriancole.s3.amazons3test.filetestsforadrian/file</StringToSign><AWSAccessKeyId>0101100101001001</AWSAccessKeyId></Error>";
    public static final String amazonHadAnError500 = "<Error><Code>InternalError</Code><Message>We encountered an internal error. Please try again.</Message><RequestId>EF6FA7A639CAFF15</RequestId><HostId>tBkX23mIeq2riHsNw2YShupMlZ9+iy3V/uN+lRhqCR4qHTE07ujFeyAUPTowvuH/</HostId></Error>";

}
