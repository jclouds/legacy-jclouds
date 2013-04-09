package org.jclouds.openstack.keystone.v2_0.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Properties;

import javax.inject.Qualifier;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ProviderModule;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests configuration via {@link ProviderModule}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ProviderModuleExpectTest")
public class ProviderModuleExpectTest extends BaseRestApiExpectTest<ProviderModuleExpectTest.DNSApi> {

   @Retention(RUNTIME)
   @Target(TYPE)
   @Qualifier
   static @interface DNS {
   }

   @ConfiguresRestClient
   public static class DNSRestClientModule extends RestClientModule<DNSApi, DNSAsyncApi> {
      @Override
      public void configure() {
         bind(new TypeLiteral<Supplier<URI>>() {}).annotatedWith(DNS.class).to(new TypeLiteral<Supplier<URI>>() {});
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }
   }

   static interface DNSApi extends Closeable {
      boolean zoneExists(@PathParam("zoneName") String zoneName);
   }

   @org.jclouds.rest.annotations.Endpoint(DNS.class)
   static interface DNSAsyncApi extends Closeable {
      @HEAD
      @Path("/zones/{zoneName}")
      @Fallback(FalseOnNotFoundOr404.class)
      public ListenableFuture<Boolean> zoneExists(@PathParam("zoneName") String zoneName);
   }

   public void testDNSEndpointApplied() {
      KeystoneAuth keystoneAuth = new KeystoneAuth();

      DNSApi api = requestsSendResponses(
            keystoneAuth.getKeystoneAuthWithUsernameAndPassword(),
            keystoneAuth.getResponseWithKeystoneAccess(),
            HttpRequest.builder().method("HEAD").endpoint("http://172.16.0.1:8776/v1/3456/zones/foo.com").build(),
            HttpResponse.builder().statusCode(200).build());
      assertTrue(api.zoneExists("foo.com"));
   }

   private static class DNSApiMetadata extends BaseRestApiMetadata {

      @Override
      public Builder toBuilder() {
         return new Builder().fromApiMetadata(this);
      }

      public DNSApiMetadata() {
         this(new Builder());
      }

      protected DNSApiMetadata(Builder builder) {
         super(builder);
      }

      public static Properties defaultProperties() {
         Properties properties = BaseRestApiMetadata.defaultProperties();
         properties.setProperty(SERVICE_TYPE, "dns");
         properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
         return properties;
      }

      public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

         protected Builder() {
            super(DNSApi.class, DNSAsyncApi.class);
            id("dns")
                  .name("DNS API")
                  .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
                  .credentialName("${password}")
                  .endpointName("Keystone base url ending in /v2.0/")
                  .documentation(URI.create("http://dns"))
                  .version("1.0")
                  .defaultEndpoint("http://localhost:5000/v2.0/")
                  .defaultProperties(DNSApiMetadata.defaultProperties())
                  .defaultModules(
                        ImmutableSet.<Class<? extends Module>> builder()
                           .add(KeystoneAuthenticationModule.class)
                           .add(ProviderModule.class)
                           .add(DNSRestClientModule.class)
                           .build());
         }

         @Override
         public DNSApiMetadata build() {
            return new DNSApiMetadata(this);
         }

         @Override
         protected Builder self() {
            return this;
         }
      }
   }

   @Override
   public ApiMetadata createApiMetadata() {
      return new DNSApiMetadata();
   }
   
   public static class KeystoneAuth extends BaseKeystoneRestApiExpectTest<KeystoneApi> {
      public HttpRequest getKeystoneAuthWithUsernameAndPassword() {
         return keystoneAuthWithUsernameAndPassword;
      }
      
      public HttpResponse getResponseWithKeystoneAccess() {
         return responseWithKeystoneAccess;
      }
   }
}