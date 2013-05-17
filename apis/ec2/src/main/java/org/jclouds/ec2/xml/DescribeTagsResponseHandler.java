/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.ec2.domain.Tag;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class DescribeTagsResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<Tag>> {

   private final TagHandler tagHander;

   private StringBuilder currentText = new StringBuilder();
   private Builder<Tag> tags = ImmutableSet.<Tag> builder();
   private boolean inTags;

   @Inject
   public DescribeTagsResponseHandler(TagHandler tagHander) {
      this.tagHander = tagHander;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FluentIterable<Tag> getResult() {
      return FluentIterable.from(tags.build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTags = true;
      }
      if (inTags) {
         tagHander.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTags = false;
      } else if (equalsOrSuffix(qName, "item")) {
         tags.add(tagHander.getResult());
      } else if (inTags) {
         tagHander.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inTags) {
         tagHander.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
