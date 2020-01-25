FROM mongo:4.0.5

RUN apt-get update &&\
    apt-get install unzip wget
RUN wget https://github.com/ging/carrefour_basket_data_challenge/raw/master/DelightingCustomersBD.json.zip &&\
    unzip DelightingCustomersBD.json.zip &&\
    rm DelightingCustomersBD.json.zip
COPY entrypoint.sh /

CMD ["bin/bash","/entrypoint.sh"]
