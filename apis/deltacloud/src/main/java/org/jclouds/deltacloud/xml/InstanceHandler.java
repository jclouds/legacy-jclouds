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
package org.jclouds.deltacloud.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.Instance.Authentication;
import org.jclouds.deltacloud.domain.KeyAuthentication;
import org.jclouds.deltacloud.domain.PasswordAuthentication;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class InstanceHandler extends ParseSax.HandlerWithResult<Instance> {
   private StringBuilder currentText = new StringBuilder();

   @Resource
   protected Logger logger = Logger.NULL;

   private URI href;
   private String id;
   private String ownerId;
   private String name;
   private URI image;
   private URI hardwareProfile;
   private URI realm;
   private Instance.State state;
   private Map<Instance.Action, HttpRequest> actions = Maps.newLinkedHashMap();
   private Set<String> publicAddresses = Sets.newLinkedHashSet();
   private Set<String> privateAddresses = Sets.newLinkedHashSet();

   private boolean inPublicAddresses;
   private boolean inPrivateAddresses;

   private Instance instance;

   private Builder credentialsBuilder = LoginCredentials.builder();
   private String keyName;
   private Authentication authentication;

   public Instance getResult() {
      return instance;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("public_addresses")) {
         inPublicAddresses = true;
      } else if (qName.equals("private_addresses")) {
         inPrivateAddresses = true;
      } else if (qName.equals("instance")) {
         String href = attributes.get("href");
         if (href != null) {
            this.href = URI.create(href);
         }
         this.id = attributes.get("id");
      } else if (qName.equals("link")) {
         try {
            Instance.Action action = Instance.Action.fromValue(attributes.get("rel"));
            if (action != Instance.Action.UNRECOGNIZED) {
               HttpRequest request = new HttpRequest(attributes.get("method").toUpperCase(), URI.create(attributes
                        .get("href")));
               actions.put(action, request);
            }
         } catch (RuntimeException e) {
            if (logger.isDebugEnabled())
               logger.warn(e, "error parsing into action: %s, %s", qName, attributes);
         }
      } else if (attributes.containsKey("href")) {
         URI href = URI.create(attributes.get("href"));
         if (qName.equals("image"))
            this.image = href;
         else if (qName.equals("hardware_profile"))
            this.hardwareProfile = href;
         else if (qName.equals("realm"))
            this.realm = href;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.endsWith("public_addresses")) {
         inPublicAddresses = false;
      } else if (qName.endsWith("private_addresses")) {
         inPrivateAddresses = false;
      }
      if (qName.equalsIgnoreCase("owner_id")) {
         this.ownerId = currentOrNull(currentText);
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentOrNull(currentText);
      } else if (qName.equalsIgnoreCase("keyname")) {
         this.keyName = currentOrNull(currentText);
      } else if (qName.equalsIgnoreCase("username")) {
         this.credentialsBuilder.user(currentOrNull(currentText));
      } else if (qName.equalsIgnoreCase("password")) {
         this.credentialsBuilder.password(currentOrNull(currentText));
      } else if (qName.equalsIgnoreCase("authentication")) {
         if (keyName != null) {
            this.authentication = new KeyAuthentication(keyName);
         } else {
            LoginCredentials creds = credentialsBuilder.build();
            if (creds != null && creds.identity != null)
               this.authentication = new PasswordAuthentication(creds);
         }
         this.keyName = null;
         this.credentialsBuilder = LoginCredentials.builder();
      } else if (qName.equalsIgnoreCase("state")) {
         this.state = Instance.State.fromValue(currentOrNull(currentText));
      } else if (qName.equalsIgnoreCase("address")) {
         if (inPublicAddresses)
            this.publicAddresses.add(currentOrNull(currentText));
         else if (inPrivateAddresses)
            this.privateAddresses.add(currentOrNull(currentText));
      } else if (qName.equalsIgnoreCase("instance")) {
         this.instance = new Instance(href, id, ownerId, name, image, hardwareProfile, realm, state, actions,
                  authentication, publicAddresses, privateAddresses);
         this.href = null;
         this.id = null;
         this.ownerId = null;
         this.name = null;
         this.image = null;
         this.hardwareProfile = null;
         this.realm = null;
         this.state = null;
         this.authentication = null;
         this.actions = Maps.newLinkedHashMap();
         this.publicAddresses = Sets.newLinkedHashSet();
         this.privateAddresses = Sets.newLinkedHashSet();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
