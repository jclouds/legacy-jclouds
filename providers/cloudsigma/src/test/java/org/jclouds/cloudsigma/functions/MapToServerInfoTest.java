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

package org.jclouds.cloudsigma.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.jclouds.cloudsigma.domain.DriveMetrics;
import org.jclouds.cloudsigma.domain.IDEDevice;
import org.jclouds.cloudsigma.domain.MediaType;
import org.jclouds.cloudsigma.domain.Model;
import org.jclouds.cloudsigma.domain.NIC;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.domain.ServerMetrics;
import org.jclouds.cloudsigma.domain.ServerStatus;
import org.jclouds.cloudsigma.domain.VNC;
import org.jclouds.cloudsigma.functions.MapToDevices.DeviceToId;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapToServerInfoTest {
   public static ServerInfo ONE = new ServerInfo.Builder()
         .persistent(true)
         .uuid("f8bee9cd-8e4b-4a05-8593-1314e3bfe49b")
         .cpu(2000)
         .bootDeviceIds(ImmutableSet.of("ide:0:0"))
         .smp(1)
         .mem(1024)
         .status(ServerStatus.ACTIVE)
         .started(new Date(1291493868l))
         .user("2f6244eb-50bc-4403-847e-f03cc3706a1f")
         .name("jo")
         .vnc(new VNC("46.20.114.124", "HfHzVmLT", false))
         .nics(ImmutableSet.of(new NIC.Builder()
               .model(Model.E1000)
               .dhcp("46.20.114.124")
               .block(
                     ImmutableList.of("tcp/43594", "tcp/5902", "udp/5060", "tcp/5900", "tcp/5901", "tcp/21", "tcp/22",
                           "tcp/23", "tcp/25", "tcp/110", "tcp/143", "tcp/43595")).build()))
         .devices(
               ImmutableMap.of("ide:0:0",
                     new IDEDevice.Builder((int) 0, (int) 0).uuid("4af85ed3-0caa-4736-8a26-a33d7de0a122").build()

               ))
         .metrics(
               new ServerMetrics.Builder()
                     .tx(2550)
                     .txPackets(31)
                     .rx(455530)
                     .rxPackets(7583)
                     .driveMetrics(
                           ImmutableMap.of("ide:0:0", new DriveMetrics.Builder().readRequests(11154)
                                 .readBytes(45686784).writeRequests(3698).writeBytes(15147008).build())).build())
         .build();

   public static ServerInfo TWO = new ServerInfo.Builder()
         .status(ServerStatus.STOPPED)
         .name("Demo")
         .mem(1024)
         .cpu(2000)
         .persistent(true)
         .uuid("0f962616-2071-4173-be79-7dd084271edf")
         .bootDeviceIds(ImmutableSet.of("ide:0:0"))
         .user("2f6244eb-50bc-4403-847e-f03cc3706a1f")
         .vnc(new VNC("auto", "HWbjvrg2", false))
         .nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).dhcp("auto").build()))
         .devices(
               ImmutableMap.of(
                     "ide:0:0",
                     new IDEDevice.Builder((int) 0, (int) 0).uuid("853bb98a-4fff-4c2f-a265-97c363f19ea5")
                           .mediaType(MediaType.CDROM).build()))
         .metrics(
               new ServerMetrics.Builder().driveMetrics(ImmutableMap.of("ide:0:0", new DriveMetrics.Builder().build()))
                     .build()).build();

   private static final MapToServerInfo MAP_TO_DRIVE = new MapToServerInfo(new MapToDevices(new DeviceToId()),
         new MapToServerMetrics(new MapToDriveMetrics()), new MapToNICs());

   public void testEmptyMapReturnsNull() {
      assertEquals(MAP_TO_DRIVE.apply(ImmutableMap.<String, String> of()), null);
   }

   public void testBasics() {
      ServerInfo expects = new ServerInfo.Builder().name("foo").uuid("hello").vnc(new VNC("auto", null, false))
            .cpu(1000).mem(2048).metrics(new ServerMetrics.Builder().build()).build();
      assertEquals(MAP_TO_DRIVE.apply(ImmutableMap.of("name", "foo", "server", "hello", "vnc:ip", "auto", "cpu",
            "1000", "mem", "2048")), expects);
   }

   public void testComplete() throws IOException {

      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToServerInfoTest.class.getResourceAsStream("/servers.txt"))).get(0);

      assertEquals(MAP_TO_DRIVE.apply(input), ONE);

   }

   public static ServerInfo NEW = new ServerInfo.Builder()
         .persistent(true)
         .uuid("bd98615a-6f74-4d63-ad1e-b13338b9356a")
         .cpu(1000)
         .bootDeviceIds(ImmutableSet.of("ide:0:0"))
         .smp(1)
         .mem(512)
         .status(ServerStatus.ACTIVE)
         .started(new Date(1292695612))
         .user("2f6244eb-50bc-4403-847e-f03cc3706a1f")
         .name("adriancole.test")
         .vnc(new VNC("83.222.249.221", "XXXXXXXX", false))
         .nics(ImmutableSet.of(new NIC.Builder()
               .model(Model.E1000)
               .block(
                     ImmutableList.of("tcp/43594", "tcp/5902", "udp/5060", "tcp/5900", "tcp/5901", "tcp/21", "tcp/22",
                           "tcp/23", "tcp/25", "tcp/110", "tcp/143", "tcp/43595")).build()))
         .devices(
               ImmutableMap.of("ide:0:0",
                     new IDEDevice.Builder((int) 0, (int) 0).uuid("403c9a86-0aab-4e47-aa95-e9768021c4c1").build()

               ))
         .metrics(
               new ServerMetrics.Builder().driveMetrics(ImmutableMap.of("ide:0:0", new DriveMetrics.Builder().build()))
                     .build()).build();

   public void testNew() throws IOException {

      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToServerInfoTest.class.getResourceAsStream("/new_server.txt"))).get(0);

      assertEquals(MAP_TO_DRIVE.apply(input), NEW);

   }
}