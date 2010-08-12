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

package org.jclouds.chef.filters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;
import java.util.Properties;

import javax.inject.Provider;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.SignedHeaderAuthTest")
public class SignedHeaderAuthTest {

   public static final String USER_ID = "spec-user";
   public static final String BODY = "Spec Body";
   // Base64.encode64(Digest::SHA1.digest("Spec Body")).chomp
   public static final String HASHED_BODY = "DFteJZPVv6WKdQmMqZUQUumUyRs=";
   public static final String TIMESTAMP_ISO8601 = "2009-01-01T12:00:00Z";

   public static final String PATH = "/organizations/clownco";
   // Base64.encode64(Digest::SHA1.digest("/organizations/clownco")).chomp

   public static final String HASHED_CANONICAL_PATH = "YtBWDn1blGGuFIuKksdwXzHU9oE=";
   public static final String REQUESTING_ACTOR_ID = "c0f8a68c52bffa1020222a56b23cccfa";

   // Content hash is ???TODO
   public static final String X_OPS_CONTENT_HASH = "DFteJZPVv6WKdQmMqZUQUumUyRs=";

   public static final String[] X_OPS_AUTHORIZATION_LINES = new String[] {
         "jVHrNniWzpbez/eGWjFnO6lINRIuKOg40ZTIQudcFe47Z9e/HvrszfVXlKG4",
         "NMzYZgyooSvU85qkIUmKuCqgG2AIlvYa2Q/2ctrMhoaHhLOCWWoqYNMaEqPc",
         "3tKHE+CfvP+WuPdWk4jv4wpIkAz6ZLxToxcGhXmZbXpk56YTmqgBW2cbbw4O",
         "IWPZDHSiPcw//AYNgW1CCDptt+UFuaFYbtqZegcBd2n/jzcWODA7zL4KWEUy",
         "9q4rlh/+1tBReg60QdsmDRsw/cdO1GZrKtuCwbuD4+nbRdVBKv72rqHX9cu0", "utju9jzczCyB+sSAQWrxSsXB/b8vV2qs0l4VD2ML+w==" };

   // We expect Mixlib::Authentication::SignedHeaderAuth//sign to return this
   // if passed the BODY above.
   public static final Multimap<String, String> EXPECTED_SIGN_RESULT = ImmutableMultimap.<String, String> builder()
         .put("X-Ops-Content-Hash", X_OPS_CONTENT_HASH).put("X-Ops-Userid", USER_ID).put("X-Ops-Sign", "version=1.0")
         .put("X-Ops-Authorization-1", X_OPS_AUTHORIZATION_LINES[0]).put("X-Ops-Authorization-2",
               X_OPS_AUTHORIZATION_LINES[1]).put("X-Ops-Authorization-3", X_OPS_AUTHORIZATION_LINES[2]).put(
               "X-Ops-Authorization-4", X_OPS_AUTHORIZATION_LINES[3]).put("X-Ops-Authorization-5",
               X_OPS_AUTHORIZATION_LINES[4]).put("X-Ops-Authorization-6", X_OPS_AUTHORIZATION_LINES[5]).put(
               "X-Ops-Timestamp", TIMESTAMP_ISO8601).build();

   // Content hash for empty string
   public static final String X_OPS_CONTENT_HASH_EMPTY = "2jmj7l5rSw0yVb/vlWAYkK/YBwk=";
   public static final Multimap<String, String> EXPECTED_SIGN_RESULT_EMPTY = ImmutableMultimap
         .<String, String> builder().put("X-Ops-Content-Hash", X_OPS_CONTENT_HASH_EMPTY).put("X-Ops-Userid", USER_ID)
         .put("X-Ops-Sign", "version=1.0").put("X-Ops-Authorization-1",
               "N6U75kopDK64cEFqrB6vw+PnubnXr0w5LQeXnIGNGLRP2LvifwIeisk7QxEx").put("X-Ops-Authorization-2",
               "mtpQOWAw8HvnWErjzuk9AvUsqVmWpv14ficvkaD79qsPMvbje+aLcIrCGT1P").put("X-Ops-Authorization-3",
               "3d2uvf4w7iqwzrIscPnkxLR6o6pymR90gvJXDPzV7Le0jbfD8kmZ8AAK0sGG").put("X-Ops-Authorization-4",
               "09F1ftW80bLatJTA66Cw2wBz261r6x/abZhIKFJFDWLzyQGJ8ZNOkUrDDtgI").put("X-Ops-Authorization-5",
               "svLVXpOJKZZfKunsElpWjjsyNt3k8vpI1Y4ANO8Eg2bmeCPeEK+YriGm5fbC").put("X-Ops-Authorization-6",
               "DzWNPylHJqMeGKVYwGQKpg62QDfe5yXh3wZLiQcXow==").put("X-Ops-Timestamp", TIMESTAMP_ISO8601).build();

