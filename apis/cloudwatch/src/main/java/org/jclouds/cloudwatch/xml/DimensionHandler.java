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
package org.jclouds.cloudwatch.xml;

import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_Dimension.html" />
 *
 * @author Jeremy Whitlock
 */
public class DimensionHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Dimension> {

   private StringBuilder currentText = new StringBuilder();
   private String name;
   private String value;

   /**
    * {@inheritDoc}
    */
   @Override
   public Dimension getResult() {
      Dimension dimension = new Dimension(name, value);

      // Reset since this handler is created once but produces N results
      name = null;
      value = null;

      return dimension;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("Name")) {
         this.name = SaxUtils.currentOrNull(currentText);
      } else if (qName.equals("Value")) {
         value = SaxUtils.currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
