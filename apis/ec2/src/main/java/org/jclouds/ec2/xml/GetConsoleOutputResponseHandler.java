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

import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.functions.ParseSax;

import com.google.common.base.Charsets;

/**
 * @author Andrew Kennedy
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetConsoleOutput.html">
 *       ApiReference query GetConsoleOutput</a>
 */
public class GetConsoleOutputResponseHandler extends ParseSax.HandlerWithResult<String> {

    private StringBuilder currentText = new StringBuilder();
    private String output;

    @Override
    public String getResult() {
       return output;
    }

    @Override
    public void endElement(String uri, String name, String qName) {
       if (qName.equalsIgnoreCase("output")) {
          this.output = new String(Base64.decode(currentText.toString().trim()), Charsets.UTF_8);
       }
       currentText = new StringBuilder();
    }

    @Override
    public void characters(char ch[], int start, int length) {
       currentText.append(ch, start, length);
    }
}
