# This image comes with lein, hooray.
FROM clojure:alpine

# Get Lein for building
# RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
# RUN chmod +x lein
# RUN ./lein
# RUN cp lein /bin/lein

# Done with host OS setup work - now to build the app.

WORKDIR /app

COPY . /app

RUN mkdir -p /root/.local/share
RUN mkdir -p /root/.local/bin

RUN lein deps

CMD lein run
