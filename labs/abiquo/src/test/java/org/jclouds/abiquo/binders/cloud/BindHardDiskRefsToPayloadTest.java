/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.binders.cloud;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;
import static org.jclouds.abiquo.util.Assert.assertPayloadEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.abiquo.domain.CloudResources;
import org.jclouds.http.HttpRequest;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;

/**
 * Unit tests for the {@link BindHardDiskRefsToPayload} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindHardDiskRefsToPayloadTest")
public class BindHardDiskRefsToPayloadTest {

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      BindHardDiskRefsToPayload binder = new BindHardDiskRefsToPayload(new JAXBParser("false"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidTypeInput() {
      BindHardDiskRefsToPayload binder = new BindHardDiskRefsToPayload(new JAXBParser("false"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      binder.bindToRequest(request, new Object());
   }

   public void testBindEmptyArray() throws IOException {
      BindHardDiskRefsToPayload binder = new BindHardDiskRefsToPayload(new JAXBParser("false"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, new DiskManagementDto[] {});
      assertPayloadEquals(request.getPayload(), withHeader("<links/>"), LinksDto.class);
   }

   public void testBindSingleHardDisk() throws IOException {
      DiskManagementDto hardDisk = CloudResources.hardDiskPut();
      BindHardDiskRefsToPayload binder = new BindHardDiskRefsToPayload(new JAXBParser("false"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, new DiskManagementDto[] { hardDisk });
      assertPayloadEquals(request.getPayload(), withHeader("<links><link href=\"" + hardDisk.getEditLink().getHref()
            + "\" rel=\"" + binder.getRelToUse(hardDisk) + "\"/></links>"), LinksDto.class);
   }

   public void testBindMultipleHardDisks() throws IOException {
      DiskManagementDto hardDisk = CloudResources.hardDiskPut();
      BindHardDiskRefsToPayload binder = new BindHardDiskRefsToPayload(new JAXBParser("false"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, new DiskManagementDto[] { hardDisk, hardDisk });
      assertPayloadEquals(request.getPayload(), withHeader("<links><link href=\"" + hardDisk.getEditLink().getHref()
            + "\" rel=\"" + binder.getRelToUse(hardDisk) + "\"/></links>"), LinksDto.class);
   }
}
