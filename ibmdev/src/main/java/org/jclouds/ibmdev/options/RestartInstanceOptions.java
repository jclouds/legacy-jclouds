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
public class RestartInstanceOptions extends BindToJsonPayload {
   String keyName;

   @Override
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, Object> postData = Maps.newLinkedHashMap();
      postData.putAll(postParams);
      postData.put("state", "restart");
      if (keyName != null)
         postData.put("keyName", keyName);
      super.bindToRequest(request, postData);
   }

   @Override
   public void bindToRequest(HttpRequest request, Object toBind) {
      throw new IllegalStateException("RestartInstance is a PUT operation");
   }

   /**
    * 
    * @param keyName
    *           The name of the SSH Public Key to add to the instance during restart.
    */
   public RestartInstanceOptions authorizePublicKey(String keyName) {
      checkNotNull(keyName, "keyName");
      this.keyName = keyName;
      return this;
   }

   public static class Builder {

      /**
       * @see RestartInstanceOptions#authorizePublicKey(String )
       */
      public static RestartInstanceOptions authorizePublicKey(String keyName) {
         RestartInstanceOptions options = new RestartInstanceOptions();
         return options.authorizePublicKey(keyName);
      }
   }
}
