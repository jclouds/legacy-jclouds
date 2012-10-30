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
package org.jclouds.trmk.vcloud_0_8.xml;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class NodesHandler extends HandlerWithResult<Set<Node>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final NodeHandler handler;
   Set<Node> result = Sets.newLinkedHashSet();

   @Inject
   public NodesHandler(NodeHandler handler) {
      this.handler = handler;
   }

   @Override
   public Set<Node> getResult() {
      return result;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      handler.startElement(uri, localName, qName, attributes);
   }

   public void endElement(String uri, String name, String qName) {
      handler.endElement(uri, name, qName);
      if (qName.equals("NodeService")) {
         result.add(handler.getResult());
      }
   }

   public void characters(char ch[], int start, int length) {
      handler.characters(ch, start, length);
   }

}
