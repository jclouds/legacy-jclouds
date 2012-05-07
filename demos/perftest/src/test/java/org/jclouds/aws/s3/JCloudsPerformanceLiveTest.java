/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;
import java.util.concurrent.Executors;

import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.netty.config.NettyPayloadModule;
import org.jclouds.s3.S3AsyncClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests the default JClouds client.
 * 
 * @author Adrian Cole
 * 
 */
@Test(singleThreaded = true, timeOut = 2 * 60 * 1000, groups = "live", testName = "JCloudsPerformanceLiveTest")
public class JCloudsPerformanceLiveTest extends BaseJCloudsPerformanceLiveTest {
   
   public JCloudsPerformanceLiveTest(){
      exec = Executors.newCachedThreadPool();
   }
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 50 + "");
      overrides.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 30 + "");
      overrides.setProperty(PROPERTY_IO_WORKER_THREADS, 50 + "");
      overrides.setProperty(PROPERTY_USER_THREADS, 0 + "");
      printPropertiesOfContext(overrides, "default");
      return overrides;
   }
   
   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module>builder()
                         .add(new NullLoggingModule())
                         .add(new NettyPayloadModule())
                         .add(new EnterpriseConfigurationModule()).build();
   }
   
   @Override
   public S3AsyncClient getApi() {
      return view.unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getAsyncApi();
   }
}