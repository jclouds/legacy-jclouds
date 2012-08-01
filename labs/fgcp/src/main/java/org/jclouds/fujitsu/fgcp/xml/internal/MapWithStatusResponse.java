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
package org.jclouds.fujitsu.fgcp.xml.internal;

import com.google.common.collect.ForwardingMap;

import javax.xml.bind.annotation.XmlElement;

/**
 * Special base class extending (forwardable) Map with fields for the elements
 * that FGCP XML responses specify.
 * <p>
 * This is useful for methods that return a set of elements that are better
 * represented as key value pairs.
 * 
 * @author Dies Koper
 */
public abstract class MapWithStatusResponse<K, V> extends ForwardingMap<K, V>
        implements StatusQuerable {
    @XmlElement(required = true)
    private String responseMessage;
    @XmlElement(required = true)
    private String responseStatus;

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public boolean isError() {
        return !"SUCCESS".equals(responseStatus);
    }

    @Override
    public String toString() {
        return "StatusResponse{" + "responseMessage='" + responseMessage + '\''
                + ", responseStatus='" + responseStatus + '\'' + '}';
    }
}
