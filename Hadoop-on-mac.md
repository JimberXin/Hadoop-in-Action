# Mac下的Hadoop安装以及配置


### 一、jdk环境配置

从官网下载最新的jdk1.8，双击安装即可。注意mac系统自带的jdk最高是1.6，

#####  全局配置
在/etc/profile中导入java路径：

	$ sudo su
	# 此处输入密码
	$ vi /etc/profile
在打开的profile文件中，配置JAVA_HOME的路径，我的是

	JAVA_HOME="/System/Library/Java/JavaVirtualMachines/1.8.0_25.jdk/Contents/Home/"	CLASS_PATH="$JAVA_HOME/lib"
	PATH=".:$PATH:$JAVA_HOME/bin"	export JAVA_HOME##### 为当前用户配置
此配置下只对当前用户有效。输入如下命令：
	cd ~
	vi .bash_profile
	
在打开的.bash_profile文件末尾，加入如下代码：

	export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home


##### 命令行查看java版本
	$ java -version

发现确实是最新版本，显示结果如下：

	java version "1.8.0_25"	Java(TM) SE Runtime Environment (build 1.8.0_25-b17)	Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02, mixed mode)
也可以用 /usr/libexec/java_home –V查看所有的java版本


### 二、安装SSH

##### 1. 安装ssh, 方法网上很多，自己找

##### 2. 配置为免密码登陆本机

	$ ssh -keygen -t -P '' -f ~/.ssh/id_dsa

其中，

	ssh-keygen 代表生成密匙；
	-t（小写） 表示指定生成的密匙类型；
	dsa 表示dsa密匙认证的意思，即密钥类型；
	-P 用于提供密语，这里表示免密码登陆，所以是’’, 
	-f指定生成的密钥文件，放在用户根文件夹下，此处 ~ 为用户目录 /Users/JimberXin.该命令后，在.ssh文件夹下创建id_dsa以及id_dsa.pub两个文件。
这是ssh的一对私钥和公钥类似钥匙和锁，接着，把id_dsa.pub(公钥)追加到授权的key中去	
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
1. 最基本的划分，Master与Slave2. 从HDFS的角度， 将主机分为NameNode 和 DadaNode。在分布式文件系统中，目录管理很重要，管理目录相当于主人，NameNode就是目录管理者
3. 从MapReduce的角度，将主机分为JobTracker和TaskTrackerHadoop有3中运行方式：单机模式、伪分布式以及全分布式。其中单机模式无需配置，hadoop被认为是一个单独的java进程，主要用于调试。伪分布式的hadoop可以看成只有一个节点的集群，在这个集群中，该节点既是Master也是Slave， 既是NameNode也是DataNode， 既是JobTracker也是TaskTracker，这里搭建的环境就是指的伪分布式。
##### (1）指定jdk安装位置	
打开etc/hadoop/hadoop-env.sh，增加如下代码  

	# The java implementation to use.	export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk

##### (2) Hadoop核心配置文件core-site.xml，配置HDFS地址与端口
打开etc/hadoop/core-site.xml，增加如下代码

	<configuration>
	    <property>            <name>hadoop.tmp.dir</name>            <value>/Users/JimberXin/hadoop-2.6.0/temp</value>            <description>A base for other temporary directory</description>
	    </property>
	    
	    <property>            <name>fs.default.name</name>            <value>hdfs://localhost:9000</value>        </property>    </configuration>
其中，hadoop.tmp.dir指定hadoop的临时文件；fs.default.name是HDFS的地址与端口号,

* 单机是file:///; 
* 伪分布式是 hdfs://localhost:9000; 
* 全分布式是hdfs://namenode

##### (3) 配置HDFS的配置，默认复制个数为3， 修改为1
打开etc/hadoop/hdfs-site.xml, 增加以下代码：
	<configuration>	    <property>            <name>dfs.replication</name>            <value>1</value>	    </property>	</configuration>##### (4) 配置MapReduce的配置
hadoop2.6.0默认是没有mapred-site.xml的，自己创建 mapred-site.xml，配置JobTracker的地址及端口号，增加如下代码：
	<configuration>		<property>    		<name>mapred.job.tracker</name>    		<value>hdfs://localhost:9001</value>		</property>	</configuration>其中，mapred.job.tracker是JobTracker的地址以及端口，
* 单机版是local，
* 伪分布式是localhost:9001
* 全分布式是jobtracker:9001
##### (5) 配置yarnHadoop2.x以后MapReduce框架并入yarn框架，需要进行配置
打开etc/hadoop/yarn-site.xml，增加如下代码
	<configuration>		<property>    		<name>mapreduce.framework.name</name>    		<value>yarn</value>		</property>
		<property>    		<name>yarn.nodemanager.aux-services</name>    		<value>mapreduce_shuffle</value>		</property>	</configuration>至此，基本的配置文件都已经修改完毕，准备启动Hadoop。在此之前，需要格式化Hadoop的文件系统HDFS。进入Hadoop文件夹，输入如下命令：
	$ bin/hadoop namenode –format此命令格式化文件系统，接下来启动Hadoop。输入命令，启动所有进程
	$ bin/start-all.sh关闭所有进程
	$ bin/stop-all.sh也可以单独开启或者关闭某些进程，例如sbin/start-dfs.sh，sbin/start-yarn.sh，sbin/stop-yarn.sh等。启动了所有进程后，命令行输入jps，用于查看当前的java进程，显示如下：
	10224 
	7729 SecondaryNameNode
	12100 Jps
	7639 DataNode
	7912 NodeManager
	7833 ResourceManager
	7566 NameNode
