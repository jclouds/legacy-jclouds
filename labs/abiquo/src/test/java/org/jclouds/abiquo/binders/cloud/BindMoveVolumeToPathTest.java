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
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.abiquo.domain.CloudResources;
import org.jclouds.abiquo.features.CloudAsyncApi;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Unit tests for the {@link BindMoveVolumeToPath} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindMoveVolumeToPathTest")
public class BindMoveVolumeToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() throws SecurityException, NoSuchMethodException {
      BindMoveVolumeToPath binder = new BindMoveVolumeToPath();
      binder.getNewEndpoint(generatedHttpRequest(), null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidInputType() throws SecurityException, NoSuchMethodException {
      BindMoveVolumeToPath binder = new BindMoveVolumeToPath();
      binder.getNewEndpoint(generatedHttpRequest(), new Object());
   }

   public void testGetNewEndpoint() throws SecurityException, NoSuchMethodException {
      BindMoveVolumeToPath binder = new BindMoveVolumeToPath();
      String newEndpoint = binder.getNewEndpoint(generatedHttpRequest(), CloudResources.volumePut());
      assertEquals(newEndpoint, "http://localhost/api/cloud/virtualdatacenters/1/volumes/1/action/move");
   }

   private static GeneratedHttpRequest generatedHttpRequest() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> withEndpointLink = method(CloudAsyncApi.class, "moveVolume", VolumeManagementDto.class,
            VirtualDatacenterDto.class);
      return GeneratedHttpRequest.builder()
            .invocation(Invocation.create(withEndpointLink, ImmutableList.<Object> of(CloudResources.volumePut(), CloudResources.virtualDatacenterPut())))
            .method(HttpMethod.POST).endpoint(URI.create("http://localhost")).build();
   }
}
