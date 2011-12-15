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
package org.jclouds.glesys.parse;


import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import java.util.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.*;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerTemplatesTest")
public class ParseServerTemplatesTest extends BaseItemParserTest<Map<String, Set<ServerTemplate>>> {

   @Override
   public String resource() {
      return "/server_templates.json";
   }

   @Override
   @SelectJson("templates")
   @Consumes(MediaType.APPLICATION_JSON)
   public Map<String, Set<ServerTemplate>> expected() {
      Map<String, Set<ServerTemplate>> result = new LinkedHashMap<String, Set<ServerTemplate>>();
      
      String[] vzNames = new String[]{
            "Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit",
            "Debian 5.0 32-bit", "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", 
            "Fedora Core 11", "Fedora Core 11 64-bit", "Gentoo", "Gentoo 64-bit",
            "Scientific Linux 6", "Scientific Linux 6 64-bit", "Slackware 12",
            "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit"
      };
      String[] xenLinuxNames = new String[] {
            "CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86", "Debian-6 x64", "Debian 5.0.1 x64",
            "FreeBSD 8.2", "Gentoo 10.1 x64", "Ubuntu 8.04 x64", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 10.10 x64", "Ubuntu 11.04 x64",
      };
      String[] xenWindowsNames = new String[] {
            "Windows Server 2008 R2 x64 std", "Windows Server 2008 R2 x64 web", "Windows Server 2008 x64 web", "Windows Server 2008 x86 web"
      };

      result.put("OpenVZ", new HashSet<ServerTemplate>());
      for (String name : vzNames) {
         result.get("OpenVZ").add(new ServerTemplate(name, 5, 128, "linux", "OpenVZ"));
      }

      result.put("Xen", new HashSet<ServerTemplate>());
      for (String name : xenLinuxNames) {
         result.get("Xen").add(new ServerTemplate(name, 5, 512, name.startsWith("FreeBSD") ? "freebsd" : "linux", "Xen"));
      }
      for (String name : xenWindowsNames) {
         result.get("Xen").add(new ServerTemplate(name, 20, 1024, "windows", "Xen"));
      }
    
      return result;
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }

}
