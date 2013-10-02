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

import static com.google.common.base.Preconditions.checkNotNull;

import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * Running Operating system
 * 
 * 
 * @author Adrian Cole
 */
public enum OsFamily {
   UNRECOGNIZED, AIX, ARCH, CENTOS, DARWIN, DEBIAN, ESX, FEDORA, FREEBSD, GENTOO, HPUX, LINUX, 
   /**
    * @see <a href="http://smartos.org">SmartOS</a>
    */
   SMARTOS,
   /**
    * @see <a href="http://aws.amazon.com/amazon-linux-ami/">amazon linux ami</a>
    */
   AMZN_LINUX, MANDRIVA, NETBSD,
   /**
    * 
    * Oracle Enterprise Linux
    */
   OEL, OPENBSD, RHEL,
   /**
    * Scientific Linux
    */
   SCIENTIFIC,
   /**
    * Google Compute Engine Linux
    */
   GCEL, SIGAR, SLACKWARE, SOLARIS, SUSE, TURBOLINUX, CLOUD_LINUX, UBUNTU, WINDOWS;

   public String value() {
      return UPPER_UNDERSCORE.to(LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static OsFamily fromValue(String osFamily) {
      try {
         return valueOf(LOWER_HYPHEN.to(UPPER_UNDERSCORE, checkNotNull(osFamily, "osFamily")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
