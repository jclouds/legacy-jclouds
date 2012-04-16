package org.jclouds.vcloud.director.v1_5.internal;

import static org.testng.Assert.assertEquals;

import java.io.Closeable;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorContext;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncClient;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Role.DefaultRoles;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class VCloudDirectorTestSession implements Closeable {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String provider;
      private String identity;
      private String credential;
      private Properties overrides;
      private String endpoint;

      public Builder provider(String provider) {
         this.provider = provider;
         return this;
      }

      public Builder identity(String identity) {
         this.identity = identity;
         return this;
      }

      public Builder credential(String credential) {
         this.credential = credential;
         return this;
      }

      public Builder overrides(Properties overrides) {
         this.overrides = overrides;
         return this;
      }

      public Builder endpoint(String endpoint) {
         this.endpoint = endpoint;
         return this;
      }

      public VCloudDirectorTestSession build() {
         return new VCloudDirectorTestSession(provider, identity, credential, overrides, endpoint);
      }
   }

   private VCloudDirectorContext userContext;
   private RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext;

   private VCloudDirectorTestSession(String provider, String identity, String credential, Properties overrides, String endpoint) {
      ContextBuilder builder = ContextBuilder.newBuilder(provider)
            .credentials(identity, credential)
            .endpoint(endpoint)
            .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()))
            .overrides(overrides);
      userContext = VCloudDirectorContext.class.cast(builder.build());

      // Look for the admin link in the current session
      Link admin = Iterables.tryFind(
	            userContext.getApi().getCurrentSession().getLinks(),
	            Predicates.and(LinkPredicates.relEquals(Link.Rel.DOWN), ReferencePredicates.typeEquals(VCloudDirectorMediaType.ADMIN)))
            .orNull();

      // Get the admin context if the link exists
      if (admin != null) {
         adminContext = userContext.getAdminContext();

         // Lookup the user details
         Reference orgRef = Iterables.getFirst(userContext.getApi().getOrgClient().getOrgList().getOrgs(), null)
               .toAdminReference(endpoint);
         Reference userRef = Iterables.find(
               adminContext.getApi().getOrgClient().getOrg(orgRef.getHref()).getUsers(),
               ReferencePredicates.nameEquals(adminContext.getApi().getCurrentSession().getUser()));
         User user = adminContext.getApi().getUserClient().getUser(userRef.getHref());

         // Check that the user has the org admin role
         Reference userRole = user.getRole();
         assertEquals(userRole.getName(), DefaultRoles.ORG_ADMIN.value());
      }
   }

   @Override
   public void close() {
      Closeables.closeQuietly(userContext);
      Closeables.closeQuietly(adminContext);
   }

   public VCloudDirectorContext getUserContext() {
      return userContext;
   }

   public RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> getAdminContext() {
      return adminContext;
   }
}
