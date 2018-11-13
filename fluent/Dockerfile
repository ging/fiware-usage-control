FROM fluent/fluentd:v1.3-onbuild
MAINTAINER Andrés Muñoz <andres.max36@gmail.com>

RUN apk add --update --virtual .build-deps \
        sudo build-base ruby-dev \
 # cutomize following instruction as you wish
 && sudo gem install \
         fluent-plugin-kafka fluent-plugin-secure-forward \
 && sudo gem sources --clear-all \
 && apk del .build-deps \
 && rm -rf /var/cache/apk/* \
           /home/fluent/.gem/ruby/2.3.0/cache/*.gem
ADD fluent.conf /fluentd/etc/

EXPOSE 24284
