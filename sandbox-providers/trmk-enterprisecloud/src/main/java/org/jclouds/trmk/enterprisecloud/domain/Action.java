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
package org.jclouds.trmk.enterprisecloud.domain;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Cole
 * 
 */
@XmlRootElement(name = "Action")
public class Action extends BaseNamedResource<Action> {
    @XmlEnum
    public static enum ActionDisabled {
      /**
       * The actionDisabled attribute will have a value of noAccess when a user
       * does not have permission to perform the action. For example, for a user
       * with read-only access, all actions have actionDisabled="noAccess" set.
       */
      @XmlEnumValue("noAccess")
      NO_ACCESS,
      /**
       * The attribute will have a value of disabled when the action is contrary
       * to business rules. For example, the action virtual machine with
       * name="power:powerOff" has actionDisabled="disabled" when the virtual
       * machine is currently powered off; a virtual machine, which is currently
       * off, may not be powered off; it may only be powered on. If both
       * conditions apply, actions have actionDisabled="noAccess" set. If
       * neither condition applies, the attribute will not appear.
       */
      @XmlEnumValue("disabled")
      DISABLED,
      /**
       * ActionDisabled was not parsed by jclouds.
       */
      UNRECOGNIZED;

      public String value() {
         return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static ActionDisabled fromValue(String actionDisabled) {
         try {
            return valueOf(LOWER_CAMEL.to(UPPER_UNDERSCORE, checkNotNull(actionDisabled, "actionDisabled")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromAction(this);
   }

   public static class Builder extends BaseNamedResource.Builder<Action> {

      protected ActionDisabled actionDisabled;

      /**
       * @see Action#getActionDisabled
       */
      public Builder actionDisabled(ActionDisabled actionDisabled) {
         this.actionDisabled = actionDisabled;
         return this;
      }

      @Override
      public Action build() {
         return new Action(href, type, name, actionDisabled);
      }

      public Builder fromAction(Action in) {
         return fromNamedResource(in).actionDisabled(in.getActionDisabled());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<Action> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<Action> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         if (attributes.containsKey("actionDisabled"))
            actionDisabled(ActionDisabled.fromValue(attributes.get("actionDisabled")));
         return this;
      }

   }

   @XmlAttribute
   protected ActionDisabled actionDisabled;

   public Action(URI href, String type, String name, @Nullable ActionDisabled actionDisabled) {
      super(href, type, name);
      this.actionDisabled = actionDisabled;
   }

   protected Action() {
       //For JAXB
   }
   /**
    * The attribute actionDisabled appears only when the example has an action
    * disabled for business rules.
    * 
    * @return
    */
   @Nullable
   public ActionDisabled getActionDisabled() {
      return actionDisabled;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Action action = (Action) o;

        if (actionDisabled != action.actionDisabled) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (actionDisabled != null ? actionDisabled.hashCode() : 0);
        return result;
    }

    @Override
    public String string() {
        return super.string()+", actionDisabled="+actionDisabled;
    }
}