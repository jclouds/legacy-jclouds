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

package org.jclouds.vcloud.xml;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.domain.network.internal.VCloudExpressOrgNetworkAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class OrgNetworkFromVCloudExpressNetworkHandler extends ParseSax.HandlerWithResult<OrgNetwork> {

   protected final VCloudExpressNetworkHandler vcxHandler;

   @Inject
   public OrgNetworkFromVCloudExpressNetworkHandler(VCloudExpressNetworkHandler vcxHandler) {
      this.vcxHandler = vcxHandler;
   }

   public OrgNetwork getResult() {
      return new VCloudExpressOrgNetworkAdapter(vcxHandler.getResult());
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      vcxHandler.startElement(uri, localName, qName, attributes);
   }

   public void endElement(String uri, String name, String qName) {
      vcxHandler.endElement(uri, name, qName);
   }

   public void characters(char ch[], int start, int length) {
      vcxHandler.characters(ch, start, length);
   }

}
