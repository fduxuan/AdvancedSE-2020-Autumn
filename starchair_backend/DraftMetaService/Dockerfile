#FROM python:alpine
FROM registry.cn-hangzhou.aliyuncs.com/xzzzh/alpine-python3:3.7.4

WORKDIR /DraftMetaService
ADD requirements.txt .
RUN echo '[global]' >> /etc/pip.conf && \
    echo 'index-url = https://mirrors.ustc.edu.cn/pypi/web/simple' >> /etc/pip.conf && \
    echo 'timeout = 6000' >> /etc/pip.conf && \
    echo 'format = columns' >> /etc/pip.conf && \
    echo 'no-cache-dir = false' >> /etc/pip.conf && \
    pip3 install -r requirements.txt

VOLUME /everything

ENV MONGO_URL=mongodb://starchair_mongo
ENV PREFIX=''
ENV LANG="en_US"
ENV DEBUG=0

ADD . /DraftMetaService
CMD ["python3", "server.py"]