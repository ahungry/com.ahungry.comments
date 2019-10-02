seed-db:
	cat sql/seed.sql | sqlite3 ~/.local/share/com.ahungry.comments.db
