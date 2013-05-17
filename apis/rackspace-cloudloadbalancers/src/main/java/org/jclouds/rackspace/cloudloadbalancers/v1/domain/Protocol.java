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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * @author Everett Toews
 */
public final class Protocol {
   private final String name;
   private final int port;

   @ConstructorProperties({ "name", "port" })
   protected Protocol(String name, int port) {
      this.name = checkNotNull(name, "name");
      this.port = checkNotNull(port, "port");
   }

   public String getName() {
      return name;
   }

   public int getPort() {
      return port;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, port);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Protocol that = Protocol.class.cast(obj);

      return Objects.equal(this.name, that.name) && Objects.equal(this.port, that.port);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("port", port).toString();
   }
}
