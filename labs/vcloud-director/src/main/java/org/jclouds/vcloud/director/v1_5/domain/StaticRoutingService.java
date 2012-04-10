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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Represents Static Routing network service.
 * <p/>
 * <p/>
 * <p>Java class for StaticRoutingService complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="StaticRoutingService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkServiceType">
 *       &lt;sequence>
 *         &lt;element name="StaticRoute" type="{http://www.vmware.com/vcloud/v1.5}StaticRouteType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "StaticRoutingService")
@XmlType(propOrder = {
      "staticRoutes"
})
public class StaticRoutingService extends NetworkServiceType<StaticRoutingService> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromStaticRoutingService(this);
   }

   public static class Builder extends NetworkServiceType.Builder<StaticRoutingService> {

      private List<StaticRoute> staticRoutes = ImmutableList.of();

      /**
       * @see StaticRoutingService#getStaticRoutes()
       */
      public Builder staticRoutes(List<StaticRoute> staticRoutes) {
         this.staticRoutes = ImmutableList.copyOf(checkNotNull(staticRoutes, "staticRoutes"));
         return this;
      }


      @Override
      public StaticRoutingService build() {
         return new StaticRoutingService(isEnabled, staticRoutes);
      }


      @Override
      public Builder fromNetworkServiceType(NetworkServiceType<StaticRoutingService> in) {
         return Builder.class.cast(super.fromNetworkServiceType(in));
      }

      public Builder fromStaticRoutingService(StaticRoutingService in) {
         return fromNetworkServiceType(in)
               .staticRoutes(in.getStaticRoutes());
      }

      @Override
      public Builder enabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }
   }

   private StaticRoutingService(boolean enabled, List<StaticRoute> staticRoutes) {
      super(enabled);
      this.staticRoutes = ImmutableList.copyOf(staticRoutes);
   }

   private StaticRoutingService() {
      // for JAXB
   }

   @XmlElement(name = "StaticRoute")
   protected List<StaticRoute> staticRoutes = Lists.newArrayList();

   /**
    * Gets the value of the staticRoutes property.
    */
   public List<StaticRoute> getStaticRoutes() {
      return Collections.unmodifiableList(this.staticRoutes);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      StaticRoutingService that = StaticRoutingService.class.cast(o);
      return equal(staticRoutes, that.staticRoutes);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(staticRoutes);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("staticRoutes", staticRoutes).toString();
   }

}
