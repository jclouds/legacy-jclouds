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
package org.jclouds.reflect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.reflect.Invokable;

/**
 * 
 * based on the {@link com.google.reflect.AccessibleObject} copied in as {@link com.google.reflect.Invokable} is package
 * private.
 * 
 * @author Adrian Cole
 * @since 1.6
 */
class Element extends AccessibleObject implements Member {

   private final AccessibleObject accessibleObject;
   protected final Member member;

   <M extends AccessibleObject & Member> Element( M member) {
      this.member = checkNotNull(member, "member");
      this.accessibleObject = member;
   }

   @Override
   public final boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
      return accessibleObject.isAnnotationPresent(annotationClass);
   }

   @Override
   public final <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
      return accessibleObject.getAnnotation(annotationClass);
   }

   @Override
   public final Annotation[] getAnnotations() {
      return accessibleObject.getAnnotations();
   }

   @Override
   public final Annotation[] getDeclaredAnnotations() {
      return accessibleObject.getDeclaredAnnotations();
   }

   @Override
   public final void setAccessible(boolean flag) throws SecurityException {
      accessibleObject.setAccessible(flag);
   }

   @Override
   public final boolean isAccessible() {
      return accessibleObject.isAccessible();
   }

   @Override
   public Class<?> getDeclaringClass() {
      return member.getDeclaringClass();
   }

   @Override
   public final String getName() {
      return member.getName();
   }

   @Override
   public final int getModifiers() {
      return member.getModifiers();
   }

   @Override
   public final boolean isSynthetic() {
      return member.isSynthetic();
   }

   /** Returns true if the element is public. */
   public final boolean isPublic() {
      return Modifier.isPublic(getModifiers());
   }

   /** Returns true if the element is protected. */
   public final boolean isProtected() {
      return Modifier.isProtected(getModifiers());
   }

   /** Returns true if the element is package-private. */
   public final boolean isPackagePrivate() {
      return !isPrivate() && !isPublic() && !isProtected();
   }

   /** Returns true if the element is private. */
   public final boolean isPrivate() {
      return Modifier.isPrivate(getModifiers());
   }

   /** Returns true if the element is static. */
   public final boolean isStatic() {
      return Modifier.isStatic(getModifiers());
   }

   /**
    * Returns {@code true} if this method is final, per {@code Modifier.isFinal(getModifiers())}.
    * 
    * <p>
    * Note that a method may still be effectively "final", or non-overridable when it has no {@code final} keyword. For
    * example, it could be private, or it could be declared by a final class. To tell whether a method is overridable,
    * use {@link Invokable#isOverridable}.
    */
   public final boolean isFinal() {
      return Modifier.isFinal(getModifiers());
   }

   /** Returns true if the method is abstract. */
   public final boolean isAbstract() {
      return Modifier.isAbstract(getModifiers());
   }

   /** Returns true if the element is native. */
   public final boolean isNative() {
      return Modifier.isNative(getModifiers());
   }

   /** Returns true if the method is synchronized. */
   public final boolean isSynchronized() {
      return Modifier.isSynchronized(getModifiers());
   }

   /** Returns true if the field is volatile. */
   final boolean isVolatile() {
      return Modifier.isVolatile(getModifiers());
   }

   /** Returns true if the field is transient. */
   final boolean isTransient() {
      return Modifier.isTransient(getModifiers());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (obj instanceof Element) {
         Element that = (Element) obj;
         return member.equals(that.member);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return member.hashCode();
   }

   @Override
   public String toString() {
      return member.toString();
   }
}
