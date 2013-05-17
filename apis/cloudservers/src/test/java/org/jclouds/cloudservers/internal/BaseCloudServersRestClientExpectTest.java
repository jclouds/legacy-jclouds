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
package org.jclouds.cloudservers.internal;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Date;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudservers.CloudServersApiMetadata;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.keystone.v1_1.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Base class for writing CloudServers Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseCloudServersRestClientExpectTest extends BaseKeystoneRestClientExpectTest<CloudServersClient> {

   public BaseCloudServersRestClientExpectTest() {
      provider = "cloudservers";
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudServersApiMetadata();
   }


   @Override
   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(provider + ".endpoint", endpoint);
      return overrides;
   }

   protected static final String CONSTANT_DATE = "2009-11-08T15:54:08.897Z";

   /**
    * override so that we can control the timestamp used in
    * {@link AddTimestampQuery}
    */
   @Override
   protected Module createModule() {
      return new TestCloudServersRestClientModule();
   }

   @ConfiguresRestClient
      protected static class TestCloudServersRestClientModule extends CloudServersRestClientModule {

      @Override
      public Supplier<Date> provideCacheBusterDate() {
         return new Supplier<Date>() {
            public Date get() {
               return new SimpleDateFormatDateService().iso8601DateParse(CONSTANT_DATE);
            }
         };
      }
   }
}
