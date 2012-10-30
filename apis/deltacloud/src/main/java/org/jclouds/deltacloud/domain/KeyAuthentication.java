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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

/**
 * 
 * @author Adrian Cole
 */
public class KeyAuthentication implements Instance.Authentication, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7669076186483470376L;
   private final String keyName;

   public KeyAuthentication(String keyName) {
      this.keyName = checkNotNull(keyName, "keyName");
   }

   public String getKeyName() {
      return keyName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
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
      KeyAuthentication other = (KeyAuthentication) obj;
      if (keyName == null) {
         if (other.keyName != null)
            return false;
      } else if (!keyName.equals(other.keyName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[keyName=%s]", keyName);
   }

}
