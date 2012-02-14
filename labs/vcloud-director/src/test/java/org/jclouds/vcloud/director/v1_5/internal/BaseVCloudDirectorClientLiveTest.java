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
package org.jclouds.vcloud.director.v1_5.internal;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;
import org.jclouds.vcloud.director.v1_5.features.OrgClient;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VCloudDirectorClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseVCloudDirectorClientLiveTest extends BaseVersionedServiceLiveTest implements OrgClient, TaskClient {
   public BaseVCloudDirectorClientLiveTest() {
      provider = "vcloud-director";
   }
   
   protected RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, identity, credential,
               ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()), overrides);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }


   private final OrgClient orgClient = context.getApi().getOrgClient();
   private final TaskClient taskClient = context.getApi().getTaskClient();

   /*
    * Proxying implementations of OrgClient.
    */

   /** @see OrgClient#getOrgList() */
   @Override
   public OrgList getOrgList() {
      return orgClient.getOrgList();
   }

   /** @see OrgClient#getOrg(ReferenceType) */
   @Override
   public Org getOrg(ReferenceType<?> orgRef) {
      return orgClient.getOrg(orgRef);
   }

   /** @see OrgClient#getOrgMetadata(ReferenceType) */
   @Override
   public Metadata getOrgMetadata(ReferenceType<?> orgRef) {
      return orgClient.getOrgMetadata(orgRef);
   }

   /** @see OrgClient#getOrgMetadataEntry(ReferenceType, String) */
   @Override
   public MetadataEntry getOrgMetadataEntry(ReferenceType<?> orgRef, String key) {
      return orgClient.getOrgMetadataEntry(orgRef, key);
   }

   /*
    * Proxying implementations of TaskClient.
    */

   /** @see TaskClient#getTaskList(ReferenceType) */
   @Override
   public TasksList getTaskList(ReferenceType<?> orgRef) {
      return taskClient.getTaskList(orgRef);
   }

   /** @see TaskClient#getTask(URI) */
   @Override
   public Task getTask(URI taskUri) {
      return taskClient.getTask(taskUri);
   }

   /** @see TaskClient#cancelTask(URI */
   @Override
   public void cancelTask(URI taskUri) {
      taskClient.cancelTask(taskUri);
   }
}
