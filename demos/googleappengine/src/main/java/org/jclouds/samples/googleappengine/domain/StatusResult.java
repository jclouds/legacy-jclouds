/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.samples.googleappengine.domain;

import java.io.Serializable;

/**
 * 
 * @author Adrian Cole
 */
public class StatusResult implements Comparable<StatusResult>, Serializable {
   /** The serialVersionUID */
   private static final long serialVersionUID = -3257496189689220018L;
   private final String service;
   private final String host;
   private final String name;
   private final String status;

   public StatusResult(String service, String host, String name, String status) {
      this.service = service;
      this.host = host;
      this.name = name;
      this.status = status;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((host == null) ? 0 : host.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((service == null) ? 0 : service.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
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
      StatusResult other = (StatusResult) obj;
      if (host == null) {
         if (other.host != null)
            return false;
      } else if (!host.equals(other.host))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (service == null) {
         if (other.service != null)
            return false;
      } else if (!service.equals(other.service))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   public int compareTo(StatusResult o) {
      return (this == o) ? 0 : getService().compareTo(o.getService());
   }

   public String getHost() {
      return host;
   }

   public String getName() {
      return name;
   }

   public String getStatus() {
      return status;
   }

   public String getService() {
      return service;
   }

}
