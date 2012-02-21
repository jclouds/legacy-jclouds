mkdir -p /tmp/$USER/scripttest

# create runscript header
cat > /tmp/$USER/scripttest/yahooprod.sh <<-'END_OF_JCLOUDS_SCRIPT'
	#!/bin/bash
	set +u
	shopt -s xpg_echo
	shopt -s expand_aliases
	
	PROMPT_COMMAND='echo -ne \"\033]0;yahooprod\007\"'
	export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin

	export INSTANCE_NAME='yahooprod'
END_OF_JCLOUDS_SCRIPT
cat >> /tmp/$USER/scripttest/yahooprod.sh <<-END_OF_JCLOUDS_SCRIPT
	export JAVA_HOME='$JAVA_HOME'
END_OF_JCLOUDS_SCRIPT

# add desired commands from the user
cat >> /tmp/$USER/scripttest/yahooprod.sh <<-'END_OF_JCLOUDS_SCRIPT'
	cd /tmp/$USER/scripttest
	echo hello
	
	cat >> /tmp/$USER/scripttest/temp.txt <<-'END_OF_JCLOUDS_FILE'
		hello world
	END_OF_JCLOUDS_FILE
	
	echo $JAVA_HOME/bin/java -DinstanceName=$INSTANCE_NAME myServer.Main
	
END_OF_JCLOUDS_SCRIPT

# add runscript footer
cat >> /tmp/$USER/scripttest/yahooprod.sh <<-'END_OF_JCLOUDS_SCRIPT'
	exit $?
	
END_OF_JCLOUDS_SCRIPT

chmod u+x /tmp/$USER/scripttest/yahooprod.sh
