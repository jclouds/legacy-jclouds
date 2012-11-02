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

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * A link.
 * <p/>
 * <pre>
 * &lt;xs:complexType name="LinkType"&gt;
 * </pre>
 *
 * @author Adrian Cole
 */
@XmlRootElement(name = "Link")
public class Link extends Reference {
   
   @XmlType
   @XmlEnum(String.class)
   public static enum Rel {
      @XmlEnumValue("add") ADD("add"),
      @XmlEnumValue("alternate") ALTERNATE("alternate"),
      @XmlEnumValue("catalogItem") CATALOG_ITEM("catalogItem"),
      @XmlEnumValue("collaboration:abort") COLLABORATION_ABORT("collaboration:abort"),
      @XmlEnumValue("collaboration:fail") COLLABORATION_FAIL("collaboration:fail"),
      @XmlEnumValue("collaboration:resume") COLLABORATION_RESUME("collaboration:resume"),
      @XmlEnumValue("consolidate") CONSOLIDATE("consolidate"),
      @XmlEnumValue("controlAccess") CONTROL_ACCESS("controlAccess"),
      @XmlEnumValue("copy") COPY("copy"),
      @XmlEnumValue("deploy") DEPLOY("deploy"),
      @XmlEnumValue("disable") DISABLE("disable"),
      @XmlEnumValue("discardState") DISCARD_STATE("discardState"),
      @XmlEnumValue("down") DOWN("down"),
      @XmlEnumValue("download:alternate") DOWNLOAD_ALTERNATE("download:alternate"),
      @XmlEnumValue("download:default") DOWNLOAD_DEFAULT("download:default"),
      @XmlEnumValue("edit") EDIT("edit"),
      @XmlEnumValue("enable") ENABLE("enable"),
      @XmlEnumValue("entityResolver") ENTITY_RESOLVER("entityResolver"),
      @XmlEnumValue("firstPage") FIRST_PAGE("firstPage"),
      @XmlEnumValue("installVmwareTools") INSTALL_VMWARE_TOOLS("installVmwareTools"),
      @XmlEnumValue("lastPage") LAST_PAGE("lastPage"),
      @XmlEnumValue("media:ejectMedia") EJECT_MEDIA("media:ejectMedia"),
      @XmlEnumValue("media:insertMedia") INSERT_MEDIA("media:insertMedia"),
      @XmlEnumValue("move") MOVE("move"),
      @XmlEnumValue("nextPage") NEXT_PAGE("nextPage"),
      @XmlEnumValue("ova") OVA("ova"),
      @XmlEnumValue("ovf") OVF("ovf"),
      @XmlEnumValue("power:powerOff") POWER_OFF("power:powerOff"),
      @XmlEnumValue("power:powerOn") POWER_ON("power:powerOn"),
      @XmlEnumValue("power:reboot") REBOOT("power:reboot"),
      @XmlEnumValue("power:reset") RESET("power:reset"),
      @XmlEnumValue("power:shutdown") SHUTDOWN("power:shutdown"),
      @XmlEnumValue("power:suspend") SUSPEND("power:suspend"),
      @XmlEnumValue("previousPage") PREVIOUS_PAGE("previousPage"),
      @XmlEnumValue("publish") PUBLISH("publish"),
      @XmlEnumValue("recompose") RECOMPOSE("recompose"),
      @XmlEnumValue("reconnect") RECONNECT("reconnect"),
      @XmlEnumValue("register") REGISTER("register"),
      @XmlEnumValue("reject") REJECT("reject"),
      @XmlEnumValue("relocate") RELOCATE("relocate"),
      @XmlEnumValue("remove") REMOVE("remove"),
      @XmlEnumValue("screen:acquireTicket") SCREEN_ACQUIRE_TICKET("screen:acquireTicket"),
      @XmlEnumValue("screen:thumbnail") SCREEN_THUMBNAIL("screen:thumbnail"),
      @XmlEnumValue("syncSyslogSettings") SYNC_SYSLOG_SETTINGS("syncSyslogSettings"),
      @XmlEnumValue("task:cancel") TASK_CANCEL("task:cancel"),
      @XmlEnumValue("blockingTask") BLOCKING_TASK("blockingTask"),
      @XmlEnumValue("taskOwner") TASK_OWNER("taskOwner"),
      @XmlEnumValue("taskParams") TASK_PARAMS("taskParams"),
      @XmlEnumValue("taskRequest") TASK_REQUEST("taskRequest"),
      @XmlEnumValue("undeploy") UNDEPLOY("undeploy"),
      @XmlEnumValue("unlock") UNLOCK("unlock"),
      @XmlEnumValue("unregister") UNREGISTER("unregister"),
      @XmlEnumValue("up") UP("up"),
      @XmlEnumValue("updateProgress") UPDATE_PROGRESS("updateProgress"),
      @XmlEnumValue("upgrade") UPGRADE("upgrade"),
      @XmlEnumValue("upload:alternate") UPLOAD_ALTERNATE("upload:alternate"),
      @XmlEnumValue("upload:default") UPLOAD_DEFAULT("upload:default"),
      @XmlEnumValue("repair") REPAIR("repair"),

