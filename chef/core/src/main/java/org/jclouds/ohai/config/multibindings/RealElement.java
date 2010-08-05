

package org.jclouds.ohai.config.multibindings;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author jessewilson@google.com (Jesse Wilson)
 */
class RealElement implements Element {
  private static final AtomicInteger nextUniqueId = new AtomicInteger(1);

  private final int uniqueId;
  private final String setName;

  RealElement(String setName) {
    uniqueId = nextUniqueId.getAndIncrement();
    this.setName = setName;
  }

  public String setName() {
    return setName;
  }

  public int uniqueId() {
    return uniqueId;
  }

  public Class<? extends Annotation> annotationType() {
    return Element.class;
  }

  @Override public String toString() {
    return "@" + Element.class.getName() + "(setName=" + setName
        + ",uniqueId=" + uniqueId + ")";
  }

  @Override public boolean equals(Object o) {
    return o instanceof Element
        && ((Element) o).setName().equals(setName())
        && ((Element) o).uniqueId() == uniqueId();
  }

  @Override public int hashCode() {
    return 127 * ("setName".hashCode() ^ setName.hashCode())
        + 127 * ("uniqueId".hashCode() ^ uniqueId);
  }
}
