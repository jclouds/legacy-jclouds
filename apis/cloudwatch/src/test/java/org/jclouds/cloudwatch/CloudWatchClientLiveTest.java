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
package org.jclouds.cloudwatch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.logging.Logger;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CloudWatchClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class CloudWatchClientLiveTest {

   private CloudWatchClient client;
   private RestContext<CloudWatchClient, CloudWatchAsyncClient> context;
   protected String provider = "cloudwatch";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint", null);
      apiversion = System.getProperty("test." + provider + ".apiversion", null);
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
               overrides);
      client = context.getApi();
   }

   @Test
   protected void testGetMetricStatisticsInRegion() {
      getMetricStatisticsInRegion(null);
   }

   protected void getMetricStatisticsInRegion(String region) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -1);

      Set<Datapoint> datapoints = client.getMetricStatisticsInRegion(
         region, "CPUUtilization", cal.getTime(), new Date(), 60, "Average");
      assert datapoints != null;
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
