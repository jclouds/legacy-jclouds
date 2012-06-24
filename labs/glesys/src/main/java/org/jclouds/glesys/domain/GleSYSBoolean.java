package org.jclouds.glesys.domain;

/**
 * Wrapping booleans for the time being (gson won't allow TypeAdapter&lt;Boolean&gt;)
 */
public class GleSYSBoolean {
   public static final GleSYSBoolean TRUE = new GleSYSBoolean(true);
   public static final GleSYSBoolean FALSE = new GleSYSBoolean(false);

   private boolean value;

   public GleSYSBoolean(boolean value) {
      this.value = value;
   }

   public boolean getValue() {
      return value;
   }
}