      UNRECOGNIZED("unrecognized");
      
      public static final List<Rel> ALL = ImmutableList.of(
            ADD, ALTERNATE, CATALOG_ITEM, COLLABORATION_ABORT,
            COLLABORATION_FAIL, COLLABORATION_RESUME, CONSOLIDATE,
            CONTROL_ACCESS, COPY, DEPLOY, DISABLE, DISCARD_STATE, DOWN,
            DOWNLOAD_ALTERNATE, DOWNLOAD_DEFAULT, EDIT, ENABLE, ENTITY_RESOLVER, FIRST_PAGE,
            INSTALL_VMWARE_TOOLS, LAST_PAGE, EJECT_MEDIA, INSERT_MEDIA, MOVE,
            NEXT_PAGE, OVA, OVF, POWER_OFF, POWER_ON, REBOOT, RESET, SHUTDOWN,
            SUSPEND, PREVIOUS_PAGE, PUBLISH, RECOMPOSE, RECONNECT, REGISTER,
            REJECT, RELOCATE, REMOVE, REPAIR, SCREEN_ACQUIRE_TICKET,
            SCREEN_THUMBNAIL, SYNC_SYSLOG_SETTINGS, TASK_CANCEL, BLOCKING_TASK, TASK_OWNER,
            TASK_PARAMS, TASK_REQUEST, UNDEPLOY, UNLOCK, UNREGISTER, UP,
            UPDATE_PROGRESS, UPGRADE, UPLOAD_ALTERNATE, UPLOAD_DEFAULT,
            UPLOAD_DEFAULT);

      protected final String stringValue;

      Rel(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, Rel> REL_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(Rel.values()), new Function<Rel, String>() {
               @Override
               public String apply(Rel input) {
                  return input.stringValue;
               }
            });

      public static Rel fromValue(String value) {
         Rel rel = REL_BY_ID.get(checkNotNull(value, "stringValue"));
         return rel == null ? UNRECOGNIZED : rel;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromLink(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends Reference.Builder<B> {

      private Rel rel;

      /**
       * @see Link#getRel()
       */
      public B rel(String rel) {
         this.rel = Rel.fromValue(rel);
         return self();
      }

      /**
       * @see Link#getRel()
       */
      public B rel(Rel rel) {
         this.rel = rel;
         return self();
      }

      @Override
      public Link build() {
         return new Link(this);
      }

      public B fromLink(Link in) {
         return fromReference(in).rel(in.getRel());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         rel(attributes.get("rel"));
         return self();
      }
   }

   @XmlAttribute(required = true)
   private Rel rel;

   protected Link(Builder<?> builder) {
      super(builder);
      this.rel = checkNotNull(builder.rel, "rel");
   }

   protected Link() {
      // For JAXB
   }

   /**
    * Defines the relationship of the link to the object that contains it. A relationship can be the
    * name of an operation on the object, a reference to a contained or containing object, or a
    * reference to an alternate representation of the object. The relationship value implies the
    * HTTP verb to use when you use the link's href value as a request URL.
    *
    * @return relationship of the link to the object that contains it.
    */
   public Rel getRel() {
      return rel;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Link that = (Link) o;
      return super.equals(that) && equal(this.rel, that.rel);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), rel);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("rel", rel);
   }
}
