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
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Iterables.*;
import static org.testng.Assert.*;

import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.Tag;
import org.jclouds.aws.ec2.util.TagFilters;
import org.jclouds.aws.ec2.util.TagFilters.ResourceType;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Tests live behavior of {@code TagClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "live", sequential = true)
public class TagClientLiveTest {

   private TagClient client;
   private RestContext<AWSEC2Client, AWSEC2AsyncClient> context;

   protected String provider = "aws-ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected String testGroup;
   private ComputeServiceContext computeContext;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider + ".credential");
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
   public void setupClientAndSecurityGroup() {
      setupCredentials();
      Properties overrides = setupProperties();
      computeContext = new ComputeServiceContextFactory().createContext(provider, ImmutableSet.<Module> of(
               new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
      context = computeContext.getProviderSpecificContext();
      client = context.getApi().getTagServices();
      testGroup = context.getApi().getSecurityGroupServices().createSecurityGroupInRegionAndReturnId(null, "test-group", "test-group");
   }

   @AfterGroups(groups = { "live" })
   public void deleteSecurityGroup() {
       context.getApi().getSecurityGroupServices().deleteSecurityGroupInRegionById(null, testGroup);
   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @Test
   void test() {
      cleanupTag(testGroup, "test-key");
      try {
         client.createTagsInRegion(null, ImmutableList.<String>builder().add(testGroup).build(), ImmutableMap.<String, String>builder().put("test-key", "test-value").build());
         checkTag(testGroup, ResourceType.SECURITY_GROUP, "test-key", "test-value");
      } finally {
	     cleanupTag(testGroup, "test-key");
      }
   }

   protected void cleanupTag(String resourceId, String key) {
      try {
        client.deleteTagsInRegion(null, ImmutableList.<String>builder().add(resourceId).build(), ImmutableMap.<String, String>builder().put(key, null).build());
      } catch (Exception e) {
        // Ignore
      }
   }

   protected void checkTag(String resourceId, ResourceType resourceType, String key, String value) {
      Set<Tag> results = client.describeTagsInRegion(null, TagFilters.filters().resourceId(resourceId).resourceType(resourceType).keyValuePair(key, value).build());
      assertNotNull(results);
      assertEquals(results.size(), 1);
      Tag tag = Iterables.getOnlyElement(results);
      assertEquals(tag.getResourceId(), resourceId);
      assertEquals(tag.getResourceType(), resourceType);
      assertEquals(tag.getKey(), key);
      assertEquals(tag.getValue(), value);
   }

   protected AWSRunningInstance getInstance(AWSInstanceClient instanceClient, String id) {
      return getOnlyElement(getOnlyElement(instanceClient.describeInstancesInRegion(null, id)));
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
