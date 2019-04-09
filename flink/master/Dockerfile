FROM ging/flink-base:1.7.2-hadoop2.8

MAINTAINER Andrés Muñoz <andres.max36@gmail.com>

# Install dependencies
RUN apt-get update && \
    apt-get install curl git -y --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Install Node js, to install OAuth Client
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Download latest version of the code and install npm dependencies
RUN git clone -b usage-genova --single-branch https://github.com/ging/oauth2-example-client.git && \
    cd oauth2-example-client && \
    npm cache clean -f && \
    npm install

# Install forever to run oauth client in backgroubnd
RUN npm install forever -g

# Create folder to store access token and share with workers
RUN mkdir -p /etc/oauth

# Copy config template
RUN cd oauth2-example-client && \
    cp config.js.template config.js

#config files (template)
#ADD /usr/local/flink/conf/flink-conf.yaml /usr/local/flink/conf/

COPY master.sh /
#RUN chmod +x /usr/local/flink/bin/jobmanager.sh
ENV FLINK_MASTER_LOG /usr/local/flink/log

EXPOSE 6123 22 8080

CMD ["/bin/bash", "/master.sh"]
