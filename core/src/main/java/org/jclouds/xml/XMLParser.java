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
package org.jclouds.xml;

import java.io.IOException;

import org.jclouds.xml.internal.JAXBParser;

import com.google.inject.ImplementedBy;

/**
 * Parses XML documents.
 * 
 * @author Ignasi Barrera
 */
@ImplementedBy(JAXBParser.class)
public interface XMLParser {
   /** The default xml header. */
   public static final String DEFAULT_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

   /**
    * Serialize the object into xml.
    */
   public String toXML(Object src) throws IOException;

   /**
    * Serialize the object into xml, as the declared type.
    */
   public <T> String toXML(Object src, Class<T> type) throws IOException;

   /**
    * Deserialize the object from xml.
    */
   public <T> T fromXML(String xml, Class<T> type) throws IOException;

}
