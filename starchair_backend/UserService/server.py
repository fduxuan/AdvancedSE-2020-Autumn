# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 10:47 下午

@Author: fduxuan

Desc: 启动

"""
from flask import Flask
from blueprint import user_blueprint
from middleware.config import Config
import pymongo
from middleware import DatabaseError
from middleware.helper import json_success
from middleware.consul import ConsulClient
import json
from flask_cors import CORS

consul_client = ConsulClient()

app = Flask(__name__)
app.config.from_object(Config)
# CORS(app, resources=r'*')
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
    admin = {"_id": 'admin', 'username': 'admin', 'fullname': 'admin', 'password': 'password', 'email': 'admin@admin.com', "admin": True}
    app.db.user.update_one({'_id': 'admin'}, {"$set": admin}, upsert=True)


@app.errorhandler(DatabaseError)
def framework_error(e):
    """
    standard exception handler in project
    :param e:
    :return:
    """
    return json.dumps(e.json(), ensure_ascii=False)


@app.route('/api/user/check', methods=['GET'])
@json_success
def check():
    return 'success'


@app.route('/api/user', methods=['GET'])
@json_success
def index():
    return 'success start user service!'


app.register_blueprint(user_blueprint, url_prefix="/api/user")


if __name__ == '__main__':
    app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))