# -*- coding: utf-8 -*-
"""
Created on 2020/11/23 11:54 下午

@Author: fduxuan

Desc:

"""
from flask import Flask
from blueprint import discuss_blueprint
from middleware.config import Config
import pymongo
from middleware import DatabaseError
from middleware import ConsulClient
import json
from middleware.helper import user_has_login, json_success

consul_client = ConsulClient()

app = Flask(__name__)
app.config.from_object(Config)
consul_client.RegisterService(app.config.get('SERVICE_NAME'), app.config.get('SERVICE_ID'),
                                  app.config.get('SERVICE_HOST'), int(app.config.get('SERVICE_PORT')), ['secure=false'])
service = consul_client.GetService(app.config.get('SERVICE_ID'))
print("{0}:{1}".format(service['Address'], service['Port']))


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


@app.route('/api/discuss/check', methods=['GET'])
@json_success
def check():
    return 'success'


@app.route('/', methods=['GET'])
@json_success
@user_has_login
def if_login():
    return 'success login'


app.register_blueprint(discuss_blueprint, url_prefix="/api/discuss")


if __name__ == '__main__':
    app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))