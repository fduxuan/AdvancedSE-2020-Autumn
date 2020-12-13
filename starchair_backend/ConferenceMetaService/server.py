# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 10:47 下午

@Author: fduxuan

Desc: 启动

"""
from flask import Flask
from blueprint import conference_blueprint
from middleware.config import Config
import pymongo
from middleware import DatabaseError
import json
from middleware.helper import user_has_login, json_success


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


@app.errorhandler(DatabaseError)
def framework_error(e):
    """
    standard exception handler in project
    :param e:
    :return:
    """
    return json.dumps(e.json(), ensure_ascii=False)


@app.route('/check', methods=['GET'])
@json_success
def check():
    return 'success'


@app.route('/', methods=['GET'])
@json_success
@user_has_login
def if_login():
    return 'success login'


app.register_blueprint(conference_blueprint, url_prefix="/api/conference")


if __name__ == '__main__':
    app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))