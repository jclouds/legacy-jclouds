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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class CreateInstanceOptions extends BindToJsonPayload {
   Long volumeID;
   Long ip;
   String publicKey = "DEFAULT";
   Map<String, String> configurationData = Maps.newLinkedHashMap();

   @Override
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, Object> postData = Maps.newLinkedHashMap();
      postData.putAll(postParams);
      postData.put("publicKey", publicKey);
      if (volumeID != null)
         postData.put("volumeID", volumeID);
      if (configurationData.size() > 0)
         postData.put("configurationData", configurationData);
      if (ip != null)
         postData.put("ip", ip);
      super.bindToRequest(request, postData);
   }

   @Override
   public void bindToRequest(HttpRequest request, Object toBind) {
      throw new IllegalStateException("CreateInstance is a POST operation");
   }

   /**
    * 
    * @param id
    *           The ID of a storage volume to associate with this instance
    * @param mountPoint
    *           The mount point in which to mount the attached storage volume
    */
   public CreateInstanceOptions mountVolume(long id, String mountPoint) {
      checkArgument(id > 0, "volume id must be a positive number");
      checkNotNull(mountPoint, "mountPoint");
      this.volumeID = id;
      configurationData.put(String.format("oss.storage.id.%d.mnt", id), mountPoint);
      return this;
   }

   /**
    * 
    * @param publicKeyName
    *           The public key to use for accessing the created instancee
    */
   public CreateInstanceOptions authorizePublicKey(String publicKeyName) {
      checkNotNull(publicKeyName, "publicKeyName");
      this.publicKey = publicKeyName;
      return this;
   }

   /**
    * 
    * @param id
    *           The ID of a static IP address to associate with this instance
    */
   public CreateInstanceOptions attachIp(long id) {
      checkArgument(id > 0, "ip id must be a positive number");
      this.ip = id;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateInstanceOptions#mountVolume(long, String  )
       */
      public static CreateInstanceOptions mountVolume(long id, String mountPoint) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.mountVolume(id, mountPoint);
      }

      /**
       * @see CreateInstanceOptions#attachIp(long )
       */
      public static CreateInstanceOptions attachIp(long id) {
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
