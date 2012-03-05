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

package org.jclouds.virtualbox.domain;

import org.jclouds.compute.domain.Template;

public class NodeSpec {

  private final Master   master;
  private final String   name;
  private final String   tag;
  private final Template template;

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Master   master;
    private String   name;
    private String   tag;
    private Template template;

    public Builder master(Master master) {
      this.master = master;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder tag(String tag) {
      this.tag = tag;
      return this;
    }

    public Builder template(Template template) {
      this.template = template;
      return this;
    }

    public NodeSpec build() {
      return new NodeSpec(master, name, tag, template);
    }

  }

  private NodeSpec(Master master, String name, String tag, Template template) {
    super();
    this.master = master;
    this.name = name;
    this.tag = tag;
    this.template = template;
  }

  public Master getMaster() {
    return master;
  }

  public String getName() {
    return name;
  }

  public String getTag() {
    return tag;
  }

  public Template getTemplate() {
    return template;
  }

}
