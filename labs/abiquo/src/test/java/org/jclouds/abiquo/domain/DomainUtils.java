/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain;

import org.jclouds.xml.XMLParser;

import com.abiquo.model.rest.RESTLink;

/**
 * Utility class to build domain objects used in tests.
 * 
 * @author Ignasi Barrera
 */
public class DomainUtils {
   /**
    * Adds the XML header to the given XML.
    */
   public static String withHeader(final String xml) {
      return XMLParser.DEFAULT_XML_HEADER + xml;
   }

   /**
    * Builds a link in xml format
    */
   public static String link(final String href, final String rel) {
      return "<link href=\"http://localhost/api" + href + "\" rel=\"" + rel + "\"/>";
   }

   /**
    * Builds a link in xml format
    */
   public static String link(final String href, final String rel, final String title) {
      return "<link href=\"http://localhost/api" + href + "\" rel=\"" + rel + "\" title=\"" + title + "\"/>";
   }

   /**
    * Builds a link in xml format
    */
   public static String link(final RESTLink link) {
      return "<link href=\"" + link.getHref() + "\" rel=\"" + link.getRel() + "\""
            + (link.getTitle() == null ? "" : " title=\"" + link.getTitle() + "\"") + "/>";
   }
}
