/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.http.HttpUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class Credentials {

   public final String identity;
   public final String credential;

   public Credentials(String identity, String credential) {
      this.identity = identity;
      this.credential = credential;
   }

   public static Credentials parse(URI uri) {
      checkNotNull(uri, "uri");
      List<String> userInfo = Lists.newArrayList(Splitter.on(':').split(
               checkNotNull(uri.getUserInfo(), "no userInfo in " + uri)));
      String identity = checkNotNull(userInfo.get(0), "no username in " + uri.getUserInfo());
      if (HttpUtils.isUrlEncoded(identity)) {
         identity = HttpUtils.urlDecode(identity);
      }
      String credential = userInfo.size() > 1 ? userInfo.get(1) : null;
      if (credential != null && HttpUtils.isUrlEncoded(credential)) {
         credential = HttpUtils.urlDecode(credential);
      }
      return new Credentials(identity, credential);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((identity == null) ? 0 : identity.hashCode());
      result = prime * result + ((credential == null) ? 0 : credential.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Credentials other = (Credentials) obj;
      if (identity == null) {
         if (other.identity != null)
            return false;
      } else if (!identity.equals(other.identity))
         return false;
      if (credential == null) {
         if (other.credential != null)
            return false;
      } else if (!credential.equals(other.credential))
         return false;

      return true;
   }

}