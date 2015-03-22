//Author: Junbo Xin
//Date: 2015/03/19
//Description: Implementation of student score using Hadoop 2.6.0

import java.io.IOException;  
import java.util.*;  

import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.conf.*;  
import org.apache.hadoop.io.*;  
import org.apache.hadoop.mapred.*;  
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.*;  


public class Score extends Configured implements Tool{
	
	public static class myMap extends Mapper<LongWritable, Text, Text, IntWritable>{
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String line = value.toString();
			System.out.println(line);
			//将输入的数据按行进行分割
			StringTokenizer tokenizerArticle = new StringTokenizer(line, "\n");
			//分别对每一行进行处理
			while(tokenizerArticle.hasMoreTokens()){
				//每行按空格划分
				StringTokenizer tokenizerLine = new StringTokenizer(tokenizerArticle.nextToken());
				String strName = tokenizerLine.nextToken();      // 姓名部分
				String strScore = tokenizerLine.nextToken();    //成绩部分
				Text name = new Text(strName);                  // 学生姓名
				int scoreInt = Integer.parseInt(strScore);     //学生成绩score of student
				context.write(name, new IntWritable(scoreInt)); //输出姓名和成绩
			}
		}
	}
	
	public static class myReduce extends Reducer<Text, IntWritable, Text, IntWritable>{
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int sum = 0;
			int count = 0;
			Iterator<IntWritable> iterator = values.iterator();
			while(iterator.hasNext()){
				sum += iterator.next().get();  //计算总分
				count ++;   //计算总科目数
				
			}
			int average = (int)sum/count;   //计算平均成绩
			context.write(key, new IntWritable(average));
		}
	}	
	
	// overide of class Tool
	public int run(String[] args) throws Exception{
		Job job = new Job(getConf());   // Job class is already deprecated??!
		
		job.setJarByClass(Score.class);
		job.setJobName("StudentScore");
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(myMap.class);
		job.setCombinerClass(myReduce.class);
		job.setReducerClass(myReduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		boolean success = job.waitForCompletion(true);

		return success? 0:1;
	}
	
	public static void main(String[] args) throws Exception{
		int ret = ToolRunner.run(new Score(), args);
		System.exit(ret);
	}
	
}
