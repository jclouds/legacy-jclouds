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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

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

   public static final class Rel {
      public static final String ADD = "add";
      public static final String ALTERNATE = "alternate";
      public static final String CATALOG_ITEM = "catalogItem";
      public static final String COLLABORATION_ABORT = "collaboration:abort";
      public static final String COLLABORATION_FAIL = "collaboration:fail";
      public static final String COLLABORATION_RESUME = "collaboration:resume";
      public static final String CONSOLIDATE = "consolidate";
      public static final String CONTROL_ACCESS = "controlAccess";
      public static final String COPY = "copy";
      public static final String DEPLOY = "deploy";
      public static final String DISABLE = "disable";
      public static final String DISCARD_STATE = "discardState";
      public static final String DOWN = "down";
      public static final String DOWNLOAD_ALTERNATE = "download:alternate";
      public static final String DOWNLOAD_DEFAULT = "download:default";
      public static final String EDIT = "edit";
      public static final String ENABLE = "enable";
      public static final String FIRST_PAGE = "firstPage";
      public static final String INSTALL_VMWARE_TOOLS = "installVmwareTools";
      public static final String LAST_PAGE = "lastPage";
      public static final String EJECT_MEDIA = "media:ejectMedia";
      public static final String INSERT_MEDIA = "media:insertMedia";
      public static final String MOVE = "move";
      public static final String NEXT_PAGE = "nextPage";
      public static final String OVA = "ova";
      public static final String OVF = "ovf";
      public static final String POWER_OFF = "power:powerOff";
      public static final String POWER_ON = "power:powerOn";
      public static final String REBOOT = "power:reboot";
      public static final String RESET = "power:reset";
      public static final String SHUTDOWN = "power:shutdown";
      public static final String SUSPEND = "power:suspend";
      public static final String PREVIOUS_PAGE = "previousPage";
      public static final String PUBLISH = "publish";
      public static final String RECOMPOSE = "recompose";
      public static final String RECONNECT = "reconnect";
      public static final String REGISTER = "register";
      public static final String REJECT = "reject";
      public static final String RELOCATE = "relocate";
      public static final String REMOVE = "remove";
      public static final String REPAIR = "repair";
      public static final String SCREEN_ACQUIRE_TICKET = "screen:acquireTicket";
      public static final String SCREEN_THUMBNAIL = "screen:thumbnail";
      public static final String TASK_CANCEL = "task:cancel";
      public static final String BLOCKING_TASK = "blockingTask";
      public static final String TASK_OWNER = "taskOwner";
      public static final String TASK_PARAMS = "taskParams";
      public static final String TASK_REQUEST = "taskRequest";
      public static final String UNDEPLOY = "undeploy";
      public static final String UNLOCK = "unlock";
      public static final String UNREGISTER = "unregister";
      public static final String UP = "up";
      public static final String UPDATE_PROGRESS = "updateProgress";
      public static final String UPGRADE = "upgrade";
      public static final String UPLOAD_ALTERNATE = "upload:alternate";
      public static final String UPLOAD_DEFAULT = "upload:default";

      /**
       * All acceptable {@link Link#getRel()} values.
       *
       * This list must be updated whenever a new relationship is added.
       */
      public static final List<String> ALL = Arrays.asList(
            ADD, ALTERNATE, CATALOG_ITEM, COLLABORATION_ABORT,
            COLLABORATION_FAIL, COLLABORATION_RESUME, CONSOLIDATE,
            CONTROL_ACCESS, COPY, DEPLOY, DISABLE, DISCARD_STATE, DOWN,
            DOWNLOAD_ALTERNATE, DOWNLOAD_DEFAULT, EDIT, ENABLE, FIRST_PAGE,
            INSTALL_VMWARE_TOOLS, LAST_PAGE, EJECT_MEDIA, INSERT_MEDIA, MOVE,
            NEXT_PAGE, OVA, OVF, POWER_OFF, POWER_ON, REBOOT, RESET, SHUTDOWN,
            SUSPEND, PREVIOUS_PAGE, PUBLISH, RECOMPOSE, RECONNECT, REGISTER,
            REJECT, RELOCATE, REMOVE, REPAIR, SCREEN_ACQUIRE_TICKET,
            SCREEN_THUMBNAIL, TASK_CANCEL, BLOCKING_TASK, TASK_OWNER,
            TASK_PARAMS, TASK_REQUEST, UNDEPLOY, UNLOCK, UNREGISTER, UP,
            UPDATE_PROGRESS, UPGRADE, UPLOAD_ALTERNATE, UPLOAD_DEFAULT
      );
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromLink(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends Reference.Builder<B> {

      private String rel;

      /**
       * @see Link#getRel()
       */
      public B rel(String rel) {
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
   private String rel;

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
   public String getRel() {
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
