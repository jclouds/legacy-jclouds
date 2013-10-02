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
package org.jclouds.compute.domain;


/**
 * 
 * @author Adrian Cole
 * 
 */
public class OsFamilyVersion64Bit {
   public OsFamily family;
   public String version;
   public boolean is64Bit;

   // for serialization
   OsFamilyVersion64Bit() {

   }

   public OsFamilyVersion64Bit(OsFamily family, String version, boolean is64Bit) {
      this.family = family;
      this.version = version;
      this.is64Bit = is64Bit;
   }

   @Override
   public String toString() {
      return "[family=" + family + ", version=" + version + ", is64Bit=" + is64Bit + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((family == null) ? 0 : family.hashCode());
      result = prime * result + (is64Bit ? 1231 : 1237);
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      OsFamilyVersion64Bit other = (OsFamilyVersion64Bit) obj;
      if (family != other.family)
         return false;
      if (is64Bit != other.is64Bit)
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }
}
