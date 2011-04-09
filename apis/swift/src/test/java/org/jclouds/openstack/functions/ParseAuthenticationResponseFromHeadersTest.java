/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Tests behavior of {@code ParseContainerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseAuthenticationResponseFromHeadersTest {

   Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(Names.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to("sdf");
         bindConstant().annotatedWith(Names.named(Constants.PROPERTY_API_VERSION)).to("1");
         bind(UriBuilder.class).to(UriBuilderImpl.class);
      }

   });

   public void testReplaceLocalhost() {
      ParseAuthenticationResponseFromHeaders parser = i.getInstance(ParseAuthenticationResponseFromHeaders.class);
      parser = parser.setHostToReplace("fooman");
      
      HttpResponse response = new HttpResponse(204, "No Content", null, ImmutableMultimap.<String, String> of(
               "X-Auth-Token", "token", "X-Storage-Token", "token", "X-Storage-Url", "http://127.0.0.1:8080/v1/token"));

      AuthenticationResponse md = parser.apply(response);
      assertEquals(md, new AuthenticationResponse("token", ImmutableMap.<String, URI> of("X-Storage-Url", URI
               .create("http://fooman:8080/v1/token"))));
   }
}
