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
package org.jclouds.cim;

import org.jclouds.compute.domain.OsFamily;

/**
 * Operating system based on DMTF CIM model.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://dmtf.org/sites/default/files/cim/cim_schema_v2280/cim_schema_2.28.0Final-Doc.zip"
 *      />
 */
public enum OSType {
   /**
    * Other
    */
   OTHER(1, "Other", OsFamily.UNRECOGNIZED, false),
   /**
    * MACOS
    */
   MACOS(2, "MACOS", OsFamily.DARWIN, false),
   /**
    * Solaris
    */
   SOLARIS(29, "Solaris", OsFamily.SOLARIS, false),
   /**
    * LINUX
    */
   LINUX(36, "LINUX", OsFamily.LINUX, false),
   /**
    * FreeBSD
    */
   FREEBSD(42, "FreeBSD", OsFamily.FREEBSD, false),
   /**
    * NetBSD
    */
   NETBSD(43, "NetBSD", OsFamily.NETBSD, false),
   /**
    * OpenBSD
    */
   OPENBSD(65, "OpenBSD", OsFamily.OPENBSD, false),
   /**
    * Not Applicable
    */
   NOT_APPLICABLE(66, "Not Applicable", OsFamily.UNRECOGNIZED, false),
   /**
    * Microsoft Windows Server 2003
    */
   WINDOWS_SERVER_2003(69, "Microsoft Windows Server 2003", OsFamily.WINDOWS, false),
   /**
    * Microsoft Windows Server 2003 64-Bit
    */
   WINDOWS_SERVER_2003_64(70, "Microsoft Windows Server 2003 64-Bit", OsFamily.WINDOWS, true),
   /**
    * Microsoft Windows Server 2008
    */
   WINDOWS_SERVER_2008(76, "Microsoft Windows Server 2008", OsFamily.WINDOWS, false),
   /**
    * Microsoft Windows Server 2008 64-Bit
    */
   WINDOWS_SERVER_2008_64(77, "Microsoft Windows Server 2008 64-Bit", OsFamily.WINDOWS, true),
   /**
    * FreeBSD 64-Bit
    */
   FREEBSD_64(78, "FreeBSD 64-Bit", OsFamily.FREEBSD, true),
   /**
    * RedHat Enterprise Linux
    */
   RHEL(79, "RedHat Enterprise Linux", OsFamily.RHEL, false),
   /**
    * RedHat Enterprise Linux 64-Bit
    */
   RHEL_64(80, "RedHat Enterprise Linux 64-Bit", OsFamily.RHEL, true),
   /**
    * Solaris 64-Bit
    */
   SOLARIS_64(81, "Solaris 64-Bit", OsFamily.SOLARIS, true),
   /**
    * SUSE
    */
   SUSE(82, "SUSE", OsFamily.SUSE, false),
   /**
    * SUSE 64-Bit
    */
   SUSE_64(83, "SUSE 64-Bit", OsFamily.SUSE, true),
   /**
    * SLES
    */
   SLES(84, "SLES", OsFamily.SUSE, false),
   /**
    * SLES 64-Bit
    */
   SLES_64(85, "SLES 64-Bit", OsFamily.SUSE, true),
   /**
    * Novell OES
    */
   NOVELL_OES(86, "Novell OES", OsFamily.SUSE, true),
   /**
    * Mandriva
    */
   MANDRIVA(89, "Mandriva", OsFamily.MANDRIVA, false),
   /**
    * Mandriva 64-Bit
    */
   MANDRIVA_64(90, "Mandriva 64-Bit", OsFamily.MANDRIVA, true),
   /**
    * TurboLinux
    */
   TURBOLINUX(91, "TurboLinux", OsFamily.TURBOLINUX, false),
   /**
    * TurboLinux 64-Bit
    */
   TURBOLINUX_64(92, "TurboLinux 64-Bit", OsFamily.TURBOLINUX, true),
   /**
    * Ubuntu
    */
   UBUNTU(93, "Ubuntu", OsFamily.UBUNTU, false),
   /**
    * Ubuntu 64-Bit
    */
   UBUNTU_64(94, "Ubuntu 64-Bit", OsFamily.UBUNTU, true),
   /**
    * Debian
    */
   DEBIAN(95, "Debian", OsFamily.DEBIAN, false),
   /**
    * Debian 64-Bit
    */
   DEBIAN_64(96, "Debian 64-Bit", OsFamily.DEBIAN, false),
   /**
    * Linux 2.4.x
    */
   LINUX_2_4(97, "Linux 2.4.x", OsFamily.LINUX, false),
   /**
    * Linux 2.4.x 64-Bi
    */
   LINUX_2_4_64(98, "Linux 2.4.x 64-Bit", OsFamily.LINUX, true),
   /**
    * Linux 2.6.x
    */
   LINUX_2_6(99, "Linux 2.6.x", OsFamily.LINUX, false),
   /**
    * Linux 2.6.x 64-Bit
    */
   LINUX_2_6_64(100, "Linux 2.6.x 64-Bit", OsFamily.LINUX, true),
   /**
    * Linux 64-Bit
    */
   LINUX_64(101, "Linux 64-Bit", OsFamily.LINUX, true),
   /**
    * Other 64-Bit
    */
   OTHER_64(102, "Other 64-Bit", OsFamily.UNRECOGNIZED, true),
   /**
    * Microsoft Windows Server 2008 R2
    */
   WINDOWS_SERVER_2008_R2(103, "Microsoft Windows Server 2008 R2", OsFamily.WINDOWS, true),
   /**
    * VMware ESXi
    */
   ESXI(104, "VMware ESXi", OsFamily.ESX, true),
   /**
    * Microsoft Windows 7
    */
   WINDOWS_7(105, "Microsoft Windows 7", OsFamily.WINDOWS, false),
   /**
    * CentOS 32-bit
    */
   CENTOS(106, "CentOS 32-bit", OsFamily.CENTOS, false),
   /**
    * CentOS 64-bit
    */
   CENTOS_64(107, "CentOS 64-bit", OsFamily.CENTOS, true),
   /**
    * Oracle Enterprise Linux 32-bit
    */
   ORACLE_ENTERPRISE_LINUX(108, "Oracle Enterprise Linux 32-bit", OsFamily.OEL, false),
   /**
    * Oracle Enterprise Linux 64-bit
    */
   ORACLE_ENTERPRISE_LINUX_64(109, "Oracle Enterprise Linux 64-bit", OsFamily.OEL, true),
   /**
    * eComStation 32-bitx
    */
   ECOMSTATION_32(109, "eComStation 32-bitx", OsFamily.UNRECOGNIZED, false), UNRECOGNIZED(Integer.MAX_VALUE,
            "UNRECOGNIZED", null, true);
   private final int code;

   public int getCode() {
      return code;
   }

   public String getValue() {
      return value;
   }

   public OsFamily getFamily() {
      return family;
   }

   public boolean is64Bit() {
      return is64Bit;
   }

   private final String value;
   private final OsFamily family;
   private final boolean is64Bit;

   OSType(int code, String value, OsFamily family, boolean is64Bit) {
      this.code = code;
      this.value = value;
      this.family = family;
      this.is64Bit = is64Bit;
   }

   public static OSType fromValue(int code) {
      for (OSType type : values()) {
         if (type.code == code)
            return type;
      }
      return UNRECOGNIZED;
   }

}
