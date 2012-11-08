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
package org.jclouds.cloudstack.ec2.internal;

import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.cloudstack.ec2.config.CloudStackEC2RestClientModule;
import org.jclouds.date.DateService;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseCloudStackEC2RestClientExpectTest extends BaseRestClientExpectTest<CloudStackEC2Client> {
   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";

   public BaseCloudStackEC2RestClientExpectTest() {
      provider = "cloudstack-ec2";
   }

   @ConfiguresRestClient
   private static final class TestCloudStackEC2RestClientModule extends CloudStackEC2RestClientModule {
      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestCloudStackEC2RestClientModule();
   }
}
