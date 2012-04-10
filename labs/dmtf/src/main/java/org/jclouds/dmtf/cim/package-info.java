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
@XmlSchema(namespace = CIM_NS, elementFormDefault = XmlNsForm.QUALIFIED,
		xmlns = {
            @XmlNs(prefix = "cim", namespaceURI = CIM_NS),
            @XmlNs(prefix = "ovf", namespaceURI = OVF_NS),
		      @XmlNs(prefix = "vssd", namespaceURI = CIM_VSSD_NS),
		      @XmlNs(prefix = "rasd", namespaceURI = CIM_RASD_NS)
		}
)
@XmlAccessorType(XmlAccessType.FIELD)
package org.jclouds.dmtf.cim;

import static org.jclouds.dmtf.DMTFConstants.CIM_NS;
import static org.jclouds.dmtf.DMTFConstants.CIM_RASD_NS;
import static org.jclouds.dmtf.DMTFConstants.CIM_VSSD_NS;
import static org.jclouds.dmtf.DMTFConstants.OVF_NS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;


