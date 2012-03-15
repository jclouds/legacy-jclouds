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

import javax.inject.Inject;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.testng.FormatApiResultsListener;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.predicates.ReferenceTypePredicates;
import org.jclouds.vcloud.director.v1_5.predicates.TaskSuccess;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * Tests behavior of {@link VCloudDirectorClient} and acts as parent for other client live tests.
 *
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = "live")
public abstract class BaseVCloudDirectorClientLiveTest extends BaseVersionedServiceLiveTest {

   protected BaseVCloudDirectorClientLiveTest() {
      provider = "vcloud-director";
   }

   // NOTE Implement as required to populate xxxClient fields, or NOP
   public abstract void setupRequiredClients();

   public Predicate<Task> retryTaskSuccess;

   @Inject
   protected void initTaskSuccess(TaskSuccess taskSuccess) {
      retryTaskSuccess = new RetryablePredicate<Task>(taskSuccess, 1000L);
   }

   protected RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context;
   protected Session session;

   @BeforeClass(groups = { "live" })
   public void setupContext() {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new RestContextFactory().createContext(provider, identity, credential, ImmutableSet.<Module> of(
               new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
      session = context.getApi().getCurrentSession();
      context.utils().injector().injectMembers(this);
      initTestParametersFromPropertiesOrLazyDiscover();
      setupRequiredClients();
   }

   protected String catalogName;
   protected String networkName;
   protected String userName;

   protected URI vAppTemplateURI;
   protected URI mediaURI;
   protected URI networkURI;
   protected URI vdcURI;
   protected URI userURI;

   // TODO change properties to URI, not id
   @SuppressWarnings("unchecked")
   protected void initTestParametersFromPropertiesOrLazyDiscover() {
      catalogName = Strings.emptyToNull(System.getProperty("test." + provider + ".catalog-name"));
      networkName = Strings.emptyToNull(System.getProperty("test." + provider + ".network-name"));

      String vAppTemplateId = Strings.emptyToNull(System.getProperty("test." + provider + ".vapptemplate-id"));
      if (vAppTemplateId != null)
         vAppTemplateURI = URI.create(endpoint + "/vAppTemplate/" + vAppTemplateId);

      String vdcId = Strings.emptyToNull(System.getProperty("test." + provider + ".vdc-id"));
      if (vdcId != null)
         vdcURI = URI.create(endpoint + "/vdc/" + vdcId);

      String mediaId = Strings.emptyToNull(System.getProperty("test." + provider + ".media-id"));
      if (mediaId != null)
         mediaURI = URI.create(endpoint + "/media/" + mediaId);

      String networkId = Strings.emptyToNull(System.getProperty("test." + provider + ".network-id"));
      if (networkId != null)
         networkURI = URI.create(endpoint + "/network/" + networkId);

      String userId = Strings.emptyToNull(System.getProperty("test." + provider + ".user-id"));
      if (userId != null)
         userURI = URI.create(endpoint + "/admin/user/" + userId);

      if (Iterables.any(Lists.newArrayList(catalogName, vAppTemplateURI, networkURI, vdcURI), Predicates.isNull())) {
         Org thisOrg = context.getApi().getOrgClient().getOrg(
                  Iterables.find(context.getApi().getOrgClient().getOrgList().getOrgs(),
                           ReferenceTypePredicates.<Reference> nameEquals(session.getOrg())).getHref());

         if (vdcURI == null)
            vdcURI = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.VDC)).getHref();

         if (networkURI == null)
            networkURI = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.ORG_NETWORK)).getHref();

         if (catalogName == null)
            catalogName = Iterables.find(thisOrg.getLinks(),
                     ReferenceTypePredicates.<Link> typeEquals(VCloudDirectorMediaType.CATALOG)).getName();

         // TODO look for default networkName
      }
   }

   protected void tearDown() {
      if (context != null)
         context.close();
   }
   
   public URI toAdminUri(ReferenceType ref) {
      return toAdminUri(ref.getHref());
   }
   
   public URI toAdminUri(URI uri) {
      return Reference.builder().href(uri).build().toAdminReference(endpoint).getHref();
   }
}
