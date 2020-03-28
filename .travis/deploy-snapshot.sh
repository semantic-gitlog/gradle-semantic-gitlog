#!/bin/bash

set -e

cd ${TRAVIS_BUILD_DIR}

# Prepare the local keyring (requires travis to have decrypted the file
# beforehand)
gpg --fast-import .travis/secret.gpg

echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
echo "not on a tag -> derive version and keep snapshot"

newVersion=`./gradlew derive --preRelease='SNAPSHOT' -i | grep 'NEXT_VERSION:==' | sed 's/^.*NEXT_VERSION:==//g'`
gitCommit="bumped version to ${newVersion}"

# Print newVersion
echo "newVersion: ${newVersion}"

if [[ -z "${newVersion}" ]]; then
  echo "missing newVersion value" >&2
  exit 1
fi

git remote set-url origin https://${GITHUB_TOKEN}@github.com/${TRAVIS_REPO_SLUG}.git
git fetch
git checkout master
git show-ref

# Run the gradle publish steps
./gradlew setVersion -P newVersion=${newVersion} 1>/dev/null 2>/dev/null
./gradlew publishMavenJavaPublicationToMavenRepository
./gradlew publishPlugins

# Generate and push CHANGELOG.md
./gradlew changelog --toRef=master

git add ./CHANGELOG* || true
git add ./build.gradle || true
git commit -m "${gitCommit}" && git push origin || true
