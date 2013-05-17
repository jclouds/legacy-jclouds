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
package org.jclouds.trmk.vcloud_0_8.compute.domain;

import java.net.URI;

/**
 * 
 * @author Adrian Cole
 */
public class OrgAndName {
   protected final URI org;
   protected final String name;

   public OrgAndName(URI org, String name) {
      this.org = org;
      this.name = name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((org == null) ? 0 : org.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      OrgAndName other = (OrgAndName) obj;
      if (org == null) {
         if (other.org != null)
            return false;
      } else if (!org.equals(other.org))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   public URI getOrg() {
      return org;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return "OrgTag [org=" + org + ", name=" + name + "]";
   }

}
