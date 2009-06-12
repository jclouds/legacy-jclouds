/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.suncloud;

import org.jclouds.aws.s3.S3ConnectionIntegrationTest;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.suncloud.config.SunCloudS3ConnectionModule;
import org.jclouds.aws.s3.suncloud.xml.config.SunCloudS3ParserModule;
import org.testng.annotations.Test;

/**
 * This performs the same test as {@link S3ConnectionIntegrationTest}, except using Sun Cloud
 * Storage.
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, groups = { "live" }, testName = "s3.suncloud.SunCloudS3ConnectionLiveTest")
public class SunCloudS3ConnectionLiveTest extends S3ConnectionIntegrationTest {

   @Override
   protected boolean debugEnabled() {
      return true;
   }

   @Override
   protected S3ContextFactory buildS3ContextFactory(String AWSAccessKeyId, String AWSSecretAccessKey) {
      return S3ContextFactory.createContext(AWSAccessKeyId, AWSSecretAccessKey).withModules(
               new SunCloudS3ConnectionModule(), new SunCloudS3ParserModule()).withHttpAddress(
               "object.storage.network.com").withHttpSecure(false).withHttpPort(80);

   }

}
