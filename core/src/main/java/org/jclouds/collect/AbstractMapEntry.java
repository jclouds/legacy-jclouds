package org.jclouds.collect;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.Objects;


public abstract class AbstractMapEntry<K, V> implements Entry<K, V> {

   public abstract K getKey();

   public abstract V getValue();

   public V setValue(V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(@Nullable Object object) {
      if (object instanceof Entry) {
         Entry<?, ?> that = (Entry<?, ?>) object;
         return Objects.equal(this.getKey(), that.getKey()) && Objects.equal(this.getValue(), that.getValue());
      }
      return false;
   }

   @Override
   public int hashCode() {
      K k = getKey();
      V v = getValue();
      return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
   }

   /**
    * Returns a string representation of the form <code>{key}={value}</code>.
    */
   @Override
   public String toString() {
      return getKey() + "=" + getValue();
   }
}
