/*
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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

/**
 * 
 * 
 */
public abstract class BaseServer {

   protected BaseServer() {
      // For JAXB and builder use
   }

   private String id;
   private String name;
   private String description;

   protected BaseServer(String id, String name, String description) {
      this.id = id;
      this.name = name;
      this.description = description;
   }

   public String getId() {
      return id;
   }

   public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      BaseServer that = BaseServer.class.cast(o);
      return equal(id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").toString();
   }

}
