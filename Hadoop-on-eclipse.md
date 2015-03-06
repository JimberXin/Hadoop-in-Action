# Eclipse下的Hadoop安装以及配置


### 1. 安装ant

官网下载最新版本： apache-ant-1.9.4, <http://ant.apache.org/bindownload.cgi>
<img src="http://jimber.qiniudn.com/QQ20150304-1@2x.jpg" width = "450" height = "250" alt="HDFS" />  
选择 apache-ant-1.9.4.zip，解压到自己指定的某个文件夹下，例如我的是：	/Users/JimberXin/apache-ant-1.9.4接着，在根目录下，找到 .bash_profile（如没有自己创建一个）添加ant的路径
	cd  /	vi .bash_profile在打开的文件里，在末尾最后添加如下命令：	export ANT_HOME=/Users/JimberXin/apache-ant-1.9.4	export PATH=$PATH:$ANT_HOME/bin按ecs退出， ZZ保存后退出	
	source .bash_profile现在，可以查看版本信息了	ant -v
显示结果如下：
	Apache Ant(TM) version 1.9.4 compiled on April 29 2014	Trying the default build file: build.xml	Buildfile: build.xml does not exist!	Build failed如果还是发现ant没找到，输入如下命令，确定下载的ant没问题	/Users/JimberXin/apache-ant-1.9.4/bin/ant –version
显示结果如下：	
	Apache Ant(TM) version 1.9.4 compiled on April 29 2014

###  2.	 编译hadoop-eclipse 插件
由于自己的hadoop版本是2.6.0, eclipse版本是4.4.0，比网上很多参考文章的版本都要高，所以在eclipse配置hadoop时自己编译了hadoop
首先下载源代码：将github上的源代码位置拷贝下载到当钱文件夹	git clone https://github.com/winghc/hadoop2x-eclipse-plugin.git下载完后，进入源码所在位置	
	cd source/contrib/eclipse-plugin查看下路径	
	/Users/JimberXin/hadoop-2.6.0/hadoop2x-eclipse-plugin/src/contrib/eclipse-plugin编译源码，输入如下命令：	ant jar -Dversion=2.6.0 -Declipse.home=/Users/JimberXin/eclipse -Dhadoop.home=/Users/JimberXin/hadoop-2.6.0其中， 

* -Dversion为hadoop 版本， 这里是2.6.0* -Declipse.home 为eclipse的位置* -Dhaddop.home为hadoop的位置

<img src="http://jimber.qiniudn.com/QQ20150304-2@2x.jpg" width = "450" height = "200" alt="HDFS" />

hadoop2.6.0开始编译编译过程依赖ivy，需要的时间有点久，编译完了以后，文件放在：
	/build/contrib/eclipse-plugin/hadoop-eclipse-plugin-2.6.0.jar下，
具体为：
	/Users/JimberXin/hadoop-2.6.0/hadoop2x-eclipse-plugin/build/contrib/eclipse-plugin/hadoop-eclipse-plugin-2.6.0.jar
	
### 二、eclipse下hadoop插件的配置

##### 1. 拷贝插件
将上述编译生成的hadoop-eclipse-plugin.jar拷贝放在eclipse目录下的plugin文件夹下，重启eclipse

##### 2. 修改hadoop路径
打开eclipse后，在eclipse的偏好设置里（mac系统在此处，如果是windows或者linux系统，在window->preference里），选择Map/Reduce的安装位置，即是hadoop的安装位置 
	
	/Users/JimberXin/hadoop-2.6.0
<img src="http://jimber.qiniudn.com/QQ20150304-5@2x.jpg" width = "450" height = "200" alt="HDFS" />	

