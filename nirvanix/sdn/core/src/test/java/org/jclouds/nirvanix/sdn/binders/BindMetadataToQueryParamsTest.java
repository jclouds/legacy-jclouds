/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.nirvanix.sdn.binders;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RuntimeDelegateImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BindMetadataToQueryParams}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.BindMetadataToQueryParamsTest")
public class BindMetadataToQueryParamsTest {
   static {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeMap() {
      BindMetadataToQueryParams binder = new BindMetadataToQueryParams();
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {
      BindMetadataToQueryParams binder = new BindMetadataToQueryParams();

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      request.addQueryParam("metadata", "imagename:foo", "serverid:2");
      replay(request);
      binder.bindToRequest(request, ImmutableMap.of("imageName", "foo", "serverId", "2"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindMetadataToQueryParams binder = new BindMetadataToQueryParams();
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      binder.bindToRequest(request, null);
   }
}
