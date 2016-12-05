#!/bin/bash
set -ev
cd $TRAVIS_BUILD_DIR/dynsem
mvn -Pstandalone install
cd $TRAVIS_BUILD_DIR/dynsem2coq
mvn -Pstandalone install
cd $TRAVIS_BUILD_DIR/org.metaborg.meta.lang.dynsem.interpreter
mvn -Pstandalone test
cd $TRAVIS_BUILD_DIR/metaborg-sl/org.metaborg.lang.sl
mvn -Pstandalone test
cd $TRAVIS_BUILD_DIR/metaborg-sl/org.metaborg.lang.sl.interp
mvn -Pstandalone test
