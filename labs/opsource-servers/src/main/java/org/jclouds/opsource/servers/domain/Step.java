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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * @author Kedar Dave
 */
@XmlRootElement(namespace = SERVER, name = "step")
public class Step {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromStep(this);
   }

   public static class Builder {

	  private String name;
	  private int number;
	  private int percentComplete;
	  
      public Builder name(String name) {
    	  this.name = name;
    	  return this;
      }

      public Builder number(int number) {
    	  this.number = number;
    	  return this;
      }

      public Builder percentComplete(int percentComplete) {
    	  this.percentComplete = percentComplete;
    	  return this;
      }

      public Step build() {
         return new Step(name, number, percentComplete);
      }

      public Builder fromStep(Step in) {
         return new Builder().name(name).number(number).percentComplete(percentComplete);
      }
   }

   private Step() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER, name="name")
   private String name;
   @XmlElement(namespace = SERVER, name="number")
   private int number;
   @XmlElement(namespace = SERVER, name="percentComplete")
   private int percentComplete;

   private Step(String name, int number, int percentComplete) {
      this.name = name;
      this.number = number;
      this.percentComplete = percentComplete;
   }

   	public String getName() {
   		return name;
   	}

	public int getNumber() {
		return number;
	}
	
	public int getPercentComplete() {
		return percentComplete;
	}
	
	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Step that = Step.class.cast(o);
		return equal(this, that);
	}

	@Override
	public int hashCode() {
      	return Objects.hashCode(name, number, percentComplete);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("name", name).add("number", number).add("percentComplete", percentComplete).toString();
	}

}
