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
package org.jclouds.aws.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * @author grkvlt@apache.org
 */
public class TagSetHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Map<String, String>> {
    private StringBuilder currentText = new StringBuilder();

    private ImmutableMap.Builder<String, String> result;
    private boolean inItem = false;
    private String key;
    private String value;

    public TagSetHandler() {
        super();
    }

    public Map<String, String> getResult() {
        return result.build();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (equalsOrSuffix(qName, "tagSet")) {
	        result = ImmutableMap.builder();
        } else if (qName.equals("item")) {
            inItem = true;
            key = null;
            value = null;
        }
        currentText = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            inItem = false;
            if (key != null) {
	            result.put(key, Strings.nullToEmpty(value));
            }
        }
        if (inItem) {
	        if (qName.equals("key")) {
	            key = currentOrNull(currentText);
	        } else if (qName.equals("value")) {
	            value = currentOrNull(currentText);
	        }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        currentText.append(ch, start, length);
    }
}
