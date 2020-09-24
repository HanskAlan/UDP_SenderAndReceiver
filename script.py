#-*- coding:utf-8 -*-
import os,re,socket,math
from time import sleep

# 常数

## 命令的尾部添加什么
# TAIL = ''
TAIL = ' rate=2 > /dev/null &'

## 不同数据之间的运行时间
INTARVAL_TIME = 10

## 文件列表
FILE_NAME = [
	"test.txt",
	# "default.txt",
]



def getIpList():
	# 执行ifconfig命令获取ip地址
	cmdFile = os.popen('ifconfig')
	cmd_result = cmdFile.read()
	# pattern = re.compile(r'(inet.*?地址:)(\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3})')
	pattern = re.compile(r'(inet )(\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3})')
	ip_list = re.findall(pattern,cmd_result)

	# 列出获取的IP地址，以数组形式表示
	ipList = []
	for ip in ip_list:
		ipList.append(ip[1])
	print("The ipList of this computer is ")
	print(ipList)
	return ipList


"""
0:time;1:srcIP;2:dstIP;3:coflowId;4:flowCount;5:flowId;6:size
"""
def createClient(file_path,ipList):
	lastTime = 0
	completeTime = ""
	with open(file_path,'r') as file_obj:
		print("Start to create client")
		lines = file_obj.readline()
		while lines:
			line_list = lines.split()
			if len(line_list) == 0 : 
				lines = file_obj.readline()
				continue

			# print(line_list)
			completeTime = line_list[0]
			if line_list[1] in ipList:
				res = math.ceil(float(line_list[6]))
				nowTime = int(line_list[0])
				print("Sleep to " + line_list[0] + " ms")
				sleep((nowTime - lastTime) / 1000.0)
				lastTime = nowTime

				command = 'java -jar script/UDP_SenderAndReceiver.jar ' + \
					' src_ip=' + line_list[1] + \
					' dest_ip=' + line_list[2] + \
					' co_flow_id=' + line_list[3] + \
					' flow_id=' + line_list[4] + \
					' flow_count=' + line_list[5] + \
					' data_size=' + str(res) + TAIL
				
				print(command)
				os.system(command)	
			lines = file_obj.readline()
	file_obj.close()

	# 等到到文件的完成时间，这是用于多文件之中同步的
	completeTime = int(completeTime)
	print("Sleep to " + str(completeTime) + " ms")
	sleep((completeTime - lastTime) / 1000.0)


if __name__ == "__main__":
	ipList = getIpList()
	flag = False # 是否是第二轮
	for i in FILE_NAME:
		if(flag):
			print("Waiting for the next DataFile")
			sleep(INTARVAL_TIME) # 不同的数据间隔一定的时间运行
		else:
			flag = True
		createClient('script/dataSet/' + i,ipList)
