#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.1
#
# Environment Variable Prerequisites
#
#   JAVA_HOME       Must point at your Java Development Kit install.
#   MAVEN_OPTS      (Optional) Java runtime options used when Maven is executed.
#   MAVEN_SKIP_RC   (Optional) Flag to disable loading of mavenrc files.
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then

  if [ -f /usr/local/etc/mavenrc ]; then
    . /usr/local/etc/mavenrc
  fi

  if [ -f /etc/mavenrc ]; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ]; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support. $var _must_ be set to either true or false.
cygwin=false
darwin=false
mingw=false
case "$(uname)" in
CYGWIN*) cygwin=true ;;
MINGW*) mingw=true ;;
Darwin*) darwin=true
  # Use /usr/libexec/java_home if available, otherwise fall back
  if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
      JAVA_HOME="$(/usr/libexec/java_home)"; export JAVA_HOME
    fi
  fi
  ;;
esac

if [ -z "$JAVA_HOME" ]; then
  if [ -r /etc/gentoo-release ]; then
    JAVA_HOME="$(java-config --jre-home)"
  fi
fi

# For Cygwin and MinGW, ensure paths are in Unix format
if $cygwin || $mingw; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
fi

if [ -z "$JAVA_HOME" ]; then
  JAVACMD="$(
    'set' +e
    'unset' -f command 2>/dev/null
    'command' -v java
  )" || :
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVACMD" ]; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

# traverses directory structure from process work directory to filesystem root
# first directory with .mvn subdirectory is considered project base directory
find_maven_basedir() {
  if [ -z "$1" ]; then
    echo "Path not specified to find_maven_basedir"
    return 1
  fi

  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ]; do
    if [ -d "$wdir"/.mvn ]; then
      basedir="$wdir"
      break
    fi
    if [ -d "${wdir}" ]; then
      wdir="$(cd "$wdir/.." && pwd)"
    fi
  done
  printf '%s' "$(cd "$basedir" && pwd -P)"
}

BASE_DIR="$(find_maven_basedir "$(dirname "$0")")"
if [ -z "$BASE_DIR" ]; then
  exit 1
fi

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-${BASE_DIR}}"
export MAVEN_PROJECTBASEDIR

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
wrapperJarPath="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ -r "$wrapperJarPath" ]; then
  if [ -n "$MVNW_VERBOSE" ]; then
    echo "Found $wrapperJarPath"
  fi
else
  if [ -n "$MVNW_VERBOSE" ]; then
    echo "Couldn't find $wrapperJarPath, downloading it ..."
  fi

  if [ -n "$MVNW_REPOURL" ]; then
    wrapperUrl="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.1/maven-wrapper-3.3.1.jar"
  else
    wrapperUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.1/maven-wrapper-3.3.1.jar"
  fi
  while IFS="=" read -r key value; do
    case "$key" in
    wrapperUrl)
      wrapperUrl="$value"
      break
      ;;
    esac
  done <"$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

  if [ -n "$MVNW_VERBOSE" ]; then
    echo "Downloading from: $wrapperUrl"
  fi

  if $cygwin; then
    wrapperJarPath="$(cygpath --path --windows "$wrapperJarPath")"
  fi

  if command -v wget >/dev/null; then
    QUIET="--quiet"
    if [ -n "$MVNW_VERBOSE" ]; then
      QUIET=""
    fi
    if ! wget $QUIET -O "$wrapperJarPath" "$wrapperUrl" -t 3; then
      rm -f "$wrapperJarPath"
      echo "Error: Failed to download Maven wrapper from $wrapperUrl" >&2
      exit 1
    fi
  elif command -v curl >/dev/null; then
    QUIET="--silent"
    if [ -n "$MVNW_VERBOSE" ]; then
      QUIET=""
    fi
    if ! curl $QUIET -o "$wrapperJarPath" "$wrapperUrl" -f -L; then
      rm -f "$wrapperJarPath"
      echo "Error: Failed to download Maven wrapper from $wrapperUrl" >&2
      exit 1
    fi
  else
    echo "Error: Cannot download Maven wrapper. wget or curl not found." >&2
    exit 1
  fi
fi
##########################################################################################
# End of extension
##########################################################################################

# If specified, validate the SHA-256 sum of the Maven wrapper jar file
wrapperSha256Sum=""
while IFS="=" read -r key value; do
  case "$key" in
  wrapperSha256Sum)
    wrapperSha256Sum="$value"
    break
    ;;
  esac
done <"$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
if [ -n "$wrapperSha256Sum" ]; then
  wrapperSha256Result=false
  if command -v sha256sum >/dev/null; then
    if echo "$wrapperSha256Sum  $wrapperJarPath" | sha256sum -c >/dev/null 2>&1; then
      wrapperSha256Result=true
    fi
  elif command -v shasum >/dev/null; then
    if echo "$wrapperSha256Sum  $wrapperJarPath" | shasum -a 256 -c >/dev/null 2>&1; then
      wrapperSha256Result=true
    fi
  fi
  if [ $wrapperSha256Result = false ]; then
    echo "Error: Failed to validate Maven wrapper SHA-256, your Maven wrapper might be tampered." >&2
    echo "Investigate or delete $wrapperJarPath to attempt a clean download." >&2
    echo "If you updated your Maven version, you need to update the specified wrapperSha256Sum property." >&2
    exit 1
  fi
fi

MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") $MAVEN_OPTS"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --path --windows "$JAVA_HOME")"
  [ -n "$CLASSPATH" ] && CLASSPATH="$(cygpath --path --windows "$CLASSPATH")"
  [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR="$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")"
fi

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $*"
export MAVEN_CMD_LINE_ARGS

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# shellcheck disable=SC2086
exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$wrapperJarPath" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
