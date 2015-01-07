#!/bin/sh
: <<'END'
if  type -p java; then
    _java=java
    echo 'Found locally installed java in PATH'
elif [[ -n ${JAVA_HOME} ]] && [[ -x ${JAVA_HOME}/bin/java ]]; then
    echo 'Found locally installed java in JAVA_HOME'
    _java=${JAVA_HOME}/bin/java
else
    echo 'No local java installed'
fi

if [ -z ${_java} ] && [ -d ${_java} ]; then
    version=$(${_java} -version 2>&1 |sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q')
    if [ $version -lt 15 ]; then
        echo 'System java is outdated!'
        unset ${_java}
    else
        echo 'System java is up to date!'
    fi
fi
END

pushd `dirname $0` > /dev/null
DIR=`pwd -P`
popd > /dev/null

if [ -z ${_java} ]; then
    echo 'Using packaged java'
    _java=${DIR}/jre/bin/java
fi

${_java} -jar ${DIR}/Updater.jar