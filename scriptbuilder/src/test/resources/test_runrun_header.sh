mkdir -p /tmp/$USER/scripttest

# create runscript header
(
cat <<END_OF_SCRIPT
#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
PROMPT_COMMAND='echo -ne "\033]0;yahooprod\007"'
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
export INSTANCE_NAME='yahooprod'
export JAVA_HOME='$JAVA_HOME'
END_OF_SCRIPT
) > /tmp/$USER/scripttest/yahooprod.sh

# add desired commands from the user
(
cat <<'END_OF_SCRIPT'
cd /tmp/$USER/scripttest
echo hello
echo $JAVA_HOME/bin/java -DinstanceName=$INSTANCE_NAME myServer.Main
END_OF_SCRIPT
) >> /tmp/$USER/scripttest/yahooprod.sh

# add runscript footer
(
cat <<'END_OF_SCRIPT'
exit 0
END_OF_SCRIPT
) >> /tmp/$USER/scripttest/yahooprod.sh

chmod u+x /tmp/$USER/scripttest/yahooprod.sh
