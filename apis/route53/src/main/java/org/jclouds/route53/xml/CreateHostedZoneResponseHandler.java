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
package org.jclouds.route53.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.HostedZoneAndNameServers;
import org.jclouds.route53.domain.NewHostedZone;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_CreateHostedZone.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class CreateHostedZoneResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<NewHostedZone> {

   private final GetHostedZoneResponseHandler zoneHandler;
   private final ChangeHandler changeHandler;

   private boolean inChange;

   @Inject
   public CreateHostedZoneResponseHandler(GetHostedZoneResponseHandler zoneHandler, ChangeHandler changeHandler) {
      this.zoneHandler = zoneHandler;
      this.changeHandler = changeHandler;
   }

   @Override
   public NewHostedZone getResult() {
      HostedZoneAndNameServers zone = zoneHandler.getResult();
      return NewHostedZone.create(zone, changeHandler.getResult());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "ChangeInfo")) {
         inChange = true;
      }
      if (inChange) {
         changeHandler.startElement(url, name, qName, attributes);
      } else {
         zoneHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inChange) {
         if (qName.equals("ChangeInfo")) {
            inChange = false;
         } else {
            changeHandler.endElement(uri, name, qName);
         }
      } else {
         zoneHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inChange) {
         changeHandler.characters(ch, start, length);
      } else {
         zoneHandler.characters(ch, start, length);
      }
   }
}
