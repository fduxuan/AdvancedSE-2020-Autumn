# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 10:47 下午

@Author: fduxuan

Desc: 启动

"""
from flask import Flask
from blueprint import user_blueprint
from middleware.config import Config
from flask import request
import pymongo
from middleware import DatebaseError
import json


app = Flask(__name__)
app.config.from_object(Config)


@app.before_first_request
def setups():
    """
    connect to mongo before the first request
    :return:
    """
    mongo_url = app.config.get('MONGO_URL')
    mongo_client = pymongo.MongoClient(mongo_url)
    app.db = mongo_client.get_database(name=app.config.get('DEFAULT_DATABASE'))


@app.errorhandler(DatebaseError)
def framework_error(e):
    """
    standard exception handler in project
    :param e:
    :return:
    """
    return json.dumps(e.json(), ensure_ascii=False)


app.register_blueprint(user_blueprint, url_prefix="/api/db/user")


if __name__ == '__main__':
    app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))