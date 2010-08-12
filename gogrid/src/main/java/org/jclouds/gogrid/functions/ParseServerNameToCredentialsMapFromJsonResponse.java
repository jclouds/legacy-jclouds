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

package org.jclouds.gogrid.functions;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class ParseServerNameToCredentialsMapFromJsonResponse implements
      Function<HttpResponse, Map<String, Credentials>> {
   private final ParseJson<GenericResponseContainer<Password>> json;

   @Inject
   ParseServerNameToCredentialsMapFromJsonResponse(
         ParseJson<GenericResponseContainer<Password>> json) {
      this.json = json;
   }

   // incidental wrapper class to assist in getting the correct data
   // deserialized from json
   private static class Password implements Comparable<Password> {
      @SerializedName("username")
      private String userName;
      private String password;
      private Server server;

      public String getUserName() {
         return userName;
      }

      public String getPassword() {
         return password;
      }

      public Server getServer() {
         return server;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;

         Password password1 = (Password) o;

         if (password != null ? !password.equals(password1.password)
               : password1.password != null)
            return false;
         if (server != null ? !server.equals(password1.server)
               : password1.server != null)
            return false;
         if (userName != null ? !userName.equals(password1.userName)
               : password1.userName != null)
            return false;

         return true;
      }

      @Override
      public int hashCode() {
         int result = userName != null ? userName.hashCode() : 0;
         result = 31 * result + (password != null ? password.hashCode() : 0);
         result = 31 * result + (server != null ? server.hashCode() : 0);
         return result;
      }

      @Override
      public int compareTo(Password o) {
         return server.getName().compareTo(o.getServer().getName());
      }
   }

   @Override
   public Map<String, Credentials> apply(HttpResponse arg0) {
      Map<String, Credentials> serverNameToCredentials = Maps.newHashMap();
      for (Password password : json.apply(arg0).getList()) {
         serverNameToCredentials.put(password.getServer().getName(),
               new Credentials(password.getUserName(), password.getPassword()));
      }
      return serverNameToCredentials;
   }

}
