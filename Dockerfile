# This image comes with lein, hooray.
FROM clojure:alpine

# Get Lein for building
# RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
# RUN chmod +x lein
# RUN ./lein
# RUN cp lein /bin/lein

# Done with host OS setup work - now to build the app.

# Use a non root user
RUN yes dummy | adduser dummy
#RUN yes dummy | passwd dummy
RUN mkdir -p /home/dummy

WORKDIR /app

COPY . /app

RUN chown -R dummy:dummy /app

RUN lein deps

CMD lein run
