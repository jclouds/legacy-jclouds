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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.Tag;
import org.jclouds.aws.ec2.util.TagFilters;
import org.jclouds.aws.ec2.util.TagFilters.ResourceType;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@code TagClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "live", singleThreaded = true)
public class TagClientLiveTest extends BaseComputeServiceContextLiveTest {
   public TagClientLiveTest() {
      provider = "aws-ec2";
   }

   private TagClient client;
   protected String testGroup;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getTagServices();
      
      try {
         testGroup = view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getSecurityGroupServices().createSecurityGroupInRegionAndReturnId(null,
                  "test-group", "test-group");
      } catch (IllegalStateException e) {
         // already exists
         testGroup = Iterables.get(
                  view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getSecurityGroupServices().describeSecurityGroupsInRegion(null, "test-group"), 0)
                  .getId();
      }
   }

   @AfterGroups(groups = { "live" })
   public void deleteSecurityGroup() {
       view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getSecurityGroupServices().deleteSecurityGroupInRegionById(null, testGroup);
   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @Test
   void test() {
      cleanupTag(testGroup, "test-key");
      cleanupTag(testGroup, "empty-key");
      try {
         client.createTagsInRegion(null, ImmutableList.<String>builder().add(testGroup).build(), ImmutableMap.<String, String>builder().put("test-key", "test-value").build());
         checkTag(testGroup, ResourceType.SECURITY_GROUP, "test-key", "test-value");
         client.createTagsInRegion(null, ImmutableList.<String>builder().add(testGroup).build(), ImmutableMap.<String, String>builder().put("empty-key", "").build());
         checkTag(testGroup, ResourceType.SECURITY_GROUP, "empty-key", "");
      } finally {
	     cleanupTag(testGroup, "test-key");
	     cleanupTag(testGroup, "empty-key");
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
}
