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

package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.InstanceAction;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class InstanceHandler extends ParseSax.HandlerWithResult<Instance> {
   private StringBuilder currentText = new StringBuilder();

   private URI href;
   private String id;
   private String ownerId;
   private String name;
   private URI image;
   private URI hardwareProfile;
   private URI realm;
   private InstanceState state;
   private Map<InstanceAction, URI> actions = Maps.newLinkedHashMap();
   private Set<String> publicAddresses = Sets.newLinkedHashSet();
   private Set<String> privateAddresses = Sets.newLinkedHashSet();

   private boolean inPublicAddresses;
   private boolean inPrivateAddresses;

   private Instance instance;

   public Instance getResult() {
      return instance;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
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
         String rel = attributes.get("rel");
         if (rel != null) {
            InstanceAction action = InstanceAction.fromValue(rel);
            if (action != InstanceAction.UNRECOGNIZED) {
               actions.put(action, URI.create(attributes.get("href")));
            }
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
         this.ownerId = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("state")) {
         this.state = InstanceState.fromValue(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("address")) {
         if (inPublicAddresses)
            this.publicAddresses.add(currentText.toString().trim());
         else if (inPrivateAddresses)
            this.privateAddresses.add(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("instance")) {
         this.instance = new Instance(href, id, ownerId, name, image, hardwareProfile, realm, state, actions,
               publicAddresses, privateAddresses);
         this.href = null;
         this.id = null;
         this.ownerId = null;
         this.name = null;
         this.image = null;
         this.hardwareProfile = null;
         this.realm = null;
         this.state = null;
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
