import consul

from middleware.config import Config

consul_config = Config()


class ConsulClient(object):
    def __init__(self):
        self._consul = consul.Consul(consul_config.CONSUL_HOST, consul_config.CONSUL_PORT, scheme="http")

    def RegisterService(self, service_name, service_id, host, port, tags=None):
        tags = tags or []
        # url = "http://106.14.244.24:5001/api/user/check"
        url = "http://"+consul_config.SERVICE_HOST + ":" + str(consul_config.SERVICE_PORT) + "/api/user/check"
        print(url)
        # 注册服务
        self._consul.agent.service.register(
            service_name,
            service_id,
            host,
            port,
            tags,
            # 设置心跳检测：健康检查ip/端口，检查时间：5s,超时时间：30s，注销时间：30s
            check=consul.Check.http(url=url, interval='5s', timeout='30s', deregister='30s')
        )
        print(f"=============注册服务{service_name}成功================")

    def GetService(self, service_id):
        """
            根据服务id获取注册中心的服务
        :param service_id:
        :return:
        """

        services = self._consul.agent.services()
        service = services.get(service_id)
        if not service:
            return None, None
        # addr = "{0}:{1}".format(service['Address'], service['Port'])
        return service

    def UnregisterService(self, service_id):
        """
            注销服务
        :param service_id:
        :return:
        """

        self._consul.agent.service.deregister(service_id)
        self._consul.agent.check.deregister(service_id)
        print(f"===============成功退出服务{service_id}====================")

    def serve(self):
        try:
            consul_client = ConsulClient()
            consul_client.RegisterService(consul_config.SERVICE_NAME, consul_config.SERVICE_ID,
                                          consul_config.SERVICE_HOST, int(consul_config.SERVICE_PORT), ['secure=false'])
            res = consul_client.GetService(consul_config.SERVICE_ID)
            # raise Exception("===============注销python-service服务===============")
        except Exception as e:
            consul_client.UnregisterService(consul_config.SERVICE_ID)
