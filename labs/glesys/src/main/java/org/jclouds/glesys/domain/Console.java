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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Connection information to connect to a server with VNC.
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_console" />
 */
public class Console {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String host;
      private int port;
      private String protocol;
      private String password;

      public Builder host(String host) {
         this.host = host;
         return this;
      }

      public Builder port(int port) {
         this.port = port;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public Console build() {
         return new Console(host, port, protocol, password);
      }
      
      public Builder fromConsole(Console in) {
         return host(in.getHost()).port(in.getPort()).password(in.getPassword()).protocol(in.getProtocol());
      }

   }

   private final String host;
   private final int port;
   private final String protocol;
   private final String password;

   public Console(String host, int port, String protocol, String password) {
      this.host = host;
      this.port = port;
      this.protocol = protocol;
      this.password = password;
   }

   /**
    * @return the host name to use to connect to the server
    */
   public String getHost() {
      return host;
   }

   /**
    * @return the port to use to connect to the server
    */
   public int getPort() {
      return port;
   }

   /**
    * @return the protocol to use to connect to the server
    */
   public String getProtocol() {
      return protocol;
   }
   
   /**
    * @return the password to use to connect to the server
    */
   public String getPassword() {
      return password;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Console) {
         final Console other = (Console) object;
         return Objects.equal(host, other.host)
               && Objects.equal(port, other.port)
               && Objects.equal(protocol, other.protocol);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(host, port, protocol);
   }

   @Override
   public String toString() {
      return String.format("[host=%s, port=%s, protocol=%s, password=%s]", host, port, protocol, password);
   }

}
