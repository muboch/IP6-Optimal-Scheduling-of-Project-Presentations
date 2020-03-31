FROM phusion/baseimage:latest

CMD ["/sbin/my_init"]

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get install git autoconf libtool zlib1g-dev gawk g++ curl cmake subversion make mono-complete swig lsb-release python-dev default-jdk twine python-setuptools python-six python3-setuptools python3-dev python-wheel python3-wheel

WORKDIR /

RUN git clone -b v7.5 https://github.com/google/or-tools --single-branch

WORKDIR /or-tools

RUN make third_party
RUN make java

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*