# Mac下的Hadoop安装以及配置


### 一、jdk环境配置

从官网下载最新的jdk1.8，双击安装即可。注意mac系统自带的jdk最高是1.6，

#####  全局配置
在/etc/profile中导入java路径：

	$ sudo su
	# 此处输入密码
	$ vi /etc/profile
在打开的profile文件中，配置JAVA_HOME的路径，我的是

	JAVA_HOME="/System/Library/Java/JavaVirtualMachines/1.8.0_25.jdk/Contents/Home/"
	PATH=".:$PATH:$JAVA_HOME/bin"


	vi .bash_profile
	
在打开的.bash_profile文件末尾，加入如下代码：

	export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home


##### 命令行查看java版本
	$ java -version

发现确实是最新版本，显示结果如下：

	java version "1.8.0_25"



### 二、安装SSH

##### 1. 安装ssh, 方法网上很多，自己找

##### 2. 配置为免密码登陆本机

	$ ssh -keygen -t -P '' -f ~/.ssh/id_dsa

其中，

	ssh-keygen 代表生成密匙；
	-t（小写） 表示指定生成的密匙类型；
	dsa 表示dsa密匙认证的意思，即密钥类型；
	-P 用于提供密语，这里表示免密码登陆，所以是’’, 
	-f指定生成的密钥文件，放在用户根文件夹下，此处 ~ 为用户目录 /Users/JimberXin.

	$ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys

该命令把公钥用于认证的公钥文件中，这里指的是authorized_keys

##### 3. 验证SSH是否已经安装成功过，以及是否可以免密码登陆

	$ ssh -version
显示结果
	
	OpenSSH_6.2p2, OSSLShim 0.9.8r 8 Dec 2011
	Bad escape character 'rsion'.
输入命令
	
	$ ssh localhost
至此，ssh 安装成功

### 三、安装Hadoop
Hadoop 分别从3个角度将主机分为两种角色

3. 从MapReduce的角度，将主机分为JobTracker和TaskTracker



	# The java implementation to use.

##### (2) Hadoop核心配置文件core-site.xml，配置HDFS地址与端口
打开etc/hadoop/core-site.xml，增加如下代码

	<configuration>
	    <property>
	    </property>
	    
	    <property>
其中，hadoop.tmp.dir指定hadoop的临时文件；fs.default.name是HDFS的地址与端口号,

* 单机是file:///; 
* 伪分布式是 hdfs://localhost:9000; 
* 全分布式是hdfs://namenode

##### (3) 配置HDFS的配置，默认复制个数为3， 修改为1
打开etc/hadoop/hdfs-site.xml, 增加以下代码：

hadoop2.6.0默认是没有mapred-site.xml的，自己创建 mapred-site.xml，配置JobTracker的地址及端口号，增加如下代码：


* 伪分布式是localhost:9001
* 全分布式是jobtracker:9001

打开etc/hadoop/yarn-site.xml，增加如下代码






	7729 SecondaryNameNode
	12100 Jps
	7639 DataNode
	7912 NodeManager
	7833 ResourceManager
	7566 NameNode
最后，验证Hadoop是否安装成功。打开浏览器，分别查看HDFS以及MapReduce的页面是否能打开。



* MapReduce的页面: <http://localhost:8088>

<img src="http://jimber.qiniudn.com/QQ20150304-9@2x.jpg" width = "450" height = "250" alt="HDFS" />

##### (6)查看集群状态



#####1、准备工作





	hadoop fs -mkdir /tmp/input  在HDFS上新建文件夹





	$ bin/hadoop fs –ls /temp/input
显示结果如下：






		15/03/03 17:17:52 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Hadoop命令会启动一个JVM来运行这个MapReduce程序，并自动获得Hadoop的配置，同时把类的路径（及其依赖关系）加入到Hadoop的库中。以上就是Hadoop Job的运行记录.

从这里可以看到，这个Job被赋予了一个ID号：job_1425351719448_0001，而且得知输入文件有两个（Total input paths to process : 2），同时还可以了解map的输入输出记录（record数及字节数），以及reduce输入输出记录。比如说，在本例中，map的task数量是2个，reduce的task数量是一个。map的输入record数是2个，输出record数是4个等信息。




