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
package org.jclouds.rimuhosting.miro;

import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.concurrent.Timeout;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for RimuHosting API
 * 
 * @author Adrian Cole
 */
public class RimuHostingApiMetadata
      extends
      BaseComputeServiceApiMetadata<RimuHostingClient, RimuHostingAsyncClient, ComputeServiceContext<RimuHostingClient, RimuHostingAsyncClient>, RimuHostingApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public RimuHostingApiMetadata() {
      this(new Builder());
   }

   protected RimuHostingApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      Timeout timeout = RimuHostingClient.class.getAnnotation(Timeout.class);
      long timeoutMillis = timeout.timeUnit().toMillis(timeout.duration());
      properties.setProperty(PROPERTY_SO_TIMEOUT, timeoutMillis + "");
      properties.setProperty(PROPERTY_CONNECTION_TIMEOUT, timeoutMillis + "");
      properties.setProperty(ComputeServiceProperties.TIMEOUT_NODE_TERMINATED, 60 * 1000 + "");
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<RimuHostingClient, RimuHostingAsyncClient, ComputeServiceContext<RimuHostingClient, RimuHostingAsyncClient>, RimuHostingApiMetadata> {

      protected Builder() {
         id("rimuhosting")
         .type(ApiType.COMPUTE)
         .name("RimuHosting API")
         .identityName("API Key")
         .documentation(URI.create("http://apidocs.rimuhosting.com"))
         .version("1")
         .defaultEndpoint("https://api.rimuhosting.com/r")
         .javaApi(RimuHostingClient.class, RimuHostingAsyncClient.class)
         .contextBuilder(TypeToken.of(RimuHostingContextBuilder.class));
      }

      @Override
      public RimuHostingApiMetadata build() {
         return new RimuHostingApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(RimuHostingApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
