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
package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jclouds.deltacloud.domain.EnumHardwareProperty;
import org.jclouds.deltacloud.domain.FixedHardwareProperty;
import org.jclouds.deltacloud.domain.HardwareParameter;
import org.jclouds.deltacloud.domain.HardwareProperty;
import org.jclouds.deltacloud.domain.RangeHardwareProperty;
import org.jclouds.deltacloud.domain.HardwareProperty.Kind;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class HardwarePropertyHandler extends ParseSax.HandlerWithResult<HardwareProperty> {
   private Kind kind;
   private String name;
   private String unit;
   private Object value;
   private HardwareParameter param;
   private Set<Object> availableValues = Sets.newLinkedHashSet();
   private Number first;
   private Number last;

   /**
    * resets state of the handler when called.
    * 
    * @return property or null
    */
   public HardwareProperty getResult() {
      try {
         switch (kind) {
         case FIXED:
            return new FixedHardwareProperty(name, unit, value);
         case ENUM:
            return new EnumHardwareProperty(name, unit, value, param, availableValues);
         case RANGE:
            return new RangeHardwareProperty(name, unit, (Number) value, param, first, last);
         default:
            return null;
         }
      } finally {
         this.kind = null;
         this.name = null;
         this.unit = null;
         this.value = null;
         this.param = null;
         this.availableValues = Sets.newLinkedHashSet();
         this.first = null;
         this.last = null;
      }
   }

   private static final Pattern LONG = Pattern.compile("^[0-9]+$");
   private static final Pattern DOUBLE = Pattern.compile("^[0-9]+\\.[0-9]+$");

   @Nullable public static
   Number parseNumberOrNull(String in) {
      if (DOUBLE.matcher(in).matches())
         return new Double(in);
      else if (LONG.matcher(in).matches())
         return Long.valueOf(in);
      return null;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("property")) {
         this.kind = Kind.fromValue(attributes.get("kind"));
         this.name = attributes.get("name");
         this.unit = attributes.get("unit");
         if (attributes.containsKey("value")) {
            this.value = stringOrNumber(attributes.get("value"));
         }
      } else if (qName.equals("param")) {
         this.param = new HardwareParameter(URI.create(attributes.get("href")), attributes.get("method"),
               attributes.get("name"), attributes.get("operation"));
      } else if (qName.equals("range")) {
         this.first = parseNumberOrNull(attributes.get("first"));
         this.last = parseNumberOrNull(attributes.get("last"));
      } else if (qName.equals("entry")) {
         this.availableValues.add(stringOrNumber(attributes.get("value")));
      }
   }

   public static Object stringOrNumber(String in) {
      Number number = parseNumberOrNull(in);
      return number != null ? number : in;
   }
}
