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
package org.jclouds.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.domain.Volume;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class DescribeVolumesResponseHandler extends ParseSax.HandlerWithResult<Set<Volume>> {

   private Set<Volume> volumes = Sets.newLinkedHashSet();
   private final CreateVolumeResponseHandler volumeHandler;

   private boolean inAttachmentSet;

   @Inject
   public DescribeVolumesResponseHandler(CreateVolumeResponseHandler volumeHandler) {
      this.volumeHandler = volumeHandler;
   }

   public Set<Volume> getResult() {
      return volumes;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("attachmentSet")) {
         inAttachmentSet = true;
      }
      volumeHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      volumeHandler.endElement(uri, localName, qName);
      if (qName.equals("attachmentSet")) {
         inAttachmentSet = false;
      } else if (qName.equals("item") && !inAttachmentSet) {
         this.volumes.add(volumeHandler.getResult());
      }
   }

   public void characters(char ch[], int start, int length) {
      volumeHandler.characters(ch, start, length);
   }

   @Override
   public DescribeVolumesResponseHandler setContext(HttpRequest request) {
      volumeHandler.setContext(request);
      super.setContext(request);
      return this;
   }
}
