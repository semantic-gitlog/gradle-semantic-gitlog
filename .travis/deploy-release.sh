#!/bin/bash

set -e

cd ${TRAVIS_BUILD_DIR}

# Prepare the local keyring (requires travis to have decrypted the file
# beforehand)
gpg --fast-import .travis/secret.gpg

echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
echo "on a tag -> set project version to ${TRAVIS_TAG}"

newVersion=${TRAVIS_TAG}
gitCommit="release: ${newVersion}"

# Print newVersion
echo "newVersion: ${newVersion}"

if [[ -z "${newVersion}" ]]; then
  echo "missing newVersion value" >&2
  exit 1
fi

# Run the gradle publish steps
./gradlew setVersion -P newVersion=${newVersion} 1>/dev/null 2>/dev/null
./gradlew publishMavenJavaPublicationToMavenRepository
./gradlew publishPlugins

# Generate and push CHANGELOG.md - fully
git reset --hard
git remote set-url origin https://${GITHUB_TOKEN}@github.com/${TRAVIS_REPO_SLUG}.git
git remote set-branches --add origin 'master'
git fetch
git checkout master
git show-ref
chmod +x ./gradlew .travis/*.sh

./gradlew setVersion -P newVersion=${newVersion} 1>/dev/null 2>/dev/null
./gradlew changelog --toRef=master

git add ./CHANGELOG* || true
git add ./build.gradle || true
git commit --amend -m "${gitCommit}" && git push -f || true
