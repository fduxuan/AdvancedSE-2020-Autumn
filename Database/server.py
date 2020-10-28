# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 10:47 下午

@Author: fduxuan

Desc: 启动

"""
from flask import Flask
from blueprint import user_blueprint


app = Flask(__name__)

app.register_blueprint(user_blueprint, url_prefix="/api/db/user")


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)