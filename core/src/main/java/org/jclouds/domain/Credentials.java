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

   public final String account;
   public final String key;

   public Credentials(String account, String key) {
      this.account = account;
      this.key = key;
   }

   public static Credentials parse(URI uri) {
      checkNotNull(uri, "uri");
      List<String> userInfo = Lists.newArrayList(Splitter.on(':').split(
               checkNotNull(uri.getUserInfo(), "no userInfo in " + uri)));
      String account = checkNotNull(userInfo.get(0), "no username in " + uri.getUserInfo());
      if (HttpUtils.isUrlEncoded(account)) {
         account = HttpUtils.urlDecode(account);
      }
      String key = userInfo.size() > 1 ? userInfo.get(1) : null;
      if (key != null && HttpUtils.isUrlEncoded(key)) {
         key = HttpUtils.urlDecode(key);
      }
      return new Credentials(account, key);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
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
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;

      return true;
   }

}