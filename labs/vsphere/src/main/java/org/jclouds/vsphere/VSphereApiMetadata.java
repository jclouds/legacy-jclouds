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
 * 
 * @author Andrea Turli
 */
package org.jclouds.vsphere;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.vsphere.config.properties.VSphereProperties.CLONING;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.vsphere.config.VSphereComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for an example of library integration (VSphereManager)
 * 
 * @author Andrea Turli
 */
public class VSphereApiMetadata extends BaseApiMetadata {

   private static final long serialVersionUID = 7050419752716105398L;

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public VSphereApiMetadata() {
      this(new Builder());
   }

   protected VSphereApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseApiMetadata.defaultProperties();
      
      String cloneStrategy = System.getProperty(CLONING, "linked");
      properties.setProperty(TEMPLATE,
                        "osFamily=UBUNTU,osVersionMatches=12.04,os64Bit=true,osArchMatches=x86,loginUser=toor:password,authenticateSudo=true");      
      return properties;
   }
   
   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder(){
         id("vsphere")
         .version("5")
         .name("vSphere API")
         .identityName("User")
         .credentialName("password")
         .endpointName("ESXi or vCenter url ending in /sdk")
         .defaultEndpoint("https://localhost/sdk")
         .documentation(URI.create("http://pubs.vmware.com/vsphere-50/index.jsp"))
         .defaultProperties(VSphereApiMetadata.defaultProperties())
         .view(ComputeServiceContext.class)
         .defaultModule(VSphereComputeServiceContextModule.class);
      }

      @Override
      public VSphereApiMetadata build() {
         return new VSphereApiMetadata(this);
      }
      
      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }      

   }
}