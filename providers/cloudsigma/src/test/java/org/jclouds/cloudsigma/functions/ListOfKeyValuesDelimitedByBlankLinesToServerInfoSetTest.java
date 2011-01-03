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

import java.util.List;
import java.util.Map;

import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.DriveMetrics;
import org.jclouds.cloudsigma.domain.NIC;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.domain.ServerMetrics;
import org.jclouds.cloudsigma.functions.MapToDevices.DeviceToId;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ListOfKeyValuesDelimitedByBlankLinesToServerInfoSetTest {

   private static final ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet FN = Guice.createInjector(
         new AbstractModule() {

            @Override
            protected void configure() {
               bind(new TypeLiteral<Function<Map<String, String>, List<NIC>>>() {
               }).to(MapToNICs.class);
               bind(new TypeLiteral<Function<Map<String, String>, Map<String, ? extends Device>>>() {
               }).to(MapToDevices.class);
               bind(new TypeLiteral<Function<Map<String, String>, Map<String, ? extends DriveMetrics>>>() {
               }).to(MapToDriveMetrics.class);
               bind(new TypeLiteral<Function<Map<String, String>, ServerMetrics>>() {
               }).to(MapToServerMetrics.class);
               bind(new TypeLiteral<Function<Device, String>>() {
               }).to(DeviceToId.class);
            }

         }).getInstance(ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class);

   public void testNone() {
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newStringPayload(""))), ImmutableSet.<ServerInfo> of());
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newStringPayload("\n\n"))),
            ImmutableSet.<ServerInfo> of());
      assertEquals(FN.apply(new HttpResponse(200, "", null)), ImmutableSet.<ServerInfo> of());
   }

   public void testOne() {
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newInputStreamPayload(MapToServerInfoTest.class
            .getResourceAsStream("/servers.txt")))), ImmutableSet.<ServerInfo> of(MapToServerInfoTest.ONE,
            MapToServerInfoTest.TWO));
   }
}