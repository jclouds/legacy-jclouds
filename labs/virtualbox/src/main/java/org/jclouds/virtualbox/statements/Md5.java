package org.jclouds.virtualbox.statements;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Md5 implements Statement {
	
	public static final Map<OsFamily, String> OS_TO_MD5 = ImmutableMap.of(
			OsFamily.UNIX,
		    "command -v md5sum >/dev/null 2>&1 && md5sum {filePath} | awk '{print $1}' || command -v md5 >/dev/null 2>&1 && md5  {filePath} | awk '{ print $4 }'", 
			OsFamily.WINDOWS, "TODO");
	
	
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
	      StringBuilder md5 = new StringBuilder();
	      md5.append(Utils.replaceTokens(OS_TO_MD5.get(family), ImmutableMap.of(
	            "filePath", filePath)));
	      return md5.toString();
	}

}
