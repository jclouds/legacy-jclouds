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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.jclouds.http.HttpRequest;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code BindSSHKeyToXmlPayloadTest}
 * @author Jason King
 */
@Test(groups = "unit", testName = "BindSSHKeyToXmlPayloadTest")
public class BindSSHKeyToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }
   });

   public void testPayloadXmlContent() throws IOException {
      final String name = "newName";
      final boolean isDefault = false;
      final String fingerPrint = "123";
      final String expected = String.format("<SshKey name=\"%s\"><Default>%b</Default><FingerPrint>%s</FingerPrint></SshKey>",
                                     name,isDefault,fingerPrint);

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindSSHKeyToXmlPayload binder = injector
               .getInstance(BindSSHKeyToXmlPayload.class);

      SSHKey key = SSHKey.builder().type("application/vnd.tmrk.cloud.admin.sshKey")
            .href(URI.create("/cloudapi/ecloud/admin/sshkeys/77"))
            .name(name)
            .defaultKey(isDefault).fingerPrint(fingerPrint).build();

      binder.bindToRequest(request, key);
      assertEquals(request.getPayload().getRawContent(), expected);
   }
}
