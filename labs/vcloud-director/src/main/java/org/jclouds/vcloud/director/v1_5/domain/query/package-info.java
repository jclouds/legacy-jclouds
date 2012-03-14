/*
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
@XmlSchema(namespace = VCLOUD_1_5_NS,
      location = "http://vcloudbeta.bluelock.com/api/v1.5/schema/master.xsd",
      elementFormDefault = XmlNsForm.QUALIFIED,
      xmlns = {
            @XmlNs(prefix = "", namespaceURI = VCLOUD_1_5_NS),
            @XmlNs(prefix = "cim", namespaceURI = VCLOUD_CIM_NS),
            @XmlNs(prefix = "ovf", namespaceURI = VCLOUD_OVF_NS),
            @XmlNs(prefix = "env", namespaceURI = VCLOUD_OVF_ENV_NS)
      }
)
@XmlAccessorType(XmlAccessType.FIELD)
package org.jclouds.vcloud.director.v1_5.domain.query;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_CIM_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_ENV_NS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

