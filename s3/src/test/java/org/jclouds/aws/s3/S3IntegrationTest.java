/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

@Test(sequential=true)
public class S3IntegrationTest {

    private static final Handler HANDLER = new ConsoleHandler() {
	{
	    setLevel(Level.ALL);
	    setFormatter(new Formatter() {

		@Override
		public String format(LogRecord record) {
		    return String.format("[%tT %-7s] [%-7s] [%s]: %s %s\n",
			    new Date(record.getMillis()), record.getLevel(),
			    Thread.currentThread().getName(), record
				    .getLoggerName(), record.getMessage(),
			    record.getThrown() == null ? "" : record
				    .getThrown());
		}
	    });
	}
    };

    static {
	Logger guiceLogger = Logger.getLogger("org.jclouds");
	guiceLogger.addHandler(HANDLER);
	guiceLogger.setLevel(Level.ALL);
    }

    
    protected String bucketPrefix = System.getProperty("user.name")
    	    + "." + this.getClass().getName();
    String badRequestWhenSourceIsDestBucketOnCopy400 = "<Error><Code>InvalidRequest</Code><Message>The Source and Destination may not be the same when the MetadataDirective is Copy.</Message><RequestId>54C77CAF4D42474B</RequestId><HostId>SJecknEUUUx88/65VAKbCdKSOCkpuVTeu7ZG9in9x9NTNglGnoxdbALCfS4k/DUZ</HostId></Error>";
    String noSuchSourceKeyOrBucketOnCopy404 = "<Error><Code>NoSuchKey</Code><Message>The specified key does not exist.</Message><Key>null</Key><RequestId>9CCDF1DACA78B36F</RequestId><HostId>63cqk9YsTFBVfBfks840JVGsepPEdQM42mU+r7HN35sF4Nk5xAcWDEUPaQpK2eFU</HostId></Error>";
    String noSuchDestinationBucketOnCopy404 = "<Error><Code>NoSuchBucket</Code><Message>The specified bucket does not exist</Message><BucketName>copydestination</BucketName><RequestId>4F0CF319C5535975</RequestId><HostId>hdZyHOm7VK+JI2UCdye3d6TVkKhRBIoWflldXVDTKbgipYlamy8HgPBzHrUAVQNJ</HostId></Error>";
    String successfulCopyObject200 = "<CopyObjectResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><LastModified>2009-03-19T13:23:27.000Z</LastModified><ETag>\"92836a3ea45a6984d1b4d23a747d46bb\"</ETag></CopyObjectResult>";
    String badSign403 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    	    + "<Error><Code>SignatureDoesNotMatch</Code><Message>The operation signature we calculated does not match the signature you provided. Check your key and signing method.</Message><StringToSignBytes>47 45 54 0a 0a 0a 54 68 75 2c 20 31 39 20 4d 61 72 20 32 30 30 39 20 31 37 3a 34 38 3a 30 31 20 47 4d 54 0a 2f 61 64 72 69 61 6e 63 6f 6c 65 2e 73 33 2e 61 6d 61 7a 6f 6e 73 33 74 65 73 74 2e 66 69 6c 65 74 65 73 74 73 66 6f 72 61 64 72 69 61 6e 2f 66 69 6c 65</StringToSignBytes><RequestId>514AA22EB75A6E42</RequestId><HostId>H5nqnZkGjuKvB+seutvx5hnp1P+WAuC9c3Y7MdQCcYDr1TGwNX/mt+FHstK0pVld</HostId><SignatureProvided>Qm6Wss7e5e/eNXV50AxChH+xkLI=</SignatureProvided><StringToSign>GET\n"
    	    + "\n"
    	    + "\n"
    	    + "Thu, 19 Mar 2009 17:48:01 GMT\n"
    	    + "/adriancole.s3.amazons3test.filetestsforadrian/file</StringToSign><AWSAccessKeyId>0101100101001001</AWSAccessKeyId></Error>";
    String amazonHadAnError = "<Error><Code>InternalError</Code><Message>We encountered an internal error. Please try again.</Message><RequestId>EF6FA7A639CAFF15</RequestId><HostId>tBkX23mIeq2riHsNw2YShupMlZ9+iy3V/uN+lRhqCR4qHTE07ujFeyAUPTowvuH/</HostId></Error>";
    protected S3Connection client;
    Injector i = null;
    private static final String sysAWSAccessKeyId = System
    	    .getProperty("jclouds.aws.accesskeyid");

    protected Injector createInject(String AWSAccessKeyId, String AWSSecretAccessKey) {
        return S3ConnectionFactory.getInjector(buildS3Properties(
        	AWSAccessKeyId, AWSSecretAccessKey), createHttpModule());
    }

    protected Properties buildS3Properties(String AWSAccessKeyId, String AWSSecretAccessKey) {
        Properties properties = new Properties(
        	S3ConnectionFactory.DEFAULT_PROPERTIES);
        properties.setProperty("jclouds.aws.accesskeyid", checkNotNull(
        	AWSAccessKeyId, "AWSAccessKeyId"));
        properties.setProperty("jclouds.aws.secretaccesskey", checkNotNull(
        	AWSSecretAccessKey, "AWSSecretAccessKey"));
        properties.setProperty("jclouds.http.secure", "false");
        properties.setProperty("jclouds.http.port", "80");
        return properties;
    }

    protected Module createHttpModule() {
        return new JavaUrlHttpFutureCommandClientModule();
    }

    private static final String sysAWSSecretAccessKey = System
    	    .getProperty("jclouds.aws.secretaccesskey");

    @BeforeTest
    @Parameters( { "jclouds.aws.accesskeyid", "jclouds.aws.secretaccesskey" })
    protected void setUpClient(@Optional String AWSAccessKeyId, @Optional String AWSSecretAccessKey) throws Exception {
        i = createInject(AWSAccessKeyId != null ? AWSAccessKeyId
        	: sysAWSAccessKeyId,
        	AWSSecretAccessKey != null ? AWSSecretAccessKey
        		: sysAWSSecretAccessKey);
        client = i.getInstance(LiveS3Connection.class);
        deleteEverything();
    }

    protected void deleteEverything() throws Exception {
        try {
            List<S3Bucket> buckets = client.getBuckets().get();
            List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
            for (S3Bucket bucket : buckets) {
        	if (bucket.getName().startsWith(bucketPrefix.toLowerCase())) {
        	    bucket = client.getBucket(bucket).get();
        	    for (S3Object object : bucket.getContents()) {
        		results.add(client
        			.deleteObject(bucket, object.getKey()));
        	    }
        	    Iterator<Future<Boolean>> iterator = results.iterator();
        	    while (iterator.hasNext()) {
        		iterator.next().get();
        		iterator.remove();
        	    }
        	    client.deleteBucket(bucket).get();
        	}
    
            }
        } catch (CancellationException e) {
            throw e;
        }
    }

    @AfterTest
    protected void tearDownClient() throws Exception {
        deleteEverything();
        client.close();
        i = null;
    }

}