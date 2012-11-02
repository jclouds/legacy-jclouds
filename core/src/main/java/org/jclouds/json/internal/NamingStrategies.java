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
package org.jclouds.json.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.SerializedName;

/**
 * NamingStrategies used for JSON deserialization using GSON
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
public class NamingStrategies {
   /**
    * Specifies how to extract the name from an annotation for use in determining the serialized
    * name.
    *
    * @see com.google.gson.annotations.SerializedName
    * @see ExtractSerializedName
    */
   public abstract static class NameExtractor<A extends Annotation> {
      protected final Class<A> annotationType;

      protected NameExtractor(Class<A> annotationType) {
         this.annotationType = checkNotNull(annotationType, "annotationType");
      }

      public abstract String extractName(A in);

      public Class<A> annotationType() {
         return annotationType;
      }

      @Override
      public String toString() {
         return "nameExtractor(" + annotationType.getSimpleName() + ")";
      }

      @Override
      public int hashCode() {
         return annotationType.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         return annotationType.equals(NameExtractor.class.cast(obj).annotationType);
      }
   }

   public static class ExtractSerializedName extends NameExtractor<SerializedName> {
      public ExtractSerializedName() {
         super(SerializedName.class);
      }

      @Override
      public String extractName(SerializedName in) {
         return checkNotNull(in, "input annotation").value();
      }
   }

   public static class ExtractNamed extends NameExtractor<Named> {
      public ExtractNamed() {
         super(Named.class);
      }

      @Override
      public String extractName(Named in) {
         return checkNotNull(in, "input annotation").value();
      }
   }

   public abstract static class AnnotationBasedNamingStrategy {
      protected final Map<Class<? extends Annotation>, ? extends NameExtractor> annotationToNameExtractor;
      private String forToString;

      @SuppressWarnings("unchecked")
      public AnnotationBasedNamingStrategy(Iterable<? extends NameExtractor> extractors) {
         checkNotNull(extractors, "means to extract names by annotations");

         this.annotationToNameExtractor = Maps.uniqueIndex(extractors, new Function<NameExtractor, Class<? extends Annotation>>() {
            @Override
            public Class<? extends Annotation> apply(NameExtractor input) {
               return input.annotationType();
            }
         });
         this.forToString = Joiner.on(",").join(Iterables.transform(extractors, new Function<NameExtractor, String>() {
            @Override
            public String apply(NameExtractor input) {
               return input.annotationType().getName();
            }
         }));
      }

      @Override
      public String toString() {
         return "AnnotationBasedNamingStrategy requiring one of " + forToString;
      }
   }

   /**
    * Definition of field naming policy for annotation-based field
    */
   public static class AnnotationFieldNamingStrategy extends AnnotationBasedNamingStrategy implements FieldNamingStrategy {

      public AnnotationFieldNamingStrategy(Iterable<? extends NameExtractor> extractors) {
         super(extractors);
         checkArgument(extractors.iterator().hasNext(), "you must supply at least one name extractor, for example: "
               + ExtractSerializedName.class.getSimpleName());
      }

      @SuppressWarnings("unchecked")
      @Override
      public String translateName(Field f) {
         for (Annotation annotation : f.getAnnotations()) {
            if (annotationToNameExtractor.containsKey(annotation.annotationType())) {
               return annotationToNameExtractor.get(annotation.annotationType()).extractName(annotation);
            }
         }
         return null;
      }
   }

   public static class AnnotationOrNameFieldNamingStrategy extends AnnotationFieldNamingStrategy implements FieldNamingStrategy {
      public AnnotationOrNameFieldNamingStrategy(NameExtractor... extractors) {
         this(ImmutableSet.copyOf(extractors));
      }

      public AnnotationOrNameFieldNamingStrategy(Iterable<? extends NameExtractor> extractors) {
         super(extractors);
      }

      @Override
      public String translateName(Field f) {
         String result = super.translateName(f);
         return result == null ? f.getName() : result;
      }
   }

   public static interface ConstructorFieldNamingStrategy {
      public String translateName(Constructor<?> c, int index);

      public <T> Constructor<? super T> getDeserializationConstructor(Class<?> raw);

   }

   /**
    * Determines field naming from constructor annotations
    */
   public static class AnnotationConstructorNamingStrategy extends AnnotationBasedNamingStrategy implements ConstructorFieldNamingStrategy {
      private final Set<Class<? extends Annotation>> markers;

      public AnnotationConstructorNamingStrategy(Iterable<? extends Class<? extends Annotation>> markers, Iterable<? extends NameExtractor> extractors) {
         super(extractors);
         this.markers = ImmutableSet.copyOf(checkNotNull(markers, "you must supply at least one annotation to mark deserialization constructors"));
      }

      @SuppressWarnings("unchecked")
      public <T> Constructor<? super T> getDeserializationConstructor(Class<?> raw) {
         for (Constructor<?> ctor : raw.getDeclaredConstructors())
            for (Class<? extends Annotation> deserializationCtorAnnotation : markers)
               if (ctor.isAnnotationPresent(deserializationCtorAnnotation))
                  return (Constructor<T>) ctor;

         return null;
      }

      @SuppressWarnings("unchecked")
      @Override
      public String translateName(Constructor<?> c, int index) {
         String name = null;

         if (markers.contains(ConstructorProperties.class) && c.getAnnotation(ConstructorProperties.class) != null) {
            String[] names = c.getAnnotation(ConstructorProperties.class).value();
            if (names != null && names.length > index) {
               name = names[index];
            }
         }

         for (Annotation annotation : c.getParameterAnnotations()[index]) {
            if (annotationToNameExtractor.containsKey(annotation.annotationType())) {
               name = annotationToNameExtractor.get(annotation.annotationType()).extractName(annotation);
               break;
            }
         }
         return name;
      }
   }
}
