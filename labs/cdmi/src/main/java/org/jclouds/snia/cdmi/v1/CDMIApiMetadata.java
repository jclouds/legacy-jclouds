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
package org.jclouds.snia.cdmi.v1;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.snia.cdmi.v1.config.CDMIRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for CDMI 1.0.1 API
 * 
 * @author Adrian Cole
 */
public class CDMIApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<CDMIApi, CDMIAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<CDMIApi, CDMIAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CDMIApiMetadata() {
      this(new Builder());
   }

   protected CDMIApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder() {
         super(CDMIApi.class, CDMIAsyncApi.class);
         id("cdmi").name("SNIA CDMI API").identityName("tenantId:user").credentialName("password")
                  .documentation(URI.create("http://www.snia.org/cdmi")).version("1.0.1")
                  .defaultEndpoint("http://localhost:8080").defaultProperties(CDMIApiMetadata.defaultProperties())
                  .defaultModules(ImmutableSet.<Class<? extends Module>> of(CDMIRestClientModule.class));
      }

      @Override
      public CDMIApiMetadata build() {
         return new CDMIApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
