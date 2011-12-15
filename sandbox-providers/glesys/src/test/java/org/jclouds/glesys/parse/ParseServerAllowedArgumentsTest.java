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
import org.jclouds.glesys.domain.ServerAllowedArguments;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerAllowedArgumentsTest")
public class ParseServerAllowedArgumentsTest extends BaseItemParserTest<Map<String, ServerAllowedArguments>> {

    @Override
    public String resource() {
        return "/server_allowed_arguments.json";
    }

    @Override
    @SelectJson("argumentslist")
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, ServerAllowedArguments> expected() {
        Map<String, ServerAllowedArguments> result = new LinkedHashMap<String, ServerAllowedArguments>();
        ServerAllowedArguments openvz = ServerAllowedArguments.builder()
                .dataCenters("Amsterdam", "Falkenberg", "New York City", "Stockholm")
                .memorySizes(128, 256, 512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288)
                .diskSizes(5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120, 140, 150)
                .cpuCores(1, 2, 3, 4, 5, 6, 7, 8)
                .templates("Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit", "Debian 5.0 32-bit",
                        "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", "Fedora Core 11", "Fedora Core 11 64-bit",
                        "Gentoo", "Gentoo 64-bit", "Scientific Linux 6", "Scientific Linux 6 64-bit", "Slackware 12",
                        "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit")
                .transfers(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000)
                .build();
        ServerAllowedArguments xen = ServerAllowedArguments.builder()
                .memorySizes(512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288, 14336, 16384)
                .diskSizes(5, 10, 20, 30, 40, 50, 80, 100, 120, 140, 150, 160, 160, 200, 250, 300)
                .cpuCores(1, 2, 3, 4, 5, 6, 7, 8)
                .templates("CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86", "Debian-6 x64",
                        "Debian 5.0.1 x64", "FreeBSD 8.2", "Gentoo 10.1 x64", "Ubuntu 8.04 x64", "Ubuntu 10.04 LTS 64-bit",
                        "Ubuntu 10.10 x64", "Ubuntu 11.04 x64", "Windows Server 2008 R2 x64 std",
                        "Windows Server 2008 R2 x64 web", "Windows Server 2008 x64 web", "Windows Server 2008 x86 web")
                .transfers(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000)
                .dataCenters("Falkenberg")
                .build();
        result.put("Xen", xen);
        result.put("OpenVZ", openvz);
        return result;
    }


    protected Injector injector() {
        return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
    }
}
