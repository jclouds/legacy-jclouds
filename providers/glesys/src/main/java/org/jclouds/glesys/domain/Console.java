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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Connection information to connect to a server with VNC.
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_console" />
 */
public class Console {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromConsole(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String host;
      protected int port;
      protected String protocol;
      protected String password;

      /**
       * @see Console#getHost()
       */
      public T host(String host) {
         this.host = checkNotNull(host, "host");
         return self();
      }

      /**
       * @see Console#getPort()
       */
      public T port(int port) {
         this.port = port;
         return self();
      }

      /**
       * @see Console#getProtocol()
       */
      public T protocol(String protocol) {
         this.protocol = checkNotNull(protocol, "protocol");
         return self();
      }

      /**
       * @see Console#getPassword()
       */
      public T password(String password) {
         this.password = checkNotNull(password, "password");
         return self();
      }

      public Console build() {
         return new Console(host, port, protocol, password);
      }

      public T fromConsole(Console in) {
         return this.host(in.getHost()).port(in.getPort()).protocol(in.getProtocol()).password(in.getPassword());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String host;
   private final int port;
   private final String protocol;
   private final String password;

   @ConstructorProperties({
         "host", "port", "protocol", "password"
   })
   protected Console(String host, int port, String protocol, String password) {
      this.host = checkNotNull(host, "host");
      this.port = port;
      this.protocol = checkNotNull(protocol, "protocol");
      this.password = checkNotNull(password, "password");
   }

   /**
    * @return the host name to use to connect to the server
    */
   public String getHost() {
      return this.host;
   }

   /**
    * @return the port to use to connect to the server
    */
   public int getPort() {
      return this.port;
   }

   /**
    * @return the protocol to use to connect to the server
    */
   public String getProtocol() {
      return this.protocol;
   }

   /**
    * @return the password to use to connect to the server
    */
   public String getPassword() {
      return this.password;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(host, port, protocol);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Console that = Console.class.cast(obj);
      return Objects.equal(this.host, that.host)
            && Objects.equal(this.port, that.port)
            && Objects.equal(this.protocol, that.protocol);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("host", host).add("port", port).add("protocol", protocol)
            .add("password", password);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
