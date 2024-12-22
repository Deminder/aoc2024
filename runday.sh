#!/bin/bash
set -e

# Download puzzle input
day=${1:?"Missing day argument"}
INPUT_FILE="data/day${day}/input.txt"
SESSION_KEY_FILE=session.txt

USER_AGENT="curl/$(curl --version | head -n 1 | cut -f2-3 -d' ') $(git remote get-url origin) $(git config user.name) <$(git config user.email)>"
if [ ! -f "$INPUT_FILE" ] && [ -f "$SESSION_KEY_FILE" ]
then
	curl "https://adventofcode.com/2024/day/$day/input" \
		--compressed \
		-A "$USER_AGENT" \
		-H "Cookie: session=$(cat "$SESSION_KEY_FILE")" \
		--create-dirs \
		--output "$INPUT_FILE"
fi

# Build jar
./gradlew :installDist

# Run puzzle solution
for part in 1 2
do
  time build/install/aoc2024/bin/aoc2024 -i "$INPUT_FILE" ${day} ${part}
  echo
done