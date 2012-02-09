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
package org.jclouds.cloudstack.filters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.api.uri.UriComponent;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLEncoder;

import static org.testng.Assert.assertEquals;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReEncodeQueryWithJavaNetURLEncoder")
public class ReEncodeQueryWithJavaNetURLEncoderTest {

   @Test
   public void testReUrlEncode() {
      String input = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCc903twxU2zcQnIJdXv61RwZNZW94uId9qz08fgsBJsCOnHNIC4+L9k" +
         "DOA2IHV9cUfEDBm1Be5TbpadWwSbS/05E+FARH2/MCO932UgcKUq5PGymS0249fLCBPci5zoLiG5vIym+1ij1hL/nHvkK99NIwe7io+Lmp" +
         "9OcF3PTsm3Rgh5T09cRHGX9horp0VoAVa9vKJx6C1/IEHVnG8p0YPPa1lmemvx5kNBEiyoNQNYa34EiFkcJfP6rqNgvY8h/j4nE9SXoUCC" +
         "/g6frhMFMOL0tzYqvz0Lczqm1Oh4RnSn3O9X4R934p28qqAobe337hmlLUdb6H5zuf+NwCh0HdZ";

      String defaultJcloudsEncodedRequest = "http://localhost?foo=" + Strings2.urlEncode(input);
      @SuppressWarnings("deprecation")
      String defaultEncodedRequest = "http://localhost?foo=" + URLEncoder.encode(input);
      assert !defaultJcloudsEncodedRequest.equals(defaultEncodedRequest);

      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost?foo=" + Strings2.urlEncode(input)));
      request = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(UriBuilder.class).to(UriBuilderImpl.class);
         }

      }).getInstance(ReEncodeQueryWithDefaultURLEncoder.class).filter(request);
      assertEquals(request.getEndpoint().toASCIIString(), "http://localhost?foo=" +
         UriComponent.encode(input, UriComponent.Type.QUERY_PARAM));
   }
}
