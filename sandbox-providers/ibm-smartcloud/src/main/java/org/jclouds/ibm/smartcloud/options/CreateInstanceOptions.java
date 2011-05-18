/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibm.smartcloud.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class CreateInstanceOptions extends BaseHttpRequestOptions {

   public CreateInstanceOptions() {
      super();
      formParameters.put("publicKey", "DEFAULT");
   }

   /**
    * 
    * @param id
    *           The ID of a storage volume to associate with this instance
    * @param mountPoint
    *           The mount point in which to mount the attached storage volume
    */
   public CreateInstanceOptions mountVolume(String id, String mountPoint) {
      checkNotNull(id, "volume id");
      checkNotNull(mountPoint, "mountPoint");
      formParameters.replaceValues("volumeID", ImmutableSet.of(id));
      String mountParam = String.format("oss.storage.id.0.mnt", id);
      formParameters.replaceValues(mountParam, ImmutableSet.of(mountPoint));
      return this;
   }

   /**
    * 
    * @param publicKeyName
    *           The public key to use for accessing the created instancee
    */
   public CreateInstanceOptions authorizePublicKey(String publicKeyName) {
      checkNotNull(publicKeyName, "publicKeyName");
      formParameters.replaceValues("publicKey", ImmutableSet.of(publicKeyName));
      return this;
   }

   /**
    * 
    * @param configurationData
    *           extra configuration to pass to the instance
    */
   public CreateInstanceOptions configurationData(Map<String, String> configurationData) {
      checkNotNull(configurationData, "configurationData");
      for (Entry<String, String> entry : configurationData.entrySet()) {
         formParameters.replaceValues(entry.getKey(), ImmutableSet.of(entry.getValue()));
      }
      return this;
   }

   /**
    * 
    * @param ip
    *           The ID of a static IP address to associate with this instance
    */
   public CreateInstanceOptions staticIP(String ip) {
      checkNotNull(ip, "ip");
      formParameters.replaceValues("ip", ImmutableSet.of(ip));
      return this;
   }

   /**
    * 
    * @param id
    *           The ID of a Vlan offering to associate with this instance.
    */
   public CreateInstanceOptions vlanID(String id) {
      checkNotNull(id, "id");
      formParameters.replaceValues("vlanID", ImmutableSet.of(id));
      return this;
   }

   /**
    * 
    * @param ip
    *           The ID of a static IP address to associate with this instance as secondary IP.
    */
   public CreateInstanceOptions secondaryIP(String ip) {
      checkNotNull(ip, "ip");
      formParameters.replaceValues("secondaryIP", ImmutableSet.of(ip));
      return this;
   }

   /**
    * 
    * @param id
    *           The ID of an existing anti-collocated instance.
    */
   public CreateInstanceOptions antiCollocationInstance(String id) {
      checkNotNull(id, "id");
      formParameters.replaceValues("antiCollocationInstance", ImmutableSet.of(id));
      return this;
   }

   /**
    * Whether or not the instance should be provisioned with the root segment only. Compared to
    * standard server size, this option will provide less storage but same memory and CPU, and thus
    * provision the instance faster.
    * 
    * @param isMiniEphemeral
    *           true if root only provision, False: normal provision
    */
   public CreateInstanceOptions isMiniEphemeral(boolean isMiniEphemeral) {
      formParameters.replaceValues("isMiniEphemeral", ImmutableSet.of(isMiniEphemeral + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateInstanceOptions#configurationData
       */
      public static CreateInstanceOptions configurationData(Map<String, String> configurationData) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.configurationData(configurationData);
      }

      /**
       * @see CreateInstanceOptions#mountVolume
       */
      public static CreateInstanceOptions mountVolume(String id, String mountPoint) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.mountVolume(id, mountPoint);
      }

      /**
       * @see CreateInstanceOptions#staticIP
       */
      public static CreateInstanceOptions staticIP(String ip) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.staticIP(ip);
      }

      /**
       * @see CreateInstanceOptions#secondaryIP
       */
      public static CreateInstanceOptions secondaryIP(String ip) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.secondaryIP(ip);
      }

      /**
       * @see CreateInstanceOptions#vlanID
       */
      public static CreateInstanceOptions vlanID(String id) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.vlanID(id);
      }

      /**
       * @see CreateInstanceOptions#antiCollocationInstance
       */
      public static CreateInstanceOptions antiCollocationInstance(String id) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.antiCollocationInstance(id);
      }

      /**
       * @see CreateInstanceOptions#isMiniEphemeral
       */
      public static CreateInstanceOptions isMiniEphemeral(boolean isMiniEphemeral) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.isMiniEphemeral(isMiniEphemeral);
      }

      /**
       * @see CreateInstanceOptions#authorizePublicKey
       */
      public static CreateInstanceOptions authorizePublicKey(String publicKeyName) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.authorizePublicKey(publicKeyName);
      }
   }

}
