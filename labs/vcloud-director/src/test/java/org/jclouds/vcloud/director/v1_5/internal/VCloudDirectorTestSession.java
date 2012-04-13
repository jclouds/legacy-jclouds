package org.jclouds.vcloud.director.v1_5.internal;

import static com.google.common.base.Objects.equal;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.ContextBuilder;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorContext;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncClient;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Role.DefaultRoles;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

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

   private RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext;
   private RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> userContext;

   private User createdAdminUser;
   private User createdUser;

   private VCloudDirectorTestSession(String provider, String identity, String credential, Properties overrides, String endpoint) {
      ContextBuilder<?, ?, ?, ?> builder = ContextBuilder.newBuilder(provider)
            .credentials(identity, credential)
            .endpoint(endpoint)
            .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()))
            .overrides(overrides);
      VCloudDirectorContext rootContext = VCloudDirectorContext.class.cast(builder.build());

      if (rootContext.getApi().getCurrentSession().getLinks().contains(Link.builder()
	         .rel("down")
	         .type("application/vnd.vmware.admin.vcloud+xml")
	         .href(URI.create(endpoint+"/admin/"))
	         .build())) {

         adminContext = rootContext.getAdminContext();

         Reference orgRef = Iterables.getFirst(rootContext.getApi().getOrgClient().getOrgList().getOrgs(), null)
               .toAdminReference(endpoint);
         assertNotNull(orgRef, String.format(REF_REQ_LIVE, "admin org"));

         Reference userRef = Iterables.find(adminContext.getApi().getOrgClient().getOrg(orgRef.getHref()).getUsers(),
               ReferencePredicates.nameEquals(adminContext.getApi().getCurrentSession().getUser()));

         User user = adminContext.getApi().getUserClient().getUser(userRef.getHref());
         Reference orgAdmin = user.getRole();
         assertTrue(equal(orgAdmin.getName(), DefaultRoles.ORG_ADMIN.value()), "must give org admin or user-only credentials");

         String adminIdentity = "testAdmin"+BaseVCloudDirectorClientLiveTest.getTestDateTimeStamp();
         String adminCredential = "testAdminPassword";

         createdAdminUser = rootContext.getAdminContext().getApi().getUserClient().createUser(orgRef.getHref(), User.builder()
            .name(adminIdentity)
            .password(adminCredential)
            .description("test user with user-level privileges")
            .role(orgAdmin)
            .deployedVmQuota(BaseVCloudDirectorClientLiveTest.REQUIRED_ADMIN_VM_QUOTA)
            .isEnabled(true)
            .build());

         Closeables.closeQuietly(rootContext);

         builder.credentials(adminIdentity, adminCredential);
         adminContext = VCloudDirectorContext.class.cast(builder.build()).getAdminContext();

         String userIdentity = "test"+BaseVCloudDirectorClientLiveTest.getTestDateTimeStamp();
         String userCredential = "testPassword";

         createdUser = adminContext.getApi().getUserClient().createUser(orgRef.getHref(), User.builder()
            .name(userIdentity)
            .password(userCredential)
            .description("test user with user-level privileges")
            .role(BaseVCloudDirectorClientLiveTest.getRoleReferenceFor(DefaultRoles.USER.value(), adminContext))
            .deployedVmQuota(BaseVCloudDirectorClientLiveTest.REQUIRED_USER_VM_QUOTA)
            .isEnabled(true)
            .build());

         builder.credentials(userIdentity, userCredential);
         userContext = VCloudDirectorContext.class.cast(builder.build());
      } else {
         userContext = rootContext;
      }
   }

   @Override
   public void close() {
      if (createdUser != null) {
         adminContext.getApi().getUserClient().deleteUser(createdUser.getHref());
      }
      if (userContext != null) userContext.close();
      if (createdAdminUser != null) {
         // TODO: may have to preserve root context if we can't delete the user for it's own context here
         adminContext.getApi().getUserClient().deleteUser(createdAdminUser.getHref());
      }
      if (adminContext != null) adminContext.close();
   }

   public RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> getUserContext() {
      return userContext;
   }

   public RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> getAdminContext() {
      return adminContext;
   }
}
