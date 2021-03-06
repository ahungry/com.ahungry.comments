seed-db:
	cat sql/seed.sql | sqlite3 ~/.local/share/com.ahungry.comments.db

build:
	docker build -t ahungry/com.ahungry.comments .

test:
	docker run --rm -it ahungry/com.ahungry.comments lein test

start:
	docker run \
	-m 256m \
	-u $(shell id -u) \
	-v $(PWD)/docker-data/.local:/home/dummy/.local \
	-p 3001:3001 \
	--rm -it ahungry/com.ahungry.comments

.PHONY: test
