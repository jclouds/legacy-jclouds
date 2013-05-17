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
package org.jclouds.trmk.vcloud_0_8.internal;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Terremark's VCloud api.

 * @author Adrian Cole
 */
public abstract class TerremarkVCloudApiMetadata extends BaseRestApiMetadata {

   protected TerremarkVCloudApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "0.8");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA, "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, "allowInOut");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NS, String.format("urn:tmrk:${%s}-${%s}",
            PROPERTY_TERREMARK_EXTENSION_NAME, PROPERTY_TERREMARK_EXTENSION_VERSION));
      properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE,
            String.format("http://www.vmware.com/vcloud/v${%s}", PROPERTY_VCLOUD_VERSION_SCHEMA));
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "15");
      // terremark can sometimes block extremely long times
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, MINUTES.toMillis(20) + "");
      return properties;
   }

   public abstract static class Builder<B extends Builder<B>> extends BaseRestApiMetadata.Builder<B> {

      protected Builder(Class<?> syncClient, Class<?> asyncClient) {
         super(syncClient, asyncClient);
         identityName("Email")
         .credentialName("Password")
         .version("0.8")
         .defaultProperties(TerremarkVCloudApiMetadata.defaultProperties())
         .view(ComputeServiceContext.class);
      }
   }
}
