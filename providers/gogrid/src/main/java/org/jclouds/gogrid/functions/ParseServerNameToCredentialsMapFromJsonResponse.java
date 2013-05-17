/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

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

   // incidental view class to assist in getting the correct data
   // deserialized from json
   private static class Password implements Comparable<Password> {
      @Named("username")
      private final String userName;
      private final String password;
      private final Server server;

      @ConstructorProperties({"username", "password", "server"})
      public Password(String userName, String password, @Nullable Server server) {
         this.userName = checkNotNull(userName, "username");
         this.password = checkNotNull(password, "password");
         this.server = server;
      }

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

         Password other = (Password) o;
         return Objects.equal(userName, other.userName)
               && Objects.equal(password, other.password)
               && Objects.equal(server, other.server);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(userName, password, server);
      }

      @Override
      public int compareTo(Password o) {
         if (null == o.getServer()) return null == server ? 0 : -1;
         if (server == null) return 1;
         return server.getName().compareTo(o.getServer().getName());
      }
   }

   @Override
   public Map<String, Credentials> apply(HttpResponse arg0) {
      Map<String, Credentials> serverNameToCredentials = Maps.newHashMap();
      for (Password password : json.apply(arg0).getList()) {
         if (null != password.getServer())
            serverNameToCredentials.put(password.getServer().getName(),
                  new Credentials(password.getUserName(), password.getPassword()));
      }
      return serverNameToCredentials;
   }

}
