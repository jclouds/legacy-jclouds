/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3;

import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.s3.blobstore.config.S3BlobStoreContextModule;
import org.jclouds.s3.config.S3RestClientModule;
import org.jclouds.s3.reference.S3Headers;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Amazon's S3 api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}
 * (asyncClient), so that children can add additional methods not declared here,
 * such as new features from AWS.
 * <p/>
 * 
 * As this is a popular api, we also allow overrides for type {@code C}
 * (context). This allows subtypes to add in new feature groups or extensions,
 * not present in the base api. For example, you could make a subtype for
 * context, that exposes admin operations.
 * 
 * @author Adrian Cole
 */
public class S3ApiMetadata extends BaseRestApiMetadata {
   
   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildClient(S3Client.class)} as
    *             {@link S3AsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<? extends S3Client, ? extends S3AsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<? extends S3Client, ? extends S3AsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public S3ApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected S3ApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, S3Headers.DEFAULT_AMAZON_HEADERTAG);
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "true");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      properties.setProperty(PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX, "/");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, String.format("x-${%s}-meta-", PROPERTY_HEADER_TAG));
      return properties;
   }
   
   public abstract static class Builder<T extends Builder<T>> extends BaseRestApiMetadata.Builder<T> {
      @SuppressWarnings("deprecation")
      protected Builder() {
         this(S3Client.class, S3AsyncClient.class);
      }

      protected Builder(Class<?> syncClient, Class<?> asyncClient){
         super(syncClient, asyncClient);
         id("s3")
         .name("Amazon Simple Storage Service (S3) API")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .defaultEndpoint("https://s3.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/AmazonS3/latest/API"))
         .version("2006-03-01")
         .defaultProperties(S3ApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .view(typeToken(S3BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(S3RestClientModule.class, S3BlobStoreContextModule.class));
      }

      @Override
      public ApiMetadata build() {
         return new S3ApiMetadata(this);
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