##### 3. 配置Map/Reduce视图* window -> Open Perspective -> other-> Map/Reduce -> 点击“OK”* windows -> show view -> other -> Map/Reduce Locations -> 点击“OK”##### 4. 配置 Map/Reduce Locations选项在“Map/Reduce Locations” Tab页 点击有房图标<大象+>或者在空白的地方右键，选择“New Hadoop location…”，弹出对话框“New hadoop location…”
<img src="http://jimber.qiniudn.com/QQ20150304-3@2x.jpg" width = "400" height = "100" alt="HDFS" />
<img src="http://jimber.qiniudn.com/QQ20150304-4@2x.jpg" width = "450" height = "350" alt="HDFS" />
参数说明如下：  1. Location name: 任意    2. map/reduce master：与mapred-site.xml里mapred.job.tracker设置一致3. DFS master：与core-site.xml里fs.default.name设置一致。 4. User name: 服务器上运行hadoop服务的用户名，这里是JimberXin### 三、新建项目并运行程序
File --> New -->Other --> Map/Reduce Project。项目名可以随便取，如wordCounts。   
新建源文件WordCount.java，代码如下：

	package wordCounts;
	import java.io.IOException;  
	import java.util.*;  
	import org.apache.hadoop.fs.Path;  
	import org.apache.hadoop.conf.*;  
	import org.apache.hadoop.io.*;  
	import org.apache.hadoop.mapred.*;  
	import org.apache.hadoop.util.*;  


	public class WordCount {
	
	// Map Class
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> { 
		private final static IntWritable one = new IntWritable(1);  
		private Text word = new Text();  
		  
		// map function
		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {  
			String line = value.toString();  
			StringTokenizer tokenizer = new StringTokenizer(line);  
			while (tokenizer.hasMoreTokens()) {  
					word.set(tokenizer.nextToken());  
					output.collect(word, one);  
				}  
			}  
		} 
	
	// Reduce Class
	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {  
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {  
			int sum = 0;  
			while (values.hasNext()) {  
					sum += values.next().get();  
				}  
				output.collect(key, new IntWritable(sum));  
		 	}  
		}  

	public static void main(String[] args) throws Exception {  
		JobConf conf = new JobConf(WordCount.class);  
		conf.setJobName("wordcount");  
		  
		conf.setOutputKeyClass(Text.class);  
		conf.setOutputValueClass(IntWritable.class);  
		  
		conf.setMapperClass(Map.class);  
		conf.setReducerClass(Reduce.class);  
		  
		conf.setInputFormat(TextInputFormat.class);  
		conf.setOutputFormat(TextOutputFormat.class);  
		  
		FileInputFormat.setInputPaths(conf, new Path(args[0]));  
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));  
		  
		JobClient.runJob(conf);  
	}  
	}
点击WordCount.java，右键Run As --> Run Configurations  
在打开的设置界面中，进行如下设置： 
 
最左侧项目中，选择 Java application
右边名字 Name 那里自定义，这里为 WordCount

第一个选项：Main 选项下的设置：  

* Project 默认是当前的工程文件 wordCounts
* Main class 是运行的主类，这里根据代码是wordCounts包下的WordCount类

设置好的界面如下图所示：  
<img src="http://jimber.qiniudn.com/QQ20150306-1@2x.jpg" width = "450" height = "320" alt="HDFS" />

第二个选项： Arguments选项下的设置：

这里需要填写的是项目的输入目录以及输出目录 
hdfs://localhost:9000为HDFS的地址，其中

* 第一行 hdfs://localhost:9000/temp/input  中 /temp/input 为HDFS的输入目录
* 第二行 hdfs://localhost:9000/temp/output 中 /temp/output 为HDFS的输出目录

设置好的界面如下图： 
 
<img src="http://jimber.qiniudn.com/QQ20150306-2@2x.jpg" width = "500" height = "320" alt="HDFS" />

在输入文件夹下需要放有需要处理的文件，上一篇《Hadoop-on-mac.md》文章<https://github.com/JimberXin/Hadoop-in-Action/blob/master/Hadoop-config.md>已经有详细说明，这里简单说明一下：

首先在本地文件夹下，创建输入文件夹input
	$ mkdir input在input文件夹下创建三个文本文件f1和f2  输入命令
	$ echo “Hello World Bye World” > f1	$ echo “Hello Hadoop Bye Hadoop” > f2
	$ echo "Hello Mac Bye Mac" > f3
接着，在HDFS上创建新文件夹/temp/input，  
输入命令
	$ bin/hadoop fs –mkdir /temp	$ bin/hadoop fs –mkdir /temp/input最后，从本地文件夹向HDFS文件夹上传文件，  输入命令
	$ bin/hadoop fs –put input /temp/input该命令将本地文件input下的文件拷贝到HDFS文件夹上的/temp/input上

设置完毕后，点击Apply, 然后点击 Run。  
如果一切都配置正常，会在 hadoop 的 /temp/output文件夹下生成两个输出文件，界面如下图所示：
<img src="http://jimber.qiniudn.com/QQ20150307-1@2x.jpg" width = "400" height = "220" alt="HDFS" />

打开其中的part-00000,显示运行结果如下：

	Bye	2
	Goodbye	1
	Hadoop	2
	Hello	3
	Mac	2
	World	2
至此，Mac下elipse环境下Hadoop的配置完成。




