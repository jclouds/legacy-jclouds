/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jclouds.codegen.ec2.queryapi.AmazonEC2QueryAPI;

import com.google.gson.Gson;
import com.google.common.io.Closeables;

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
public class AmazonEC2QueryAPIParser {

   public AmazonEC2QueryAPI parseJSONResource(String resource) {
      InputStream in = AmazonEC2QueryAPI.class.getResourceAsStream(resource);
      return parseJSONInputStream(in);
   }

   public AmazonEC2QueryAPI parseJSONFile(File file) throws FileNotFoundException {
      InputStream in = new FileInputStream(file);
      return parseJSONInputStream(in);
   }

   public AmazonEC2QueryAPI parseJSONInputStream(InputStream in) {
      assert in != null;
      Gson gson = new Gson();
      try {
         AmazonEC2QueryAPIValidator validator = new AmazonEC2QueryAPIValidator();
         validator.setModel(gson.fromJson(new InputStreamReader(in), AmazonEC2QueryAPI.class));
         validator.validateCategories().validateCommands().validateDomain();
         return validator.getModel();
      } finally {
         Closeables.closeQuietly(in);
      }
   }

}
