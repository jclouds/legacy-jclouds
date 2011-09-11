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

import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class DescribeSnapshotsResponseHandler extends ParseSax.HandlerWithResult<Set<Snapshot>> {

   private Set<Snapshot> snapshots = Sets.newLinkedHashSet();
   private final SnapshotHandler snapshotHandler;

   @Inject
   public DescribeSnapshotsResponseHandler(SnapshotHandler snapshotHandler) {
      this.snapshotHandler = snapshotHandler;
   }

   public Set<Snapshot> getResult() {
      return snapshots;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      snapshotHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      snapshotHandler.endElement(uri, localName, qName);
      if (qName.equals("item")) {
         this.snapshots.add(snapshotHandler.getResult());
      }
   }

   public void characters(char ch[], int start, int length) {
      snapshotHandler.characters(ch, start, length);
   }

   @Override
   public DescribeSnapshotsResponseHandler setContext(HttpRequest request) {
      snapshotHandler.setContext(request);
      super.setContext(request);
      return this;
   }
}
