# UDP-Client&Server
UDP的客户端和服务端，由hsk和aberror在某比赛时使用。
启动客户端（Client）后，一直向服务端的5001端口发送UDP数据包，直到服务端接受的数据包的总大小达到data_size参数的设置之后停止，数据的（最大）发送速率为rate所设置（不设置则默认为1）。

当服务端接受的数据量达到预设值之后，将向客户端发送应答ACK信号，使客户端停止发送数据。

> 注意在window系统之中，假如没有对防火墙进行设置很多端口是关闭的。
## 使用命令
### Client
```shell
java -jar UDP_SenderAndReceiver.jar dest_ip=192.168.2.188 src_ip=192.168.2.119 flow_count=10 data_size=10 flow_id=1 co_flow_id=2
java -jar UDP_SenderAndReceiver.jar dest_ip=192.168.2.188 src_ip=192.168.2.119 flow_count=10 data_size=10 flow_id=2 co_flow_id=2
java -jar UDP_SenderAndReceiver.jar dest_ip=192.168.2.188 src_ip=192.168.2.119 flow_count=10 data_size=10 flow_id=3 co_flow_id=3
java -jar UDP_SenderAndReceiver.jar dest_ip=192.168.2.188 src_ip=192.168.2.119 flow_count=10 data_size=10 flow_id=4 co_flow_id=3 rate=2
# rate=2是可选参数，设置最大传输速率（单位MB/s)，不设置默认为1MB/s
# 在上一个flow传出完成后删掉之前的记录的timeOut时间为5s
# 假如前一个的流传输的记录尚未达到timeOut时间，在接收到同样参数的新的flow的时会认为新的flow和以前的flow是同一个flow。（所以相同参数的flow要等待timeOut的时间（5s）再发送才是正常的）
```

#### 参数介绍

```shell
# 必选参数
## 目的地址和源地址
src_ip=192.168.2.119 
dest_ip=192.168.2.188 
## 协流中流的数量
flow_count=10 
## 流的大小
data_size=10 
## 协流和流的id
co_flow_id=2
flow_id=1 

# 可选参数
## 最高传输速率单位MB/s，默认为1
rate=1 
```

### Server

```shell
# 运行server，监听5001端口
java -cp UDP_SenderAndReceiver.jar Server
```

## 一些规定
对hash做出如下定义，在后面的行文中以hash简写。
$$
\begin{aligned}
  \text{hash} (coflowId,flowId) = 60 \times coflowID + flowID  
\end{aligned}
$$

```sequence
Client->Server: 正向数据流，输出端口为65535 - hash，输入端口5001
Client->Server: 正向数据流，输出端口为65535 - hash，输入端口5001
Client->Server: ... 一直发送
Server->Client: 直到接收到足够数据后，发送应答信号ACK
Server->Client: ACK输出端口65535 - hash，输入端口为10000 + hash
```