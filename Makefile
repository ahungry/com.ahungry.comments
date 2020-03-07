seed-db:
	cat sql/seed.sql | sqlite3 ~/.local/share/com.ahungry.comments.db

build:
	docker build -t ahungry/com.ahungry.comments .

test:
	docker run --rm -it ahungry/com.ahungry.comments lein test

start:
	docker run \
	-p 3001:3001 \
	-it ahungry/com.ahungry.comments

.PHONY: test
