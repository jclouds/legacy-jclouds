/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;
import java.util.concurrent.Executors;

import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.s3.S3AsyncClient;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests the default JClouds client.
 * 
 * @author Adrian Cole
 * 
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, groups = { "live" })
public class JCloudsPerformanceLiveTest extends BaseJCloudsPerformanceLiveTest {

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      exec = Executors.newCachedThreadPool();
      String accesskeyid = System.getProperty("test.aws-s3.identity");
      String secretkey = System.getProperty("test.aws-s3.credential");
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 50 + "");
      overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 30 + "");
      overrides.setProperty(PROPERTY_IO_WORKER_THREADS, 50 + "");
      overrides.setProperty(PROPERTY_USER_THREADS, 0 + "");
      String contextName = "standard";
      overrideWithSysPropertiesAndPrint(overrides, contextName);
      context = new BlobStoreContextFactory().createContext("aws-s3", accesskeyid, secretkey, ImmutableSet.of(
            new NullLoggingModule(), new EnterpriseConfigurationModule()), overrides);
   }

   @Override
   public S3AsyncClient getApi() {
      return (S3AsyncClient) context.getProviderSpecificContext().getAsyncApi();
   }
}