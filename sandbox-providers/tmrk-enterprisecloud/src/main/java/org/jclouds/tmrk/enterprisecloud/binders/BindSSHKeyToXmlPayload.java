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

import com.google.common.collect.ImmutableMap;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.jclouds.util.Strings2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author Jason King
 */
@Singleton
public class BindSSHKeyToXmlPayload implements Binder {

   private final String xmlTemplate;
   private final BindToStringPayload stringBinder;

   @Inject
   BindSSHKeyToXmlPayload(@Named("EditSSHKey") String xmlTemplate,
                          BindToStringPayload stringBinder) {
      this.xmlTemplate = xmlTemplate;
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object key) {
      checkArgument(checkNotNull(key, "key") instanceof SSHKey, "this binder is only valid for SSHKey instances!");
      checkNotNull(request, "request");
      SSHKey sshKey = SSHKey.class.cast(key);

      String name = sshKey.getName();
      String isDefault = Boolean.toString(sshKey.isDefaultKey());
      String fingerPrint = sshKey.getFingerPrint();

      String payload = Strings2.replaceTokens(xmlTemplate,
            ImmutableMap.of("name", name, "isDefault", isDefault, "fingerPrint", fingerPrint));
            
      return stringBinder.bindToRequest(request, payload);
   }
}
