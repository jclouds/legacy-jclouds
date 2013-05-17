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
package org.jclouds.dynect.v3;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.dynect.v3.config.DynECTParserModule;
import org.jclouds.dynect.v3.config.DynECTHttpApiModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for DynECT 1.0 API
 * 
 * @author Adrian Cole
 */
public class DynECTApiMetadata extends BaseHttpApiMetadata<DynECTApi> {
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DynECTApiMetadata() {
      this(new Builder());
   }

   protected DynECTApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<DynECTApi, Builder> {

      protected Builder() {
          id("dynect")
         .name("DynECT API2")
         .identityName("${customer}:${userName}")
         .credentialName("${password}")
         .documentation(URI.create("https://manage.dynect.net/help/docs/api2/rest/"))
         .version("3.3.8")
         .defaultEndpoint("https://api2.dynect.net/REST")
         .defaultProperties(DynECTApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(DynECTParserModule.class)
                                     .add(DynECTHttpApiModule.class).build());
      }
      
      @Override
      public DynECTApiMetadata build() {
         return new DynECTApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
