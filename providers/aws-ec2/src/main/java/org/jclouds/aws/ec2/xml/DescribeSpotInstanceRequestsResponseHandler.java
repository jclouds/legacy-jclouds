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

package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
//TODO finish
public class DescribeSpotInstanceRequestsResponseHandler extends
         ParseSax.HandlerWithResult<Set<SpotInstanceRequest>> {

   private Set<SpotInstanceRequest> spotRequests = Sets.newLinkedHashSet();
   private final SpotInstanceRequestHandler spotRequestHandler;

   @Inject
   public DescribeSpotInstanceRequestsResponseHandler(SpotInstanceRequestHandler spotRequestHandler) {
      this.spotRequestHandler = spotRequestHandler;
   }

   public Set<SpotInstanceRequest> getResult() {
      return spotRequests;
   }

   @Override
   public HandlerWithResult<Set<SpotInstanceRequest>> setContext(HttpRequest request) {
      spotRequestHandler.setContext(request);
      return super.setContext(request);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (!qName.equals("item"))
         spotRequestHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("item")) {
         spotRequests.add(spotRequestHandler.getResult());
      }
      spotRequestHandler.endElement(uri, localName, qName);
   }

   public void characters(char ch[], int start, int length) {
      spotRequestHandler.characters(ch, start, length);
   }

}
