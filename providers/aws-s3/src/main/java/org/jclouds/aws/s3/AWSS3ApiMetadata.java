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
package org.jclouds.aws.s3;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStoreContext;
import org.jclouds.aws.s3.blobstore.config.AWSS3BlobStoreContextModule;
import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.s3.S3ApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Amazon-specific S3 API
 * 
 * @author Adrian Cole
 */
public class AWSS3ApiMetadata extends S3ApiMetadata {
   
   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(AWSS3Client.class)} as
    *             {@link AWSS3AsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<AWSS3Client, AWSS3AsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<AWSS3Client, AWSS3AsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public AWSS3ApiMetadata() {
      this(new Builder());
   }

   protected AWSS3ApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = S3ApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends S3ApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(AWSS3Client.class, AWSS3AsyncClient.class);
         id("aws-s3")
         .name("Amazon-specific S3 API")
         .defaultProperties(AWSS3ApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .view(typeToken(AWSS3BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AWSS3RestClientModule.class, AWSS3BlobStoreContextModule.class));
      }
      
      @Override
      public AWSS3ApiMetadata build() {
         return new AWSS3ApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
