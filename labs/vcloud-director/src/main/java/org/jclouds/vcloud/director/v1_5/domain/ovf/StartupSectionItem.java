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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for Item element declaration.
 *
 * <pre>
 * &lt;element name="Item">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *         &lt;attribute name="order" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *         &lt;attribute name="startDelay" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" default="0" />
 *         &lt;attribute name="waitingForGuest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *         &lt;attribute name="stopDelay" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" default="0" />
 *         &lt;attribute name="startAction" type="{http://www.w3.org/2001/XMLSchema}string" default="powerOn" />
 *         &lt;attribute name="stopAction" type="{http://www.w3.org/2001/XMLSchema}string" default="powerOff" />
 *         &lt;anyAttribute processContents='lax'/>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 */
@XmlType
@XmlRootElement(name = "Item")
public class StartupSectionItem extends Item {
   
   // TODO Builder

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      StartupSectionItem that = StartupSectionItem.class.cast(obj);
      return super.equals(that);
   }

}
