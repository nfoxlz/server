# CompeteMIS平台 Java版后台
CompeteMIS平台是一个开源的低代码的MIS基础平台，通过本系统，可快速搭建ERP、MES、财务等MIS软件系统。仅需要使用SQL及少量的C#代码就可快速的搭建起应用。一般一个简单画面功能可以在几分钟内完成，复杂画面功能可以在几小时内完成。
后台有Java语言和Go语言两种实现版本。这是Java版本。Java版后台采用spring boot + Dubbo。

**注意**：部署Java版后台时，需要配置注册中心。源码中的注册中心配置如下：
```yaml
dubbo:
  application:
    name: mis-system-web
    qos-port: 33333
  protocol:
    name: dubbo
    port: -1
  registry:
    address: zookeeper://${zookeeper.address:192.168.172.128}:2181
  consumer:
    timeout: 10000
  provider:
    timeout: 30000
```
为zookeeper，IP地址为192.168.172.128。可在mis-system-web子工程下的src/resources/application.yml中修改。

**注意**：Java版后台的端口号为8090，与客户端的8080端口不一致。可以使用反向代理将8080重映射到8090。下面是Nginx的配置示例：
```config
worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       8080;
        server_name  localhost;

        location /api {
            proxy_pass http://127.0.0.1:8090/api;
        }
    }
}
```

**作者邮箱：** 4043171@qq.com
