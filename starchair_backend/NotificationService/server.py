# -*- coding: utf-8 -*-

from flask import Flask
from geventwebsocket import WebSocketServer, Resource
from blueprint import notification_blueprint
from util.websocket import wsApplication
from middleware.config import Config
import pymongo
from middleware import DatabaseError
from util.consul import ConsulClient
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


@app.errorhandler(DatabaseError)
def framework_error(e):
    """
    standard exception handler in project
    :param e:
    :return:
    """
    return json.dumps(e.json(), ensure_ascii=False)


# for debug
@app.route('/register')
def register():
    consul_client.RegisterService(app.config.get('SERVICE_NAME'), app.config.get('SERVICE_ID'),
                                  app.config.get('SERVICE_HOST'), int(app.config.get('SERVICE_PORT')), ['secure=false'])
    service = consul_client.GetService(app.config.get('SERVICE_ID'))
    return "{0}:{1}".format(service['Address'], service['Port'])


@app.route('/getService')
def get():
    service = consul_client.GetService(app.config.get('SERVICE_ID'))
    return "{0}:{1}".format(service['Address'], service['Port'])


@app.route('/unregister')
def unregister():
    consul_client.UnregisterService(app.config.get('SERVICE_ID'))
    return "unregister" + app.config.get('SERVICE_ID')


app.register_blueprint(notification_blueprint, url_prefix="/api/notification")


if __name__ == '__main__':
    # app.run(host=app.config.get('HOST'), port=app.config.get('PORT'))
    # 如果是http请求走app使用原有的wsgi处理，如果是websocket请求走WebSocketApp处理
    WebSocketServer(
        (app.config.get('HOST'), app.config.get('PORT')),

        Resource([
            ('^/ws/.*', wsApplication),
            ('^/.*', app)
        ]),
        debug=False
    ).serve_forever()

