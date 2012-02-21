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

import static org.testng.Assert.*;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * Tests behavior of {@link VCloudDirectorClient}
 * 
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@Test(groups = "live")
public abstract class BaseVCloudDirectorClientLiveTest extends BaseVersionedServiceLiveTest {

   protected BaseVCloudDirectorClientLiveTest() {
      provider = "vcloud-director";
   }

   protected String catalogName;
   protected String mediaId;
   protected String vAppTemplateId;
   protected String networkId;
   protected String vDCId;

   @Override
   protected Properties setupProperties() {
      Properties overrides= super.setupProperties();
      if (catalogName != null)
         overrides.setProperty(provider + ".catalog-name", catalogName);
      if (mediaId != null)
         overrides.setProperty(provider + ".media-id", mediaId);
      if (vAppTemplateId != null)
         overrides.setProperty(provider + ".vapptemplate-id", vAppTemplateId);
      if (networkId != null)
         overrides.setProperty(provider + ".network-id", networkId);
      if (vDCId != null)
         overrides.setProperty(provider + ".vcd-id", vDCId);
      return overrides;
   }
   
   @BeforeClass(inheritGroups = true)
   // NOTE Implement as required to populate xxxClient fields, or NOP
   public abstract void setupRequiredClients();

   /** Injected by {@link #setupContext} */
   public Predicate<URI> retryTaskSuccess;

   @Override
   @BeforeClass(groups = { "live" })
   protected void setupCredentials() {
      super.setupCredentials();

      catalogName = System.getProperty("test." + provider + ".catalog-name");
      mediaId = System.getProperty("test." + provider + ".media-id");
      vAppTemplateId = System.getProperty("test." + provider + ".vapptemplate-id");
      networkId = System.getProperty("test." + provider + ".network-id");
      vDCId = System.getProperty("test." + provider + ".vdc-id");
   }
   
   protected RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context;

   @BeforeClass(groups = { "live" })
   public void setupContext() {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new RestContextFactory().createContext(provider, identity, credential,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), overrides);

      TaskSuccess taskSuccess = context.utils().injector().getInstance(TaskSuccess.class);
      retryTaskSuccess = new RetryablePredicate<URI>(taskSuccess, 1000L);
   }

   protected void tearDown() {
      if (context != null)
         context.close();
   }
}
