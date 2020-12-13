from geventwebsocket import WebSocketServer, WebSocketApplication, Resource
from util.rabbit_producer import rmq_producer
from util.rabbit_consumer import rmq_consumer
from threading import Thread

WEBSOCKET_DICT = {}


class wsApplication(WebSocketApplication):
    def on_open(self):
        print("Connection opened")
        current_client = self.ws.handler.active_client
        username = self.ws.path.split('/')[-1]
        WEBSOCKET_DICT[username] = current_client
        print(WEBSOCKET_DICT)
        # 新建一个raabit消费者，并将current client绑定到消费方法
        new_consumer = Thread(target=rmq_consumer.threaded_start_consumer, args=(username, current_client))
        new_consumer.start()

    def on_message(self, message, *args, **kwargs):
        print("Recieved from client", message)

    def on_close(self, reason):
        print("Connection closed", reason)
        current_client = self.ws.handler.active_client
        # 从WEBSOCKET_DICT中删除该client，并删除rabbit消费者(在线程中自动完成)
        for k, v in WEBSOCKET_DICT.copy().items():
            if v == current_client:
                del WEBSOCKET_DICT[k]
                # 发送退出信号
                rmq_producer.send_message(k, 'quit')
                break
        print(WEBSOCKET_DICT)


