/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

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
      formParameters.removeAll("volumeID");
      formParameters.put("volumeID", id + "");

      String mountParam = String.format("oss.storage.id.%s.mnt", id);
      formParameters.removeAll(mountParam);
      formParameters.put(mountParam, mountPoint);
      return this;
   }

   /**
    * 
    * @param publicKeyName
    *           The public key to use for accessing the created instancee
    */
   public CreateInstanceOptions authorizePublicKey(String publicKeyName) {
      checkNotNull(publicKeyName, "publicKeyName");
      formParameters.removeAll("publicKey");
      formParameters.put("publicKey", publicKeyName);
      return this;
   }

   /**
    * 
    * @param id
    *           The ID of a static IP address to associate with this instance
    */
   public CreateInstanceOptions attachIp(String id) {
      checkNotNull(id, "ip");
      formParameters.removeAll("ip");
      formParameters.put("ip", id + "");
      return this;
   }

   public static class Builder {

      /**
       * @see CreateInstanceOptions#mountVolume(String, String )
       */
      public static CreateInstanceOptions mountVolume(String id, String mountPoint) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.mountVolume(id, mountPoint);
      }

      /**
       * @see CreateInstanceOptions#attachIp(String )
       */
      public static CreateInstanceOptions attachIp(String id) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.attachIp(id);
      }

      /**
       * @see CreateInstanceOptions#authorizePublicKey(String )
       */
      public static CreateInstanceOptions authorizePublicKey(String publicKeyName) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.authorizePublicKey(publicKeyName);
      }
   }
}
