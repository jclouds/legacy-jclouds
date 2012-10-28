package org.jclouds.googlestorage;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.googlestorage.config.GoogleStorageRestClientModule;
import org.jclouds.oauth.v2.config.OAuthAuthenticationModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.s3.S3ApiMetadata;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.s3.blobstore.config.S3BlobStoreContextModule;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERITIFICATE_KEY_PASSWORD;
import static org.jclouds.oauth.v2.OAuthConstants.PKCS_CERTIFICATE_KEY_NAME;
import static org.jclouds.oauth.v2.OAuthConstants.SIGNATURE_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_ASSERTION_DESCRIPTION;
import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_SCOPE;

public class GoogleStorageApiMetadata extends BaseRestApiMetadata {


   public static final TypeToken<RestContext<? extends S3Client, ? extends GoogleStorageAsyncClient>> CONTEXT_TOKEN =
           new TypeToken<RestContext<? extends S3Client, ? extends GoogleStorageAsyncClient>>() {
           };

   @Override
   public Builder toBuilder() {
      return (Builder) new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public GoogleStorageApiMetadata() {
      this(new Builder(S3Client.class, S3AsyncClient.class));
   }

   protected GoogleStorageApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = S3ApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US");
      properties.put("oauth.endpoint", "https://accounts.google.com/o/oauth2/token");
      properties.put(TOKEN_SCOPE, "https://www.googleapis.com/auth/devstorage.full_control");
      properties.put(TOKEN_ASSERTION_DESCRIPTION, "https://accounts.google.com/o/oauth2/token");
      properties.put(PKCS_CERTIFICATE_KEY_NAME, "privatekey");
      properties.put(PKCS_CERITIFICATE_KEY_PASSWORD, "notasecret");
      properties.put(SIGNATURE_ALGORITHM, "RS256");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder(Class<?> syncClient, Class<?> asyncClient) {
         super(syncClient, asyncClient);
         id("googlestorage")
                 .name("Google Cloud Storage (v1) API")
                 .identityName("client_id")
                 .credentialName("client_secret")
                 .defaultEndpoint("http://storage.googleapis.com")
                 .version("v1")
                 .documentation(URI.create("https://developers.google.com/storage/docs/json_api/"))
                 .defaultProperties(GoogleStorageApiMetadata.defaultProperties())
                 .context(CONTEXT_TOKEN)
                 .view(TypeToken.of(S3BlobStoreContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>of(GoogleStorageRestClientModule.class,
                         S3BlobStoreContextModule.class, OAuthAuthenticationModule.class));
      }

      @Override
      public ApiMetadata build() {
         return new GoogleStorageApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}