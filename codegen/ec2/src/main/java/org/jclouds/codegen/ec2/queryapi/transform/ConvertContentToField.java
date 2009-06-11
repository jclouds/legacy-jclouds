/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.transform;

import org.jclouds.codegen.ec2.queryapi.Content;
import org.jclouds.codegen.model.Field;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Converts object models representing AWS API beans into Java classes.
 * <p>
 * This implementation is designed to perform the following steps:
 * <ul>
 * <li>Parse the JSON object representation produced by the <tt>parse_ec2.pl</tt> perl script</li>
 * <li>Convert the JSON into Java object models (@see org.jclouds.aws.codegen.models)</li>
 * 
 * @author Adrian Cole
 */
public class ConvertContentToField implements Function<Content, Field> {

   public Field apply(Content from) {
      Field field = new Field();
      field.setConstraints(from.getConstraints());
      field.setDefaultValue(from.getDefaultValue());
      field.setDesc(from.getDesc());
      field.setName(from.getName());
      field.setJavaName(parseJavaName(from.getName()));
      field.setOptional(Boolean.parseBoolean(from.getOptional()));
      field.setType(from.getType());
      field.setValueMap(from.getValueMap());
      field.setJavaType(parseJavaType(from.getType()));
      return field;
   }

   @VisibleForTesting
   String parseJavaType(String type) {
      if (type.indexOf("xsd:") >= 0) {
         return parseJavaTypeForXsd(type);
      }
      return type;
   }

   @VisibleForTesting
   String parseJavaTypeForXsd(String type) {
      if (type.equals("xsd:string")) {
         return "String";
      } else if (type.equals("xsd:Int")) {
         return "Integer";
      } else if (type.equals("xsd:boolean")) {
         return "Boolean";
      } else if (type.equals("xsd:dateTime")) {
         return "org.joda.time.DateTime";
      } else {
         throw new IllegalArgumentException("type not supported: " + type);
      }

   }

   @VisibleForTesting
   String parseJavaName(String name) {
      return name;
   }

}
