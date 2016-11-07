#!/bin/bash
set -ev
cd $TRAVIS_BUILD_DIR/dynsem
mvn install
cd $TRAVIS_BUILD_DIR/dynsem2coq
mvn install
cd org.metaborg.meta.lang.dynsem.interpreter
mvn -Pstandalone test
