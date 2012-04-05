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
package org.jclouds.byon;

import java.net.URI;
import java.util.Properties;

import org.jclouds.JcloudsVersion;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for jclouds BYON API
 * 
 * <h3>note</h3>
 * 
 * This class is not setup to allow a subclasses to override the type of api,
 * asyncapi, or context. This is an optimization for s.
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
public class BYONApiMetadata extends BaseComputeServiceApiMetadata<Supplier, Supplier, ComputeServiceContext<Supplier, Supplier>, BYONApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public BYONApiMetadata() {
      this(new Builder());
   }

   protected BYONApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      return BaseComputeServiceApiMetadata.Builder.defaultProperties();
   }

   public static class Builder extends BaseComputeServiceApiMetadata.Builder<Supplier, Supplier, ComputeServiceContext<Supplier, Supplier>, BYONApiMetadata> {

      protected Builder() {
         id("byon")
         .name("Bring Your Own Node (BYON) API")
         .identityName("Unused")
         .defaultIdentity("foo")
         .defaultCredential("bar")
         .defaultEndpoint("file://byon.yaml")
         .documentation(URI.create("https://github.com/jclouds/jclouds/tree/master/apis/byon"))
         .version(String.format("%s.%s", JcloudsVersion.get().majorVersion, JcloudsVersion.get().minorVersion))
         .buildVersion(JcloudsVersion.get().toString())
         .defaultProperties(BYONApiMetadata.defaultProperties())
         .javaApi(Supplier.class, Supplier.class)
         .contextBuilder(TypeToken.of(BYONComputeServiceContextBuilder.class));
      }

      @Override
      public BYONApiMetadata build() {
         return new BYONApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(BYONApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}