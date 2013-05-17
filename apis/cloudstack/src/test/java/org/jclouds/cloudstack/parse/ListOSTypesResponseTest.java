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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListOSTypesResponseTest extends BaseSetParserTest<OSType> {

   @Override
   public String resource() {
      return "/listostypesresponse.json";
   }

   @Override
   @SelectJson("ostype")
   public Set<OSType> expected() {
      return ImmutableSet
            .<OSType> builder()
            .add(OSType.builder().id("69").OSCategoryId("7").description("Asianux 3(32-bit)").build())
            .add(OSType.builder().id("70").OSCategoryId("7").description("Asianux 3(64-bit)").build())
            .add(OSType.builder().id("1").OSCategoryId("1").description("CentOS 4.5 (32-bit)").build())
            .add(OSType.builder().id("2").OSCategoryId("1").description("CentOS 4.6 (32-bit)").build())
            .add(OSType.builder().id("3").OSCategoryId("1").description("CentOS 4.7 (32-bit)").build())
            .add(OSType.builder().id("4").OSCategoryId("1").description("CentOS 4.8 (32-bit)").build())
            .add(OSType.builder().id("5").OSCategoryId("1").description("CentOS 5.0 (32-bit)").build())
            .add(OSType.builder().id("6").OSCategoryId("1").description("CentOS 5.0 (64-bit)").build())
            .add(OSType.builder().id("7").OSCategoryId("1").description("CentOS 5.1 (32-bit)").build())
            .add(OSType.builder().id("8").OSCategoryId("1").description("CentOS 5.1 (64-bit)").build())
            .add(OSType.builder().id("9").OSCategoryId("1").description("CentOS 5.2 (32-bit)").build())
            .add(OSType.builder().id("10").OSCategoryId("1").description("CentOS 5.2 (64-bit)").build())
            .add(OSType.builder().id("11").OSCategoryId("1").description("CentOS 5.3 (32-bit)").build())
            .add(OSType.builder().id("12").OSCategoryId("1").description("CentOS 5.3 (64-bit)").build())
            .add(OSType.builder().id("13").OSCategoryId("1").description("CentOS 5.4 (32-bit)").build())
            .add(OSType.builder().id("14").OSCategoryId("1").description("CentOS 5.4 (64-bit)").build())
            .add(OSType.builder().id("111").OSCategoryId("1").description("CentOS 5.5 (32-bit)").build())
            .add(OSType.builder().id("112").OSCategoryId("1").description("CentOS 5.5 (64-bit)").build())
            .add(OSType.builder().id("73").OSCategoryId("2").description("Debian GNU/Linux 4(32-bit)").build())
            .add(OSType.builder().id("74").OSCategoryId("2").description("Debian GNU/Linux 4(64-bit)").build())
            .add(OSType.builder().id("72").OSCategoryId("2").description("Debian GNU/Linux 5(64-bit)").build())
            .add(OSType.builder().id("15").OSCategoryId("2").description("Debian GNU/Linux 5.0 (32-bit)").build())
            .add(OSType.builder().id("132").OSCategoryId("2").description("Debian GNU/Linux 6(32-bit)").build())
            .add(OSType.builder().id("133").OSCategoryId("2").description("Debian GNU/Linux 6(64-bit)").build())
            .add(OSType.builder().id("102").OSCategoryId("6").description("DOS").build())
            .add(OSType.builder().id("118").OSCategoryId("4").description("Fedora 10").build())
            .add(OSType.builder().id("117").OSCategoryId("4").description("Fedora 11").build())
            .add(OSType.builder().id("116").OSCategoryId("4").description("Fedora 12").build())
            .add(OSType.builder().id("115").OSCategoryId("4").description("Fedora 13").build())
            .add(OSType.builder().id("120").OSCategoryId("4").description("Fedora 8").build())
            .add(OSType.builder().id("119").OSCategoryId("4").description("Fedora 9").build())
            .add(OSType.builder().id("83").OSCategoryId("9").description("FreeBSD (32-bit)").build())
            .add(OSType.builder().id("84").OSCategoryId("9").description("FreeBSD (64-bit)").build())
            .add(OSType.builder().id("92").OSCategoryId("6").description("Microsoft Small Bussiness Server 2003").build())
            .add(OSType.builder().id("78").OSCategoryId("8").description("Novell Netware 5.1").build())
            .add(OSType.builder().id("77").OSCategoryId("8").description("Novell Netware 6.x").build())
            .add(OSType.builder().id("68").OSCategoryId("7").description("Open Enterprise Server").build())
            .add(OSType.builder().id("16").OSCategoryId("3").description("Oracle Enterprise Linux 5.0 (32-bit)").build())
            .add(OSType.builder().id("17").OSCategoryId("3").description("Oracle Enterprise Linux 5.0 (64-bit)").build())
            .add(OSType.builder().id("18").OSCategoryId("3").description("Oracle Enterprise Linux 5.1 (32-bit)").build())
            .add(OSType.builder().id("19").OSCategoryId("3").description("Oracle Enterprise Linux 5.1 (64-bit)").build())
            .add(OSType.builder().id("20").OSCategoryId("3").description("Oracle Enterprise Linux 5.2 (32-bit)").build())
            .add(OSType.builder().id("21").OSCategoryId("3").description("Oracle Enterprise Linux 5.2 (64-bit)").build())
            .add(OSType.builder().id("22").OSCategoryId("3").description("Oracle Enterprise Linux 5.3 (32-bit)").build())
            .add(OSType.builder().id("23").OSCategoryId("3").description("Oracle Enterprise Linux 5.3 (64-bit)").build())
            .add(OSType.builder().id("24").OSCategoryId("3").description("Oracle Enterprise Linux 5.4 (32-bit)").build())
            .add(OSType.builder().id("25").OSCategoryId("3").description("Oracle Enterprise Linux 5.4 (64-bit)").build())
            .add(OSType.builder().id("134").OSCategoryId("3").description("Oracle Enterprise Linux 5.5 (32-bit)").build())
            .add(OSType.builder().id("135").OSCategoryId("3").description("Oracle Enterprise Linux 5.5 (64-bit)").build())
            .add(OSType.builder().id("104").OSCategoryId("7").description("OS/2").build())
            .add(OSType.builder().id("60").OSCategoryId("7").description("Other (32-bit)").build())
            .add(OSType.builder().id("103").OSCategoryId("7").description("Other (64-bit)").build())
            .add(OSType.builder().id("75").OSCategoryId("7").description("Other 2.6x Linux (32-bit)").build())
            .add(OSType.builder().id("76").OSCategoryId("7").description("Other 2.6x Linux (64-bit)").build())
            .add(OSType.builder().id("98").OSCategoryId("7").description("Other Linux (32-bit)").build())
            .add(OSType.builder().id("99").OSCategoryId("7").description("Other Linux (64-bit)").build())
            .add(OSType.builder().id("59").OSCategoryId("10").description("Other Ubuntu (32-bit)").build())
            .add(OSType.builder().id("100").OSCategoryId("10").description("Other Ubuntu (64-bit)").build())
            .add(OSType.builder().id("131").OSCategoryId("10").description("Red Hat Enterprise Linux 2").build())
            .add(OSType.builder().id("66").OSCategoryId("4").description("Red Hat Enterprise Linux 3(32-bit)").build())
            .add(OSType.builder().id("67").OSCategoryId("4").description("Red Hat Enterprise Linux 3(64-bit)").build())
            .add(OSType.builder().id("106").OSCategoryId("4").description("Red Hat Enterprise Linux 4(64-bit)").build())
            .add(OSType.builder().id("26").OSCategoryId("4").description("Red Hat Enterprise Linux 4.5 (32-bit)").build())
            .add(OSType.builder().id("27").OSCategoryId("4").description("Red Hat Enterprise Linux 4.6 (32-bit)").build())
            .add(OSType.builder().id("28").OSCategoryId("4").description("Red Hat Enterprise Linux 4.7 (32-bit)").build())
            .add(OSType.builder().id("29").OSCategoryId("4").description("Red Hat Enterprise Linux 4.8 (32-bit)").build())
            .add(OSType.builder().id("30").OSCategoryId("4").description("Red Hat Enterprise Linux 5.0 (32-bit)").build())
            .add(OSType.builder().id("31").OSCategoryId("4").description("Red Hat Enterprise Linux 5.0 (64-bit)").build())
            .add(OSType.builder().id("32").OSCategoryId("4").description("Red Hat Enterprise Linux 5.1 (32-bit)").build())
            .add(OSType.builder().id("33").OSCategoryId("4").description("Red Hat Enterprise Linux 5.1 (64-bit)").build())
            .add(OSType.builder().id("34").OSCategoryId("4").description("Red Hat Enterprise Linux 5.2 (32-bit)").build())
            .add(OSType.builder().id("35").OSCategoryId("4").description("Red Hat Enterprise Linux 5.2 (64-bit)").build())
            .add(OSType.builder().id("36").OSCategoryId("4").description("Red Hat Enterprise Linux 5.3 (32-bit)").build())
            .add(OSType.builder().id("37").OSCategoryId("4").description("Red Hat Enterprise Linux 5.3 (64-bit)").build())
            .add(OSType.builder().id("38").OSCategoryId("4").description("Red Hat Enterprise Linux 5.4 (32-bit)").build())
            .add(OSType.builder().id("39").OSCategoryId("4").description("Red Hat Enterprise Linux 5.4 (64-bit)").build())
            .add(OSType.builder().id("113").OSCategoryId("4").description("Red Hat Enterprise Linux 5.5 (32-bit)").build())
            .add(OSType.builder().id("114").OSCategoryId("4").description("Red Hat Enterprise Linux 5.5 (64-bit)").build())
            .add(OSType.builder().id("136").OSCategoryId("4").description("Red Hat Enterprise Linux 6.0 (32-bit)").build())
            .add(OSType.builder().id("137").OSCategoryId("4").description("Red Hat Enterprise Linux 6.0 (64-bit)").build())
            .add(OSType.builder().id("85").OSCategoryId("9").description("SCO OpenServer 5").build())
            .add(OSType.builder().id("86").OSCategoryId("9").description("SCO UnixWare 7").build())
            .add(OSType.builder().id("79").OSCategoryId("9").description("Sun Solaris 10(32-bit)").build())
            .add(OSType.builder().id("80").OSCategoryId("9").description("Sun Solaris 10(64-bit)").build())
            .add(OSType.builder().id("82").OSCategoryId("9").description("Sun Solaris 8(Experimental)").build())
            .add(OSType.builder().id("81").OSCategoryId("9").description("Sun Solaris 9(Experimental)").build())
            .add(OSType.builder().id("109").OSCategoryId("5").description("SUSE Linux Enterprise 10(32-bit)").build())
            .add(OSType.builder().id("110").OSCategoryId("5").description("SUSE Linux Enterprise 10(64-bit)").build())
            .add(OSType.builder().id("96").OSCategoryId("5").description("SUSE Linux Enterprise 8(32-bit)").build())
            .add(OSType.builder().id("97").OSCategoryId("5").description("SUSE Linux Enterprise 8(64-bit)").build())
            .add(OSType.builder().id("107").OSCategoryId("5").description("SUSE Linux Enterprise 9(32-bit)").build())
            .add(OSType.builder().id("108").OSCategoryId("5").description("SUSE Linux Enterprise 9(64-bit)").build())
            .add(OSType.builder().id("41").OSCategoryId("5").description("SUSE Linux Enterprise Server 10 SP1 (32-bit)")
                  .build())
            .add(OSType.builder().id("42").OSCategoryId("5").description("SUSE Linux Enterprise Server 10 SP1 (64-bit)")
                  .build())
            .add(OSType.builder().id("43").OSCategoryId("5").description("SUSE Linux Enterprise Server 10 SP2 (32-bit)")
                  .build())
            .add(OSType.builder().id("44").OSCategoryId("5").description("SUSE Linux Enterprise Server 10 SP2 (64-bit)")
                  .build())
            .add(OSType.builder().id("45").OSCategoryId("5").description("SUSE Linux Enterprise Server 10 SP3 (64-bit)")
                  .build())
            .add(OSType.builder().id("46").OSCategoryId("5").description("SUSE Linux Enterprise Server 11 (32-bit)")
                  .build())
            .add(OSType.builder().id("47").OSCategoryId("5").description("SUSE Linux Enterprise Server 11 (64-bit)")
                  .build())
            .add(OSType.builder().id("40").OSCategoryId("5").description("SUSE Linux Enterprise Server 9 SP4 (32-bit)")
                  .build())
            .add(OSType.builder().id("121").OSCategoryId("10").description("Ubuntu 10.04 (32-bit)").build())
            .add(OSType.builder().id("126").OSCategoryId("10").description("Ubuntu 10.04 (64-bit)").build())
            .add(OSType.builder().id("125").OSCategoryId("10").description("Ubuntu 8.04 (32-bit)").build())
            .add(OSType.builder().id("130").OSCategoryId("10").description("Ubuntu 8.04 (64-bit)").build())
            .add(OSType.builder().id("124").OSCategoryId("10").description("Ubuntu 8.10 (32-bit)").build())
            .add(OSType.builder().id("129").OSCategoryId("10").description("Ubuntu 8.10 (64-bit)").build())
            .add(OSType.builder().id("123").OSCategoryId("10").description("Ubuntu 9.04 (32-bit)").build())
            .add(OSType.builder().id("128").OSCategoryId("10").description("Ubuntu 9.04 (64-bit)").build())
            .add(OSType.builder().id("122").OSCategoryId("10").description("Ubuntu 9.10 (32-bit)").build())
            .add(OSType.builder().id("127").OSCategoryId("10").description("Ubuntu 9.10 (64-bit)").build())
            .add(OSType.builder().id("95").OSCategoryId("6").description("Windows 2000 Advanced Server").build())
            .add(OSType.builder().id("105").OSCategoryId("6").description("Windows 2000 Professional").build())
            .add(OSType.builder().id("61").OSCategoryId("6").description("Windows 2000 Server").build())
            .add(OSType.builder().id("55").OSCategoryId("6").description("Windows 2000 Server SP4 (32-bit)").build())
            .add(OSType.builder().id("65").OSCategoryId("6").description("Windows 3.1").build())
            .add(OSType.builder().id("48").OSCategoryId("6").description("Windows 7 (32-bit)").build())
            .add(OSType.builder().id("49").OSCategoryId("6").description("Windows 7 (64-bit)").build())
            .add(OSType.builder().id("63").OSCategoryId("6").description("Windows 95").build())
            .add(OSType.builder().id("62").OSCategoryId("6").description("Windows 98").build())
            .add(OSType.builder().id("64").OSCategoryId("6").description("Windows NT 4").build())
            .add(OSType.builder().id("87").OSCategoryId("6").description("Windows Server 2003 DataCenter Edition(32-bit)")
                  .build())
            .add(OSType.builder().id("88").OSCategoryId("6").description("Windows Server 2003 DataCenter Edition(64-bit)")
                  .build())
            .add(OSType.builder().id("50").OSCategoryId("6").description("Windows Server 2003 Enterprise Edition(32-bit)")
                  .build())
            .add(OSType.builder().id("51").OSCategoryId("6").description("Windows Server 2003 Enterprise Edition(64-bit)")
                  .build())
            .add(OSType.builder().id("89").OSCategoryId("6").description("Windows Server 2003 Standard Edition(32-bit)")
                  .build())
            .add(OSType.builder().id("90").OSCategoryId("6").description("Windows Server 2003 Standard Edition(64-bit)")
                  .build())
            .add(OSType.builder().id("91").OSCategoryId("6").description("Windows Server 2003 Web Edition").build())
            .add(OSType.builder().id("52").OSCategoryId("6").description("Windows Server 2008 (32-bit)").build())
            .add(OSType.builder().id("53").OSCategoryId("6").description("Windows Server 2008 (64-bit)").build())
            .add(OSType.builder().id("54").OSCategoryId("6").description("Windows Server 2008 R2 (64-bit)").build())
            .add(OSType.builder().id("56").OSCategoryId("6").description("Windows Vista (32-bit)").build())
            .add(OSType.builder().id("101").OSCategoryId("6").description("Windows Vista (64-bit)").build())
            .add(OSType.builder().id("93").OSCategoryId("6").description("Windows XP (32-bit)").build())
            .add(OSType.builder().id("94").OSCategoryId("6").description("Windows XP (64-bit)").build())
            .add(OSType.builder().id("57").OSCategoryId("6").description("Windows XP SP2 (32-bit)").build())
            .add(OSType.builder().id("58").OSCategoryId("6").description("Windows XP SP3 (32-bit)").build()).build();
   }
}
