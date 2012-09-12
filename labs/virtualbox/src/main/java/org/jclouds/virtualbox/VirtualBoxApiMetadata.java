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

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_GUEST_CREDENTIAL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_GUEST_IDENTITY;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGES_DESCRIPTOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.virtualbox.config.HardcodeLocalhostAsNodeMetadataSupplier;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for VirtualBox API

 * @author Adrian Cole
 */
public class VirtualBoxApiMetadata extends BaseApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7039233043408808289L;

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

   public static Properties defaultProperties() {
      Properties properties = BaseApiMetadata.defaultProperties();
      
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
      
      properties.put(VIRTUALBOX_GUEST_IDENTITY, "toor");
      properties.put(VIRTUALBOX_GUEST_CREDENTIAL, "password");
      properties.setProperty(TEMPLATE, 
            String.format("osFamily=UBUNTU,osVersionMatches=12.04.1,os64Bit=true,osArchMatches=amd64,loginUser=%s:%s,authenticateSudo=true", 
                  properties.getProperty(VIRTUALBOX_GUEST_IDENTITY), 
                  properties.getProperty(VIRTUALBOX_GUEST_CREDENTIAL)));
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder() {
         id("virtualbox")
         .name("VirtualBox API")
         .identityName("User")
         .credentialName("Password")
         .documentation(URI.create("https://www.virtualbox.org/sdkref/index.html"))
         .defaultIdentity(System.getProperty("user.name"))
         .defaultCredential("CHANGE_ME")
         .defaultEndpoint("http://localhost:18083/")
         .documentation(URI.create("https://github.com/jclouds/jclouds/tree/master/apis/byon"))
          // later version not in maven, yet
         .version("4.1.4")
         .buildVersion("4.1.8r75467")
         .defaultProperties(VirtualBoxApiMetadata.defaultProperties())
         .view(ComputeServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(HardcodeLocalhostAsNodeMetadataSupplier.class, VirtualBoxComputeServiceContextModule.class));
      }

      @Override
      public VirtualBoxApiMetadata build() {
         return new VirtualBoxApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}