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
package org.jclouds.tmrk.enterprisecloud.xml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKeys;
import org.jclouds.tmrk.enterprisecloud.features.SSHKeyAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.*;

/**
 * Tests behavior of JAXB parsing for SSHKey(s)
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "SSHKeysJAXBParsingTest")
public class SSHKeysJAXBParsingTest extends BaseRestClientTest {

   @BeforeClass
   void setupFactory() {
   RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
        "credentialFoo", String.class, Integer.class,
        ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

            @Override
            protected void configure() {}

            @SuppressWarnings("unused")
            @Provides
            @Named("exception")
            Set<String> exception() {
                throw new AuthorizationException();
            }

        }));

      injector = createContextBuilder(contextSpec).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

   @Test
   public void testParseSSHKeysWithJAXB() throws Exception {
      Method method = SSHKeyAsyncClient.class.getMethod("getSSHKeys",URI.class);
      HttpRequest request = factory(SSHKeyAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, SSHKeys> parser = (Function<HttpResponse, SSHKeys>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/sshKeys.xml");
      SSHKeys sshKeys = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertNotNull(sshKeys);
      Link link = Iterables.getOnlyElement(sshKeys.getLinks());
      assertEquals(link.getName(),"Cloudsoft Corporation [Beta]");
      assertEquals(link.getRelationship(), Link.Relationship.UP);

      Action action = Iterables.getOnlyElement(sshKeys.getActions());
      assertEquals(action.getName(),"createSshKey");
      assertEquals(sshKeys.getSSHKeys().size(), 2);

      SSHKey key1 = Iterables.get(sshKeys.getSSHKeys(),0);
      assertTrue(key1.isDefaultKey());
      assertEquals(key1.getFingerPrint(),"c8:3f:41:d6:28:e2:86:37:a6:a3:e6:df:62:d9:31:e5");

      SSHKey key2= Iterables.get(sshKeys.getSSHKeys(),1);
      assertFalse(key2.isDefaultKey());
      assertEquals(key2.getFingerPrint(),"a1:3f:41:d6:28:e2:86:37:a6:a3:e6:df:62:d9:31:e5");
   }

   @Test
   public void testParseSSHKeyWithJAXB() throws Exception {
      Method method = SSHKeyAsyncClient.class.getMethod("getSSHKey",URI.class);
      HttpRequest request = factory(SSHKeyAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, SSHKey> parser = (Function<HttpResponse, SSHKey>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/sshKey.xml");
      SSHKey sshKey = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertEquals(sshKey.getHref(),URI.create("/cloudapi/ecloud/admin/sshkeys/77"));
      assertEquals(sshKey.getType(),"application/vnd.tmrk.cloud.admin.sshKey");
      assertEquals(sshKey.getName(),"test");
      
      assertEquals(sshKey.getLinks().size(),1);
      Link link = Iterables.getOnlyElement(sshKey.getLinks());
      assertEquals(link.getHref(),URI.create("/cloudapi/ecloud/admin/organizations/17"));
      assertEquals(link.getType(),"application/vnd.tmrk.cloud.admin.organization");
      assertEquals(link.getName(),"Cloudsoft Corporation [Beta]");
      assertEquals(link.getRelationship(), Link.Relationship.UP);

      assertEquals(sshKey.getActions().size(), 2);
      Action action1 = Iterables.get(sshKey.getActions(), 0);
      assertEquals(action1.getHref(),URI.create("/cloudapi/ecloud/admin/sshkeys/77"));
      assertEquals(action1.getType(),"application/vnd.tmrk.cloud.admin.sshKey");
      assertEquals(action1.getName(),"edit");

      Action action2 = Iterables.get(sshKey.getActions(), 1);
      assertEquals(action2.getHref(),URI.create("/cloudapi/ecloud/admin/sshkeys/77"));
      assertNull(action2.getType());
      assertEquals(action2.getName(),"remove");
      assertEquals(action2.getActionDisabled(), Action.ActionDisabled.DISABLED);

      assertEquals(sshKey.isDefaultKey(),true);
      assertEquals(sshKey.getFingerPrint(),"c8:3f:41:d6:28:e2:86:37:a6:a3:e6:df:62:d9:31:e5");
   }
}
