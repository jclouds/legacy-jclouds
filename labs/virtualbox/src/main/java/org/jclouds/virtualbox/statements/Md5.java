package org.jclouds.virtualbox.statements;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableList;

public class Md5 implements Statement {
	
	private String filePath;
	
	public Md5(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public Iterable<String> functionDependencies(OsFamily family) {
	      return ImmutableList.of();
	}

	@Override
	public String render(OsFamily family) {
	      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
	          throw new UnsupportedOperationException("windows not yet implemented");
	      return ScriptBuilder.call("calculateMd5").render(family);
	}

}
