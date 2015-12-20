package my.group.mapreduce;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Combiner模板。请用真实逻辑替换模板内容
 */
public class MyCombiner implements Reducer {

    public void setup(TaskContext context) throws IOException {

    }

    public void reduce(Record key, Iterator<Record> values, TaskContext context) throws IOException {
    	
    }

    public void cleanup(TaskContext arg0) throws IOException {

    }
}
