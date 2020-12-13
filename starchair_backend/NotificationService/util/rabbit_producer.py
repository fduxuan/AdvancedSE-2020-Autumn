import pika
import time
from pika.exceptions import ChannelClosed
from pika.exceptions import ConnectionClosed

from middleware.config import Config

config = Config()


class Producer:
    def __init__(self):
        self.credentials = pika.PlainCredentials(config.RABBIT_USERNAME, config.RABBIT_PWD)
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(
                config.RABBIT_HOST, config.RABBIT_PORT, '/', self.credentials, heartbeat=0))
        # 如果设置heartbeat_interval = 0， 意味着不检测心跳，server端将不会主动断开连接
        self.channel = self.connection.channel()

    def __del__(self):
        self.connection.close()

    def reconnect(self):
        try:
            if self.connection and not self.connection.is_closed:
                self.connection.close()
            self.connection = pika.BlockingConnection(pika.ConnectionParameters(
                config.RABBIT_HOST, config.RABBIT_PORT, '/', self.credentials, heartbeat=0))
            self.channel = self.connection.channel()
        except Exception as e:
            print(e)

    def send_message(self, queue_name, message):
        count = 0
        print("[" + queue_name + "]" + "is trying to send msg: " + message)
        while count < 5:
            try:
                self.channel.queue_declare(queue=queue_name, durable=True)
                self.channel.basic_publish(exchange='', routing_key=queue_name, body=message,
                                           properties=pika.BasicProperties(delivery_mode=2,  # make message persistent
                                            ))
                print("[" + queue_name + "]" + " send msg: " + message + " success... the count is" + str(count))
                count = count + 1
                break
            except ConnectionClosed as e:
                count = count + 1
                print("Connection closed, retrying... the count is " + str(count))
                self.reconnect()
                time.sleep(2)
            except ChannelClosed as e:
                count = count + 1
                print("Channel closed, retrying... the count is " + str(count))
                self.reconnect()
                time.sleep(2)
            except Exception as e:
                count = count + 1
                print("Unknown exception, retrying... the count is " + str(count))
                self.reconnect()
                time.sleep(2)


rmq_producer = Producer()

if __name__ == '__main__':
    producer = Producer()
    # producer.send_message('test', 'hello from test')
    producer.send_message('test', 'quit')
