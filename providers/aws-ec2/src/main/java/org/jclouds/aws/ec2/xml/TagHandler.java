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

import org.jclouds.aws.ec2.domain.Tag;
import org.jclouds.aws.ec2.util.TagFilters.ResourceType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

/**
 * @author grkvlt@apache.org
 */
public class TagHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Tag> {
    private StringBuilder currentText = new StringBuilder();

    private String resourceId;
    private ResourceType resourceType;
    private String key;
    private String value;

    public Tag getResult() {
        Tag returnVal = new Tag(resourceId, resourceType, key, value);
        return returnVal;
    }

    public void endElement(String uri, String name, String qName) throws SAXException {
        if (qName.equals("resourceId")) {
            this.resourceId = currentText.toString().trim();
        } else if (qName.equals("resourceType")) {
            resourceType = ResourceType.fromValue(currentText.toString().trim());
        } else if (qName.equals("key")) {
            key = currentText.toString().trim();
        } else if (qName.equals("value")) {
            value = currentText.toString().trim();
        }
        currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
        currentText.append(ch, start, length);
    }
}