最后，验证Hadoop是否安装成功。打开浏览器，分别查看HDFS以及MapReduce的页面是否能打开。
* HDFS的页面：<http://localhost:50070>
<img src="http://jimber.qiniudn.com/QQ20150304-8@2x.jpg" width = "600" height = "450" alt="HDFS" />

* MapReduce的页面: <http://localhost:8088>

<img src="http://jimber.qiniudn.com/QQ20150304-9@2x.jpg" width = "650" height = "300" alt="HDFS" />

##### (6)查看集群状态输入命令: 
	$ bin/hdfs dfsadmin –report显示结果如下:
	15/03/04 19:18:08 WARN util.NativeCodeLoader: 	Unable to load native-hadoop library for your 	platform... using builtin-java classes where applicable	Configured Capacity: 120154296320 (111.90 GB)	Present Capacity: 22928572416 (21.35 GB)	DFS Remaining: 22928371712 (21.35 GB)	DFS Used: 200704 (196 KB)	DFS Used%: 0.00%	Under replicated blocks: 0	Blocks with corrupt replicas: 0	Missing blocks: 0		-------------------------------------------------	Live datanodes (1):	Name: 127.0.0.1:50010 (localhost)	Hostname: 192.168.1.118	Decommission Status : Normal	Configured Capacity: 120154296320 (111.90 GB)	DFS Used: 200704 (196 KB)	Non DFS Used: 97225723904 (90.55 GB)	DFS Remaining: 22928371712 (21.35 GB)	DFS Used%: 0.00%	DFS Remaining%: 19.08%	Configured Cache Capacity: 0 (0 B)	Cache Used: 0 (0 B)	Cache Remaining: 0 (0 B)	Cache Used%: 100.00%	Cache Remaining%: 0.00%	Xceivers: 1	Last contact: Wed Mar 04 19:18:09 CST 2015可知DataNode的个数以及运行状态是对的
### 四、运行wordCount程序
#####1、准备工作######（1）创建本地示例文件首先在hadoop文件夹下，创建输入文件夹input
	$ mkdir input接着在input文件夹下创建两个文本文件f1和f2输入命令
	$ echo “Hello World Bye World” > f1	$ echo “Hello Hadoop Bye Hadoop” > f2######（2）	创建HDFS上的文件夹   Hdfs是hadoop Distributed file system的缩写，是hadoop的分布式文件系统。   
   Hdfs由hadoop来管理，它不同于普通的文件系统，不能直观的查找文件，必须要通过hadoop命令操作hdfs。
   一些常见HDFS的命令

	hadoop fs -mkdir /tmp/input  在HDFS上新建文件夹	hadoop fs -put input1.txt /tmp/input  把本地文件input1.txt传到HDFS的/tmp/input目录下    hadoop fs -get  input1.txt /tmp/input/input1.txt  把HDFS文件拉到本地    hadoop fs -ls /tmp/output        列出HDFS的某目录    hadoop fs -cat /tmp/ouput/output1.txt  查看HDFS上的文件    hadoop fs -rmr /home/less/hadoop/tmp/output  删除HDFS上的目录    hadoop dfsadmin -report 查看HDFS状态，比如有哪些datanode，每个datanode的情况    hadoop dfsadmin -safemode leave  离开安全模式    hadoop dfsadmin -safemode enter  进入安全模式这里查看这个文件还有一个方法就是在网页中查看:进入<http://127.0.0.1:50070>,也就是hdfs的管理系统，然后点击相应的文件目录就可以了.
这里需要注意:当查看hadoop文件系统的时候需要用命令hadoop fs –ls  这样前面加上hadoop fs 因为假如直接用ls的话,那么就是指模拟出来的linux文件系统中的东西
现在，可以开始在HDFS上创建文件夹了，输入命令
	$ bin/hadoop fs –mkdir /temp	$ bin/hadoop fs –mkdir /temp/input######（3）	从本地文件夹向HDFS文件夹上传文件输入命令
	$ bin/hadoop fs –put input /temp该命令将本地文件input下的文件拷贝到HDFS文件夹上得/temp上
######（4）	检查HDFS是否已经存在拷贝后的文件输入命令	
	$ bin/hadoop fs –ls /temp/input
显示结果如下：
	15/03/03 17:16:48 WARN util.NativeCodeLoader: Unable to load   native-hadoop library for your platform... using builtin-java classes where applicable	Found 2 items	-rw-r--r-- 1  JimberXin supergroup 22 2015-03-03 17:16  /temp/input/f1	-rw-r--r—1  JimberXin supergroup 28 2015-03-03 17:16  /temp/input/f2可以发现，f1和f2确实已经在HDFS的文件夹/temp/input上了
