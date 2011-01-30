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

package org.jclouds.vcloud.terremark.compute.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TerremarkECloudParseOsFromVAppTemplateNameTest")
public class TerremarkECloudParseOsFromVAppTemplateNameTest {

   public Set<String> parseNames(String resource) {
      InputStream is = getClass().getResourceAsStream(resource);
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      return factory.create(injector.getInstance(CatalogHandler.class)).parse(is).keySet();
   }

   public void test() {

      TerremarkECloudParseOsFromVAppTemplateName function = new TerremarkECloudParseOsFromVAppTemplateName(Guice
               .createInjector(new GsonModule()).getInstance(Json.class).<Map<OsFamily, Map<String, String>>> fromJson(
                        new ComputeServiceConstants.ReferenceData().osVersionMapJson,
                        new TypeLiteral<Map<OsFamily, Map<String, String>>>() {
                        }.getType()));
      Set<String> names = parseNames("/catalog-ecloud.xml");
      assertEquals(Sets.newLinkedHashSet(Iterables.transform(names, function)), ImmutableSet.of(

      // CentOS 5 (x64)
               new OperatingSystemBuilder().family(OsFamily.CENTOS).version("5.0").description("CentOS 5 (x64)")
                        .is64Bit(true).build(),
               // CentOS 5 (x86)
               new OperatingSystemBuilder().family(OsFamily.CENTOS).version("5.0").description("CentOS 5 (x86)")
                        .is64Bit(false).build(),
               // CentOS 5.5 x32
               new OperatingSystemBuilder().family(OsFamily.CENTOS).version("5.5").description("CentOS 5.5 x32")
                        .is64Bit(false).build(),
               // CentOS 5.5 x64
               new OperatingSystemBuilder().family(OsFamily.CENTOS).version("5.5").description("CentOS 5.5 x64")
                        .is64Bit(true).build(),
               // Red Hat Enterprise Linux 5 (x64)
               new OperatingSystemBuilder().family(OsFamily.RHEL).version("5.0").description(
                        "Red Hat Enterprise Linux 5 (x64)").is64Bit(true).build(),
               // Red Hat Enterprise Linux 5 (x86)
               new OperatingSystemBuilder().family(OsFamily.RHEL).version("5.0").description(
                        "Red Hat Enterprise Linux 5 (x86)").is64Bit(false).build(),
               // Red Hat Enterprise Linux 5.5 x32
               new OperatingSystemBuilder().family(OsFamily.RHEL).version("5.5").description(
                        "Red Hat Enterprise Linux 5.5 x32").is64Bit(false).build(),
               // Red Hat Enterprise Linux 5.5 x64
               new OperatingSystemBuilder().family(OsFamily.RHEL).version("5.5").description(
                        "Red Hat Enterprise Linux 5.5 x64").is64Bit(true).build(),
               // Sun Solaris 10 (x64)
               new OperatingSystemBuilder().family(OsFamily.SOLARIS).version("10").description("Sun Solaris 10 (x64)")
                        .is64Bit(true).build(),
               // Ubuntu 8.04 LTS (x64)
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).version("8.04")
                        .description("Ubuntu 8.04 LTS (x64)").is64Bit(true).build(),
               // Ubuntu 8.04 LTS (x86)
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).version("8.04")
                        .description("Ubuntu 8.04 LTS (x86)").is64Bit(false).build(),
               // Ubuntu Server 10.04 x32
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).version("10.04").description(
                        "Ubuntu Server 10.04 x32").is64Bit(false).build(),
               // Ubuntu Server 10.04 x64
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).version("10.04").description(
                        "Ubuntu Server 10.04 x64").is64Bit(true).build(),
               // -Windows 2003 Std. R2 SQL 2005 Std. (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows 2003 Std. R2 SQL 2005 Std. (x64)").is64Bit(true).build(),
               // -Windows 2003 Std. R2 SQL 2008 Std. (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows 2003 Std. R2 SQL 2008 Std. (x64)").is64Bit(true).build(),
               // -Windows 2008 R2 Std wSQL 2008 R2 Std (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008 R2").description(
                        "-Windows 2008 R2 Std wSQL 2008 R2 Std (x64)").is64Bit(true).build(),
               // -Windows 2008 R2 Std wSQL 2008 R2 Web (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008 R2").description(
                        "-Windows 2008 R2 Std wSQL 2008 R2 Web (x64)").is64Bit(true).build(),
               // -Windows 2008 Std wSQL 2008 Std (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows 2008 Std wSQL 2008 Std (x64)").is64Bit(true).build(),
               // -Windows 2008 Std wSQL 2008 Web (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows 2008 Std wSQL 2008 Web (x64)").is64Bit(true).build(),
               // -Windows Server 2003 R2 Enterprise Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows Server 2003 R2 Enterprise Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2003 R2 Enterprise Edition (x86)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows Server 2003 R2 Enterprise Edition (x86)").is64Bit(false).build(),
               // -Windows Server 2003 R2 Standard Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows Server 2003 R2 Standard Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2003 R2 Standard Edition (x86)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2003 R2").description(
                        "-Windows Server 2003 R2 Standard Edition (x86)").is64Bit(false).build(),
               // -Windows Server 2008 Enterprise Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Enterprise Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 Enterprise Edition (x86)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Enterprise Edition (x86)").is64Bit(false).build(),
               // -Windows Server 2008 R2 Enterprise Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008 R2").description(
                        "-Windows Server 2008 R2 Enterprise Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 R2 Standard Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008 R2").description(
                        "-Windows Server 2008 R2 Standard Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 R2 Web Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008 R2").description(
                        "-Windows Server 2008 R2 Web Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 Standard Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Standard Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 Standard Edition (x86)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Standard Edition (x86)").is64Bit(false).build(),
               // -Windows Server 2008 Web Edition (x64)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Web Edition (x64)").is64Bit(true).build(),
               // -Windows Server 2008 Web Edition (x86)
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).version("2008").description(
                        "-Windows Server 2008 Web Edition (x86)").is64Bit(false).build()

      ));

   }
}
