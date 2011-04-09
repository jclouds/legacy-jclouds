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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

/**
 * A parameter corresponding to a hardware option.
 * 
 * @author Adrian Cole
 */
public class HardwareParameter {
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((method == null) ? 0 : method.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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
      HardwareParameter other = (HardwareParameter) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (method == null) {
         if (other.method != null)
            return false;
      } else if (!method.equals(other.method))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (operation == null) {
         if (other.operation != null)
            return false;
      } else if (!operation.equals(other.operation))
         return false;
      return true;
   }

   private final URI href;
   private final String method;
   private final String name;
   private final String operation;

   public HardwareParameter(URI href, String method, String name, String operation) {
      this.href = checkNotNull(href, "href");
      this.method = checkNotNull(method, "method");
      this.name = checkNotNull(name, "name");
      this.operation = checkNotNull(operation, "operation");
   }

   /**
    * 
    * @return URI of the action this applies to
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return HTTP method of the action this applies to
    */
   public String getMethod() {
      return method;
   }

   /**
    * 
    * @return name of the HTTP request parameter related to this
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return name of the action this applies to
    */
   public String getOperation() {
      return operation;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", method=" + method + ", name=" + name + ", operation=" + operation + "]";
   }
}
