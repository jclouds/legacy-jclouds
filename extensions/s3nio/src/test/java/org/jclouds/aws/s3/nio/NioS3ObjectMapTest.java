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
package org.jclouds.aws.s3.nio;

import java.util.Properties;

import org.jclouds.aws.s3.S3ObjectMapTest;
import org.jclouds.aws.s3.nio.config.S3HttpNioConnectionPoolClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "s3.S3ObjectMapTest")
public class NioS3ObjectMapTest extends S3ObjectMapTest {
    
    @Override
    protected Properties buildS3Properties(String AWSAccessKeyId,
	    String AWSSecretAccessKey) {
	Properties properties = super.buildS3Properties(AWSAccessKeyId,
		AWSSecretAccessKey);
	properties.setProperty("jclouds.http.pool.max_connection_reuse", "75");
	properties.setProperty("jclouds.http.pool.max_session_failures", "2");
	properties
		.setProperty("jclouds.http.pool.request_invoker_threads", "1");
	properties.setProperty("jclouds.http.pool.io_worker_threads", "2");
	properties.setProperty("jclouds.pool.max_connections", "12");
	return properties;
    }

    @Override
    protected Module createHttpModule() {
	return new S3HttpNioConnectionPoolClientModule();
    }

}
