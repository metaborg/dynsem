#!/bin/bash
set -ev
export MAVEN_OPTS="-server -Xms512m -Xmx1024m -Xss16m"
cd $TRAVIS_BUILD_DIR/dynsem
mvn -Pstandalone install
cd $TRAVIS_BUILD_DIR/dynsem2coq
mvn -Pstandalone install
cd org.metaborg.meta.lang.dynsem.interpreter
mvn -Pstandalone test
