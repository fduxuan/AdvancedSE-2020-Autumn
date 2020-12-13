import pika
from middleware.config import Config
from threading import Thread
from util.rabbit_producer import rmq_producer
import time

config = Config()


class Consumer:
    def __init__(self):
        self.credentials = pika.PlainCredentials(config.RABBIT_USERNAME, config.RABBIT_PWD)
        self.consumer_dict = dict()

    def __del__(self):
        print("main thread ended, cleaning up...")
        # shutdown all threads gracefully
        for queue_name in self.consumer_dict.keys():
            rmq_producer.send_message(queue_name, 'quit')

    def is_consumer_exist(self, queue_name):
        """
        对指定队列，判断consumer是否已存在。始终保持一个队列至多一个consumer
        :param queue_name:
        :return:
        """
        if queue_name in self.consumer_dict.keys():
            return True
        return False

    def threaded_start_consumer(self, queue_name, ws=None):
        connection = pika.BlockingConnection(pika.ConnectionParameters(
            config.RABBIT_HOST, config.RABBIT_PORT, '/', self.credentials, heartbeat=0))
        channel = connection.channel()
        channel.queue_declare(queue=queue_name, durable=True)

        # consumer接收到消息时的回调函数
        def on_message(ws_client, queue_name, ch, method, body):
            print(body.decode())
            ch.basic_ack(delivery_tag=method.delivery_tag)  # 对quit也要ack
            # 接收到producer发送的quit信号
            if body.decode() == 'quit':
                ch.stop_consuming()
                return
            if ws_client:
                try:
                    ws_client.ws.send(body.decode())
                except Exception:
                    # 防止ws已经关闭了，把该consumer关掉
                    print("websocket error")
                    rmq_producer.send_message(queue_name, 'quit')

        consumer_tag = channel.basic_consume(on_message_callback=lambda ch, method, properties, body: on_message(ws, queue_name, ch, method, body),
                                             queue=queue_name,
                                             auto_ack=False)
        self.consumer_dict[queue_name] = {'connection': connection, 'channel': channel, 'consumer': consumer_tag}
        # print(self.consumer_dict)
        # 阻塞，直到收到quit信号
        channel.start_consuming()
        # 此处已经stop consuming
        print('stop consuming ', queue_name, ', cleaning up..')
        channel.basic_cancel(consumer_tag=consumer_tag)
        connection.close()
        if queue_name in self.consumer_dict.keys():  # 防止同一个user登录时，被删除两次
            del self.consumer_dict[queue_name]
        # print(self.consumer_dict)


rmq_consumer = Consumer()

if __name__ == '__main__':
    consumer = Consumer()
    test_consumer = Thread(target=consumer.threaded_start_consumer, args=('test',))
    test_consumer.start()

    test1_consumer = Thread(target=consumer.threaded_start_consumer, args=('test1',))
    test1_consumer.start()

    for i in range(20):
        print(i)
        time.sleep(1)
    del consumer
