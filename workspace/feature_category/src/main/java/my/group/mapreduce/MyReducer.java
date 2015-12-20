package my.group.mapreduce;

import com.aliyun.odps.data.Record;
import com.aliyun.odps.mapred.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * Reducer模板。请用真实逻辑替换模板内容
 */
public class MyReducer implements Reducer {
    private Record result;

    public void setup(TaskContext context) throws IOException {
        result = context.createOutputRecord();
    }

    public void reduce(Record key, Iterator<Record> values, TaskContext context) throws IOException {

    	long firstDayGap = -10000;
        long lastDayGap = 10000;
        long firstHourGap = -10000;
        long lastHourGap = 10000;

    	long[][] f_day_cnt = new long[4][31];    //最近第k天action数量统计
        
        while (values.hasNext()) {
            Record val = values.next();

            long behavior_type = val.getBigint(0);
            long dayGap = val.getBigint(1);
            long hour = val.getBigint(2);
            
            if (dayGap > firstDayGap){
            	firstDayGap = dayGap;
            }
            if (dayGap < lastDayGap){
            	lastDayGap = dayGap;
            }
            if (24*dayGap+24-hour > firstHourGap){
            	firstHourGap = 24*dayGap+24-hour;
            }
            if (24*dayGap+24-hour < lastHourGap){
            	lastHourGap = 24*dayGap+24-hour;
            }
            
            f_day_cnt[(int)(behavior_type-1)][(int)dayGap] ++;
        }

        long[][] f_cnt = new long[4][31];   //最近k天action数量统计
        long[][] f_day = new long[4][31];   //最近k天活跃天数数量统计
        
        for (int i = 0; i < 4; i ++){
            f_cnt[i][0] = f_day_cnt[i][0];
            f_day[i][0] = f_day_cnt[i][0] > 0 ? 1 : 0;
            for (int j = 1; j < 31; j ++){
                f_cnt[i][j] = f_cnt[i][j-1] + f_day_cnt[i][j];
                f_day[i][j] = f_day[i][j-1] + (f_day_cnt[i][j] > 0 ? 1 : 0);
            }
        }
        
        result.set(0, key.get(0));
        
        result.set(1, firstDayGap);
        result.set(2, lastDayGap);
        result.set(3, firstHourGap);
        result.set(4, lastHourGap);
        
        int startIndex = 5;
        int matSize = 4*29;
        
        for (int i = 0; i < 4; i ++){
        	for (int j = 0; j < 29; j ++){
        		result.set(startIndex+29*i+j, f_cnt[i][j]);
        	}
        }
        
        for (int i = 0; i < 4; i ++){
        	for (int j = 0; j < 29; j ++){
        		result.set(startIndex+matSize+29*i+j, f_day[i][j]);
        	}
        }
        
        context.write(result);
    }

    public void cleanup(TaskContext arg0) throws IOException {

    }
}
