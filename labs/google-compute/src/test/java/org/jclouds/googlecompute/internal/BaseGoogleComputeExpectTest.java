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
package org.jclouds.googlecompute.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64Url;

import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.oauth.v2.OAuthConstants;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Joiner;
import com.google.common.base.Ticker;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class BaseGoogleComputeExpectTest<T> extends BaseRestApiExpectTest<T> {

   private static final String header = "{\"alg\":\"none\",\"typ\":\"JWT\"}";

   private static final String CLAIMS_TEMPLATE = "{" +
           "\"iss\":\"myproject\"," +
           "\"scope\":\"%s\"," +
           "\"aud\":\"https://accounts.google.com/o/oauth2/token\"," +
           "\"exp\":3600," +
           "\"iat\":0}";

   protected static final String TOKEN = "1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M";

   protected static final HttpResponse TOKEN_RESPONSE = HttpResponse.builder().statusCode(200).payload(
           payloadFromString("{\n" +
                   "  \"access_token\" : \"" + TOKEN + "\",\n" +
                   "  \"token_type\" : \"Bearer\",\n" +
                   "  \"expires_in\" : 3600\n" +
                   "}")).build();


   public BaseGoogleComputeExpectTest() {
      provider = "google-compute";
   }

   @Override
   protected Module createModule() {
      return new Module() {
         @Override
         public void configure(Binder binder) {
            binder.bind(Ticker.class).toInstance(new Ticker() {
               @Override
               public long read() {
                  return 0;
               }
            });
         }
      };
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      // use no sig algorithm for expect tests (means no credential is required either)
      props.put("jclouds.oauth.signature-or-mac-algorithm", OAuthConstants.NO_ALGORITHM);
      return props;
   }

   protected HttpRequest requestForScopes(String... scopes) {
      String claims = String.format(CLAIMS_TEMPLATE, Joiner.on(",").join(scopes));

      String payload = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
              // Base64 Encoded Header
              "assertion=" + base64Url().omitPadding().encode(header.getBytes(UTF_8)) + "." +
              // Base64 Encoded Claims
              base64Url().omitPadding().encode(claims.getBytes(UTF_8)) + ".";

      return HttpRequest.builder()
              .method("POST")
              .endpoint(URI.create("https://accounts.google.com/o/oauth2/token"))
              .addHeader("Accept", MediaType.APPLICATION_JSON)
              .payload(payloadFromStringWithContentType(payload, "application/x-www-form-urlencoded"))
              .build();
   }

   /**
    * Parse tests don't apply @Transform so we need to apply the transformation to PagedIterable on the result of
    * expected()
    */
   protected <T> PagedIterable<T> toPagedIterable(ListPage<T> list) {
      return PagedIterables.of(list);
   }


}
