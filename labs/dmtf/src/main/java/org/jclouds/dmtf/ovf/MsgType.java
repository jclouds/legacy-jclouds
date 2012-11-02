/*
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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.base.Objects;


/**
 * Type for localizable string.
 *
 * Default string value
 * 
 * <pre>
 * &lt;complexType name="Msg_Type" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlType(name = "Msg_Type")
public class MsgType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromMsgType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {

      protected String value;
      protected String msgid;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see MsgType#getValue()
       */
      public B value(String value) {
         this.value = value;
         return self();
      }

      /**
       * @see MsgType#getMsgid()
       */
      public B msgid(String msgid) {
         this.msgid = msgid;
         return self();
      }

      public MsgType build() {
         return new MsgType(this);
      }

      public B fromMsgType(MsgType in) {
         return value(in.getValue()).msgid(in.getMsgid());
      }
   }

    @XmlValue
    protected String value;
    @XmlAttribute
    protected String msgid;

    private MsgType() {
       // JAXB
    }

    private MsgType(Builder<?> builder) {
       this.value = builder.value;
       this.msgid = builder.msgid;
    }

    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the msgid property.
     */
    public String getMsgid() {
        if (msgid == null) {
            return "";
        } else {
            return msgid;
        }
    }

    @Override
    public int hashCode() {
       return Objects.hashCode(value, msgid);
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       MsgType that = MsgType.class.cast(obj);
       return equal(this.value, that.value) &&
             equal(this.msgid, that.msgid);
    }

    @Override
    public String toString() {
       return Objects.toStringHelper("")
             .add("value", value).add("msgid", msgid).toString();
    }
}
