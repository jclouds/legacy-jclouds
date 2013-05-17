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
package org.jclouds.compute.predicates;

import java.util.Set;

import org.jclouds.compute.domain.OperatingSystem;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Container for operating system filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Adrian Cole
 */
public class OperatingSystemPredicates {
   /**
    * evaluates true if the OperatingSystem is unix like
    * 
    */
   public static Predicate<OperatingSystem> isUnix() {
      return new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem os) {
            if (os.getFamily() != null) {
               switch (os.getFamily()) {
                  case WINDOWS:
                     return false;
               }
            }
            for (String toMatch : searchStrings(os))
               if (toMatch != null && toMatch.toLowerCase().indexOf("windows") != -1)
                  return false;
            return true;
         }

         @Override
         public String toString() {
            return "isUnix()";
         }
      };
   }

   /**
    * evaluates true if the OperatingSystem supports the apt installer
    * 
    */
   public static Predicate<OperatingSystem> supportsApt() {
      return new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem os) {
            if (os.getFamily() != null) {
               switch (os.getFamily()) {
                  case DEBIAN:
                  case UBUNTU:
                     return true;
               }
            }
            for (String toMatch : searchStrings(os))
               if (toMatch != null && (toMatch.toLowerCase().indexOf("ubuntu") != -1
                        || toMatch.toLowerCase().indexOf("debian") != -1))
                  return true;
            return false;
         }

         @Override
         public String toString() {
            return "supportsApt()";
         }
      };
   }

   /**
    * evaluates true if the OperatingSystem supports the yum installer
    * 
    */
   public static Predicate<OperatingSystem> supportsYum() {
      return new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem os) {
            if (os.getFamily() != null) {
               switch (os.getFamily()) {
                  case CENTOS:
                  case AMZN_LINUX:
                  case FEDORA:
                  case RHEL:
                     return true;
               }
            }

            for (String toMatch : searchStrings(os))
               if (toMatch.toLowerCase().indexOf("centos") != -1 || toMatch.toLowerCase().indexOf("rhel") != -1
                        || toMatch.toLowerCase().replace(" ", "").indexOf("redhate") != -1
                        || toMatch.toLowerCase().indexOf("fedora") != -1)
                  return true;
            return false;
         }

         @Override
         public String toString() {
            return "supportsYum()";
         }
      };
   }

   /**
    * evaluates true if the OperatingSystem supports the zypper installer
    * 
    */
   public static Predicate<OperatingSystem> supportsZypper() {
      return new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem os) {
            if (os.getFamily() != null) {
               switch (os.getFamily()) {
                  case SUSE:
                     return true;
               }
            }
            for (String toMatch : searchStrings(os))
               if (toMatch != null && toMatch.toLowerCase().indexOf("suse") != -1)
                  return true;
            return false;
         }

         @Override
         public String toString() {
            return "supportsZypper()";
         }
      };
   }

   /**
    * return everything.
    */
   public static Predicate<OperatingSystem> any() {
      return Predicates.<OperatingSystem> alwaysTrue();
   }

   /**
    * return true if this is a 64bit os.
    */
   public static Predicate<OperatingSystem> is64Bit() {
      return new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem os) {
            return os.is64Bit();
         }

         @Override
         public String toString() {
            return "is64Bit()";
         }
      };
   }

   static Iterable<String> searchStrings(OperatingSystem os) {
      Set<String> search = Sets.newLinkedHashSet();
      if (os.getName() != null)
         search.add(os.getName());
      if (os.getDescription() != null)
         search.add(os.getDescription());
      return search;
   }

}
