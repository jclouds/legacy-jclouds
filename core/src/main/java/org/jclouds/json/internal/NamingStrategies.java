/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.json.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static org.jclouds.reflect.Reflection2.constructors;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.inject.Named;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
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
    * Specifies how to extract the name from an annotation for use in determining the serialized name.
    * 
    * @see com.google.gson.annotations.SerializedName
    * @see ExtractSerializedName
    */
   public abstract static class NameExtractor<A extends Annotation> implements Function<Annotation, String>,
         Supplier<Predicate<Annotation>> {
      protected final Class<A> annotationType;
      protected final Predicate<Annotation> predicate;

      protected NameExtractor(final Class<A> annotationType) {
         this.annotationType = checkNotNull(annotationType, "annotationType");
         this.predicate = new Predicate<Annotation>() {
            public boolean apply(Annotation input) {
               return input.getClass().equals(annotationType);
            }
         };
      }

      @SuppressWarnings("unchecked")
      public Class<Annotation> annotationType() {
         return (Class<Annotation>) annotationType;
      }

      @Override
      public String apply(Annotation in) {
         return extractName(annotationType.cast(in));
      }

      protected abstract String extractName(A cast);

      @Override
      public Predicate<Annotation> get() {
         return predicate;
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
      protected final Map<Class<? extends Annotation>, ? extends NameExtractor<?>> annotationToNameExtractor;
      protected final String forToString;

      public AnnotationBasedNamingStrategy(Iterable<? extends NameExtractor<?>> extractors) {
         checkNotNull(extractors, "means to extract names by annotations");

         this.annotationToNameExtractor = Maps.uniqueIndex(extractors,
               new Function<NameExtractor<?>, Class<? extends Annotation>>() {
                  @Override
                  public Class<? extends Annotation> apply(NameExtractor<?> input) {
                     return input.annotationType();
                  }
               });
         this.forToString = Joiner.on(",").join(transform(extractors, new Function<NameExtractor<?>, String>() {
            @Override
            public String apply(NameExtractor<?> input) {
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
   public static class AnnotationFieldNamingStrategy extends AnnotationBasedNamingStrategy implements
         FieldNamingStrategy {

      public AnnotationFieldNamingStrategy(Iterable<? extends NameExtractor<?>> extractors) {
         super(extractors);
         checkArgument(extractors.iterator().hasNext(), "you must supply at least one name extractor, for example: "
               + ExtractSerializedName.class.getSimpleName());
      }

      @Override
      public String translateName(Field f) {
         for (Annotation annotation : f.getAnnotations()) {
            if (annotationToNameExtractor.containsKey(annotation.annotationType())) {
               return annotationToNameExtractor.get(annotation.annotationType()).apply(annotation);
            }
         }
         return null;
      }
   }

   public static class AnnotationOrNameFieldNamingStrategy extends AnnotationFieldNamingStrategy implements
         FieldNamingStrategy {

      public AnnotationOrNameFieldNamingStrategy(Iterable<? extends NameExtractor<?>> extractors) {
         super(extractors);
      }

      @Override
      public String translateName(Field f) {
         String result = super.translateName(f);
         return result == null ? f.getName() : result;
      }
   }

   /**
    * Determines field naming from constructor annotations
    */
   public static final class AnnotationConstructorNamingStrategy extends AnnotationBasedNamingStrategy {
      private final Predicate<Invokable<?, ?>> hasMarker;
      private final Collection<? extends Class<? extends Annotation>> markers;

      public AnnotationConstructorNamingStrategy(Collection<? extends Class<? extends Annotation>> markers,
            Iterable<? extends NameExtractor<?>> extractors) {
         super(extractors);
         this.markers = checkNotNull(markers,
               "you must supply at least one annotation to mark deserialization constructors");
         this.hasMarker = hasAnnotationIn(markers);
      }

      private static Predicate<Invokable<?, ?>> hasAnnotationIn(
            final Collection<? extends Class<? extends Annotation>> markers) {
         return new Predicate<Invokable<?, ?>>() {
            public boolean apply(Invokable<?, ?> input) {
               return FluentIterable.from(Arrays.asList(input.getAnnotations()))
                     .transform(new Function<Annotation, Class<? extends Annotation>>() {
                        public Class<? extends Annotation> apply(Annotation input) {
                           return input.annotationType();
                        }
                     }).anyMatch(in(markers));
            }
         };
      }

      @VisibleForTesting
      <T> Invokable<T, T> getDeserializer(TypeToken<T> token) {
         return tryFind(constructors(token), hasMarker).orNull();
      }

      @VisibleForTesting
      <T> String translateName(Invokable<T, T> c, int index) {
         String name = null;

         if (markers.contains(ConstructorProperties.class) && c.getAnnotation(ConstructorProperties.class) != null) {
            String[] names = c.getAnnotation(ConstructorProperties.class).value();
            if (names != null && names.length > index) {
               name = names[index];
            }
         }

         for (Annotation annotation : c.getParameters().get(index).getAnnotations()) {
            if (annotationToNameExtractor.containsKey(annotation.annotationType())) {
               name = annotationToNameExtractor.get(annotation.annotationType()).apply(annotation);
               break;
            }
         }
         return name;
      }
   }
}
