package org.jclouds.glesys.domain;

/**
 * Wrapping booleans for the time being (gson won't allow TypeAdapter&lt;Boolean&gt;)
 */
public class GleSYSBoolean {
   private boolean value;

   public GleSYSBoolean(boolean value) {
      this.value = value;
   }

   public boolean getValue() {
      return value;
   }
}
