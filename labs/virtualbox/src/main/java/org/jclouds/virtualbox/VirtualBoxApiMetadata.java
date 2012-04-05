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
package org.jclouds.virtualbox;

import static org.jclouds.compute.config.ComputeServiceProperties.IMAGE_AUTHENTICATE_SUDO;
import static org.jclouds.compute.config.ComputeServiceProperties.IMAGE_LOGIN_USER;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGES_DESCRIPTOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for VirtualBox API
 * 
 * <h3>note</h3>
 * 
 * This class is not setup to allow a subclasses to override the type of api,
 * asyncapi, or context. This is an optimization for simplicity.
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
public class VirtualBoxApiMetadata extends BaseComputeServiceApiMetadata<Supplier, Supplier, ComputeServiceContext<Supplier, Supplier>, VirtualBoxApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public VirtualBoxApiMetadata() {
      this(new Builder());
   }

   protected VirtualBoxApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      
      properties.put(IMAGE_LOGIN_USER, "toor:password");
      properties.put(IMAGE_AUTHENTICATE_SUDO, "true");
      

      properties.put(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE, "<Esc><Esc><Enter> "
               + "/install/vmlinuz noapic preseed/url=PRECONFIGURATION_URL "
               + "debian-installer=en_US auto locale=en_US kbd-chooser/method=us " + "hostname=" + "HOSTNAME "
               + "fb=false debconf/frontend=noninteractive "
               + "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
               + "initrd=/install/initrd.gz -- <Enter>");

      String workingDir = System.getProperty("test.virtualbox.workingDir", VIRTUALBOX_DEFAULT_DIR);

      properties.put(VIRTUALBOX_WORKINGDIR, workingDir);

      String yamlDescriptor = System.getProperty("test.virtualbox.image.descriptor.yaml", VIRTUALBOX_WORKINGDIR
               + File.separator + "images.yaml");

      properties.put(VIRTUALBOX_IMAGES_DESCRIPTOR, yamlDescriptor);

      properties.put(VIRTUALBOX_PRECONFIGURATION_URL, "http://10.0.2.2:23232/preseed.cfg");
      return properties;
   }

   public static class Builder extends BaseComputeServiceApiMetadata.Builder<Supplier, Supplier, ComputeServiceContext<Supplier, Supplier>, VirtualBoxApiMetadata> {

      protected Builder() {
         id("virtualbox")
         .type(ApiType.COMPUTE)
         .name("VirtualBox API")
         .identityName("User")
         .credentialName("Password")
         .documentation(URI.create("https://www.virtualbox.org/sdkref/index.html"))
         .defaultIdentity("administrator")
         .defaultCredential("12345")
         .defaultEndpoint("http://localhost:18083/")
         .documentation(URI.create("https://github.com/jclouds/jclouds/tree/master/apis/byon"))
          // later version not in maven, yet
         .version("4.1.4")
         .buildVersion("4.1.8r75467")
         .defaultProperties(VirtualBoxApiMetadata.defaultProperties())
         .javaApi(Supplier.class, Supplier.class)
         .contextBuilder(TypeToken.of(VirtualBoxContextBuilder.class));
      }

      @Override
      public VirtualBoxApiMetadata build() {
         return new VirtualBoxApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(VirtualBoxApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}