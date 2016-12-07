#!/bin/bash
set -ev
cd $TRAVIS_BUILD_DIR/dynsem
mvn -U -Pstandalone install
cd $TRAVIS_BUILD_DIR/dynsem2coq
mvn -U -Pstandalone install
cd $TRAVIS_BUILD_DIR/org.metaborg.meta.lang.dynsem.interpreter
mvn -U -Pstandalone install
cd $TRAVIS_BUILD_DIR/metaborg-sl/org.metaborg.lang.sl
mvn -U -Pstandalone install
cd $TRAVIS_BUILD_DIR/metaborg-sl/org.metaborg.lang.sl.interp
mvn -U -Pstandalone install