   public static String PUBLIC_KEY;
   public static String PRIVATE_KEY;

   static {
      try {
         PUBLIC_KEY = Utils.toStringAndClose(SignedHeaderAuthTest.class.getResourceAsStream("/pubkey.txt"));

         PRIVATE_KEY = Utils.toStringAndClose(SignedHeaderAuthTest.class.getResourceAsStream("/privkey.txt"));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
   }

   @Test
   void canonicalizedPathRemovesMultipleSlashes() {
      assertEquals(signing_obj.canonicalPath("///"), "/");
   }

   @Test
   void canonicalizedPathRemovesTrailingSlash() {
      assertEquals(signing_obj.canonicalPath("/path/"), "/path");
   }

   @Test
   void shouldGenerateTheCorrectStringToSignAndSignature() {

      URI host = URI.create("http://localhost/" + PATH);
      HttpRequest request = new HttpRequest(HttpMethod.POST, host);
      request.setPayload(BODY);

      String expected_string_to_sign = new StringBuilder().append("Method:POST").append("\n").append("Hashed Path:")
            .append(HASHED_CANONICAL_PATH).append("\n").append("X-Ops-Content-Hash:").append(HASHED_BODY).append("\n")
            .append("X-Ops-Timestamp:").append(TIMESTAMP_ISO8601).append("\n").append("X-Ops-UserId:").append(USER_ID)
            .toString();

      assertEquals(signing_obj.createStringToSign("POST", HASHED_CANONICAL_PATH, HASHED_BODY, TIMESTAMP_ISO8601),
            expected_string_to_sign);
      assertEquals(signing_obj.sign(expected_string_to_sign), Joiner.on("").join(X_OPS_AUTHORIZATION_LINES));

      signing_obj.filter(request);
      Multimap<String, String> headersWithoutContentLength = LinkedHashMultimap.create(request.getHeaders());
      headersWithoutContentLength.removeAll(HttpHeaders.CONTENT_LENGTH);
      assertEquals(headersWithoutContentLength.values(), EXPECTED_SIGN_RESULT.values());
   }

   @Test
   void shouldGenerateTheCorrectStringToSignAndSignatureWithNoBody() {

      URI host = URI.create("http://localhost/" + PATH);
      HttpRequest request = new HttpRequest(HttpMethod.DELETE, host);

      signing_obj.filter(request);
      Multimap<String, String> headersWithoutContentLength = LinkedHashMultimap.create(request.getHeaders());
      assertEquals(headersWithoutContentLength.entries(), EXPECTED_SIGN_RESULT_EMPTY.entries());
   }

   @Test
   void shouldNotChokeWhenSigningARequestForAResourceWithALongName() {
      StringBuilder path = new StringBuilder("nodes/");
      for (int i = 0; i < 100; i++)
         path.append('A');
      URI host = URI.create("http://localhost/" + path.toString());
      HttpRequest request = new HttpRequest(HttpMethod.PUT, host);
      request.setPayload(BODY);
      signing_obj.filter(request);
   }

   private SignedHeaderAuth signing_obj;
   private Crypto crypto;

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    * @throws IOException
    * 
    */
   @BeforeClass
   protected void createFilter() throws IOException {

      Injector injector = new RestContextFactory().createContextBuilder("chef", USER_ID, PRIVATE_KEY,
            ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule()), new Properties()).buildInjector();

      crypto = injector.getInstance(Crypto.class);
      HttpUtils utils = injector.getInstance(HttpUtils.class);

      PrivateKey privateKey = injector.getInstance(PrivateKey.class);

      signing_obj = new SignedHeaderAuth(new SignatureWire(), USER_ID, privateKey, new Provider<String>() {

         @Override
         public String get() {
            return TIMESTAMP_ISO8601;
         }

      }, crypto, utils);
   }

}