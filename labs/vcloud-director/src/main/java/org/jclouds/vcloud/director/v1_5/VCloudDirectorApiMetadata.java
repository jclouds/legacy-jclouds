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
package org.jclouds.vcloud.director.v1_5;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.PROPERTY_VCLOUD_DIRECTOR_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.PROPERTY_VCLOUD_DIRECTOR_VERSION_SCHEMA;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.PROPERTY_VCLOUD_DIRECTOR_XML_NAMESPACE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.PROPERTY_VCLOUD_DIRECTOR_XML_SCHEMA;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.vcloud.director.v1_5.config.VCloudDirectorRestClientModule;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for VCloudDirector 1.5 API
 * 
 * @author Adrian Cole
 */
public class VCloudDirectorApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<VCloudDirectorContext> CONTEXT_TOKEN = TypeToken.of(VCloudDirectorContext.class);
   
   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public VCloudDirectorApiMetadata() {
      this(new Builder());
   }

   public VCloudDirectorApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      /** FIXME this should not be the default */
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(30 * 60));

      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_XML_NAMESPACE,
            String.format("http://www.vmware.com/vcloud/v${%s}", PROPERTY_VCLOUD_DIRECTOR_VERSION_SCHEMA));
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(8 * 60));
      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_XML_SCHEMA, "${jclouds.endpoint}/v1.5/schema/master.xsd");
      
      // TODO integrate these with the {@link ComputeTimeouts} instead of having a single timeout for everything.
      properties.setProperty(PROPERTY_SESSION_INTERVAL, Integer.toString(300));
      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_TIMEOUT_TASK_COMPLETED, Long.toString(1200l * 1000l));

      return properties;
   }

   public static class Builder
         extends
       BaseRestApiMetadata.Builder {

      protected Builder() {
         super(VCloudDirectorApi.class, VCloudDirectorAsyncApi.class);
          id("vcloud-director")
         .name("vCloud Director 1.5 API")
         .identityName("User at Organization (user@org)")
         .credentialName("Password")
         .documentation(URI.create("http://www.vmware.com/support/pubs/vcd_pubs.html"))
         .version("1.5")
         .defaultProperties(VCloudDirectorApiMetadata.defaultProperties())
         .context(TypeToken.of(VCloudDirectorContext.class))
         .defaultModule(VCloudDirectorRestClientModule.class);
//         .view(TypeToken.of(ComputeServiceContext.class))
//         .defaultModules(ImmutableSet.<Class<? extends Module>>of(VCloudDirectorRestClientModule.class, VCloudDirectorComputeServiceContextModule.class));
      }

      @Override
      public VCloudDirectorApiMetadata build() {
         return new VCloudDirectorApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
