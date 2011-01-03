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

package org.jclouds.cloudsigma.domain;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class VNC {
   @Nullable
   private final String ip;
   @Nullable
   private final String password;
   private final boolean tls;

   public VNC(String ip, String password, boolean tls) {
      this.ip = ip;
      this.password = password;
      this.tls = tls;
   }

   /**
    * 
    * @return IP address for overlay VNC access on port 5900. Set to 'auto', to reuse nic:0:dhcp if
    *         available, or otherwise allocate a temporary IP at boot.
    */
   public String getIp() {
      return ip;
   }

   /**
    * 
    * @return Password for VNC access. If unset, VNC is disabled.
    */
   public String getPassword() {
      return password;
   }

   /**
    * 
    * @return Set to 'on' to require VeNCrypt-style TLS auth in addition to the password. If this is
    *         unset, only unencrypted VNC is available.
    */
   public boolean isTls() {
      return tls;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + (tls ? 1231 : 1237);
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
      VNC other = (VNC) obj;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (tls != other.tls)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[ip=" + ip + ", password=" + password + ", tls=" + tls + "]";
   }
}