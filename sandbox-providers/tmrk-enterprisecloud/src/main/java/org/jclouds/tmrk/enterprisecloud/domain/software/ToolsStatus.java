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
package org.jclouds.tmrk.enterprisecloud.domain.software;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * <xs:simpleType name="ToolsStatus">
 * @author Jason King
 */
@XmlEnum
public enum ToolsStatus {
    @XmlEnumValue("NotInstalled")
    NOT_INSTALLED,
    @XmlEnumValue("NotRunning")
    NOT_RUNNING,
    @XmlEnumValue("OutOfDate")
    OUT_OF_DATE,
    @XmlEnumValue("Current")
    CURRENT;

    public String value() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    @Override
    public String toString() {
        return value();
    }
}
