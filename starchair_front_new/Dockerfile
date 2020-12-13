FROM node:latest as builder
WORKDIR /starchair_frontend
COPY . /starchair_frontend
RUN npm install
RUN npm run build

FROM nginx:alpine

COPY --from=builder /starchair_frontend/dist/  /srv/dist
# 使用自定义nginx.conf配置端口和监听
COPY default.conf /etc/nginx/conf.d/
