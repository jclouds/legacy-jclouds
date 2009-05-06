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
package org.jclouds.samples.googleappengine.functest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3Constants;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Starts up the Google App Engine for Java Development environment and deploys
 * an application which tests S3.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "integration", enabled = true, sequential = true, testName = "functionalTests")
public class GoogleAppEngineTest extends BaseGoogleAppEngineTest {

    private static final String sysAWSAccessKeyId = System
	    .getProperty(S3Constants.PROPERTY_AWS_ACCESSKEYID);
    private static final String sysAWSSecretAccessKey = System
	    .getProperty(S3Constants.PROPERTY_AWS_SECRETACCESSKEY);

    @BeforeTest
    @Parameters( { "warfile", "devappserver.address", "devappserver.port",
	    S3Constants.PROPERTY_AWS_ACCESSKEYID,
	    S3Constants.PROPERTY_AWS_SECRETACCESSKEY })
    public void startDevAppServer(final String warfile, final String address,
	    final String port, @Optional String AWSAccessKeyId,
	    @Optional String AWSSecretAccessKey) throws Exception {
	AWSAccessKeyId = AWSAccessKeyId != null ? AWSAccessKeyId
		: sysAWSAccessKeyId;
	AWSSecretAccessKey = AWSSecretAccessKey != null ? AWSSecretAccessKey
		: sysAWSSecretAccessKey;

	checkNotNull(AWSAccessKeyId, "AWSAccessKeyId");
	checkNotNull(AWSSecretAccessKey, "AWSSecretAccessKey");

	Properties props = new Properties();
	props.put(S3Constants.PROPERTY_AWS_ACCESSKEYID, AWSAccessKeyId);
	props.put(S3Constants.PROPERTY_AWS_SECRETACCESSKEY, AWSSecretAccessKey);
	writePropertiesAndStartServer(address, port, warfile, props);
    }

    @Test
    public void shouldPass() throws InterruptedException, IOException {
	InputStream i = url.openStream();
	String string = IOUtils.toString(i);
	assert string.indexOf("Hello World!") >= 0 : string;
    }

    @Test(invocationCount = 50, enabled = true, threadPoolSize = 10)
    public void testGuiceJCloudsServed() throws InterruptedException,
	    IOException {
	URL gurl = new URL(url, "/guice/listbuckets.s3");
	InputStream i = gurl.openStream();
	String string = IOUtils.toString(i);
	assert string.indexOf("List") >= 0 : string;
    }
}
