package my.group.mapreduce;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.Mapper;

import java.io.IOException;

/**
 * Mapper模板。请用真实逻辑替换模板内容
 */
public class MyMapper implements Mapper {
    private Record key;
    private Record value;

    public void setup(TaskContext context) throws IOException {
        key = context.createMapOutputKeyRecord();
        value = context.createMapOutputValueRecord();
    }
    
    public int timeToDayIndex(String time){
    	String[] t1 = time.split(" ");
    	String[] t2 = t1[0].split("-");
    	int month = Integer.parseInt(t2[1]);
    	int day = Integer.parseInt(t2[2]);
    	if (month == 11){
    		return day - 18;
    	}
    	else{
    		return day + 12;
    	}
    }
    
    public int timeToHour(String time){
    	String[] t = time.split(" ");
    	return Integer.parseInt(t[1]);
    }

    public void map(long recordNum, Record record, TaskContext context) throws IOException {
    	
//    	int lastDayIndex = 28; // 2014-12-16
//    	int lastDayIndex = 29; // 2014-12-17
    	int lastDayIndex = 30; // 2014-12-18
    	
        String user_id = record.getString(0);
        String item_id = record.getString(1);
        Long behavior_type = record.getBigint(2);
        String user_geohash = record.getString(3);
        String item_category = record.getString(4);
        String time = record.getString(5);

        int dayIndex = timeToDayIndex(time);
        int hour = timeToHour(time);
        
        if (dayIndex > lastDayIndex){
        	return;
        }
        
        key.setString(0, user_id);
        
        value.setBigint(0, behavior_type);
        value.setBigint(1, (long)(lastDayIndex-dayIndex));
        value.setBigint(2, (long)hour);
        
        context.write(key, value);
    }

    public void cleanup(TaskContext context) throws IOException {

    }
}