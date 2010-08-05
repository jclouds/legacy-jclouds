package org.jclouds.ohai.config.multibindings;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An internal binding annotation applied to each element in a multibinding.
 * All elements are assigned a globally-unique id to allow different modules
 * to contribute multibindings independently.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
@Retention(RUNTIME) @BindingAnnotation
@interface Element {
  String setName();
  int uniqueId();
}