######（5）	运行WordCount程序利用hadoop2.6.0自带的hadoop-mapreduce-examples-2.6.0.jar，给定输入，来运行WordCount程序
	$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar  wordcount /temp/input/ /output/wordcount3其中:
* bin/hadoop jar为执行jar命令；* share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar为WordCount所在的jar包；* wordcount为生成的程序主类名，自定义* /temp/input为程序的输入，这里包括f1和f2* /output/wordcount3为程序的输出文件
输出结果如下所示：

		15/03/03 17:17:52 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable		15/03/03 17:17:53 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032		15/03/03 17:17:54 INFO input.FileInputFormat: Total input paths to process : 2		15/03/03 17:17:54 INFO mapreduce.JobSubmitter: number of splits:2		15/03/03 17:17:54 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1425351719448_0001		15/03/03 17:17:55 INFO impl.YarnClientImpl: Submitted application application_1425351719448_0001		15/03/03 17:17:55 INFO mapreduce.Job: The url to track the job: http://xinjunbodeMacBook-Pro-2.local:8088/proxy/application_1425351719448_0001/		15/03/03 17:17:55 INFO mapreduce.Job: Running job: job_1425351719448_0001		15/03/03 17:18:08 INFO mapreduce.Job: Job job_1425351719448_0001 running in uber mode : false		15/03/03 17:18:08 INFO mapreduce.Job:  map 0% reduce 0%		15/03/03 17:18:16 INFO mapreduce.Job:  map 100% reduce 0%		15/03/03 17:18:22 INFO mapreduce.Job:  map 100% reduce 100%		15/03/03 17:18:22 INFO mapreduce.Job: Job job_1425351719448_0001 completed successfully		15/03/03 17:18:22 INFO mapreduce.Job: Counters: 49			File System Counters				FILE: Number of bytes read=79				FILE: Number of bytes written=317173				FILE: Number of read operations=0				FILE: Number of large read operations=0				FILE: Number of write operations=0				HDFS: Number of bytes read=250				HDFS: Number of bytes written=41				HDFS: Number of read operations=9				HDFS: Number of large read operations=0				HDFS: Number of write operations=2			Job Counters 				Launched map tasks=2				Launched reduce tasks=1				Data-local map tasks=2				Total time spent by all maps in occupied slots (ms)=10968				Total time spent by all reduces in occupied slots (ms)=3183				Total time spent by all map tasks (ms)=10968				Total time spent by all reduce tasks (ms)=3183				Total vcore-seconds taken by all map tasks=10968				Total vcore-seconds taken by all reduce tasks=3183				Total megabyte-seconds taken by all map tasks=11231232				Total megabyte-seconds taken by all reduce tasks=3259392			Map-Reduce Framework				Map input records=2				Map output records=8				Map output bytes=82				Map output materialized bytes=85				Input split bytes=200				Combine input records=8				Combine output records=6				Reduce input groups=5				Reduce shuffle bytes=85				Reduce input records=6				Reduce output records=5				Spilled Records=12				Shuffled Maps =2				Failed Shuffles=0				Merged Map outputs=2				GC time elapsed (ms)=215				CPU time spent (ms)=0				Physical memory (bytes) snapshot=0				Virtual memory (bytes) snapshot=0				Total committed heap usage (bytes)=513802240			Shuffle Errors				BAD_ID=0				CONNECTION=0				IO_ERROR=0				WRONG_LENGTH=0				WRONG_MAP=0				WRONG_REDUCE=0			File Input Format Counters 				Bytes Read=50			File Output Format Counters 				Bytes Written=41
				
Hadoop命令会启动一个JVM来运行这个MapReduce程序，并自动获得Hadoop的配置，同时把类的路径（及其依赖关系）加入到Hadoop的库中。以上就是Hadoop Job的运行记录.

从这里可以看到，这个Job被赋予了一个ID号：job_1425351719448_0001，而且得知输入文件有两个（Total input paths to process : 2），同时还可以了解map的输入输出记录（record数及字节数），以及reduce输入输出记录。比如说，在本例中，map的task数量是2个，reduce的task数量是一个。map的输入record数是2个，输出record数是4个等信息。######（6）	查看运行结果 输入命令
	$ bin/hadoop dfs –ls /output/wordcount3/*显示结果如下：
	15/03/04 22:22:07 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable	Found 2 items	-rw-r--r--   1 JimberXin supergroup          0 2015-03-03 17:18 /output/wordcount3/_SUCCESS	-rw-r--r--   1 JimberXin supergroup         41 2015-03-03 17:18 /output/wordcount3/part-r-00000可知生成了两个文件，我们的结果存在part-r-00000中，显示其内容.
输入命令：
	$ bin/hadoop dfs –cat /output/wordcount3/*显示结果如下:
	15/03/03 17:19:12 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable	Bye	1	Goodbye	1	Hadoop	2	Hello	2	World	2