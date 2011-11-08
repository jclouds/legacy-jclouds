package org.jclouds.trmk.enterprisecloud.domain;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseNamedResource;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class Action extends BaseNamedResource<Action> {
   public static enum ActionDisabled {
      /**
       * The actionDisabled attribute will have a value of noAccess when a user
       * does not have permission to perform the action. For example, for a user
       * with read-only access, all actions have actionDisabled="noAccess" set.
       */
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
      public Builder fromNamedResource(BaseNamedResource<Action> in) {
         return Builder.class.cast(super.fromNamedResource(in));
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

   protected final ActionDisabled actionDisabled;

   public Action(URI href, String type, String name, @Nullable ActionDisabled actionDisabled) {
      super(href, type, name);
      this.actionDisabled = actionDisabled;
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

}