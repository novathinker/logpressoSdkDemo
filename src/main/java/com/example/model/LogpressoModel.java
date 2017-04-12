package com.example.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.logpresso.client.Cursor;
import com.logpresso.client.Logpresso;
import com.logpresso.client.Query;
import com.logpresso.client.Tuple;

/**
 * Created by hando.kim on 2017-03-25.
 * 싱글턴패턴으로 생성
 * 로그프레소 컨넥션은 비동기로 여러 쿼리를 동시에 수행할 수 있도록 되어 있기 때문에
 * 롱런 쿼리의 경우 하나의 컨넥션을 받아 여러 스레드가 사용하는 것이 바람직함
 */

public class LogpressoModel {
    private static Logpresso logpresso;
    private static volatile LogpressoModel instance = null;

    //private Connection() {}

    public static LogpressoModel getInstance(){
        if (instance == null) {
            synchronized(LogpressoModel.class) {
                if (instance == null) instance = new LogpressoModel();
                try {
                    logpresso = new Logpresso();
                    logpresso.connect("localhost", 8888, "root", "logpresso");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public Logpresso getClient() throws IOException{
        return  logpresso;
    }
    
    public int getQueryId(String queryString) throws IOException {
    	int  queryId = 0;
    	try {
    		queryId = logpresso.createQuery(queryString);
    		logpresso.startQuery(queryId);
    	} catch (Exception e) {
            e.printStackTrace();
        }
    	return queryId;
    }

    public Query getQuery(int queryId) throws IOException{
    	Query query = null;
    	try {
    		query = logpresso.getQuery(queryId);
    	} catch (Exception e) {
            e.printStackTrace();
        }
    	return query;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map> getResult(int queryId, int last,int limit) throws IOException {
    	List<Map> result = new ArrayList<Map>();
    	try {
    		Query query = logpresso.getQuery(queryId);
    		String status = query.getStatus();
    		while (!status.equals("Ended") && !status.equals("Cancelled") && limit > query.getLoadedCount()) {
    			Thread.sleep(50);
    			status = query.getStatus();
    		}
    		//limit = Math.min(limit, (int)query.getLoadedCount());
    		Map resultSet = logpresso.getResult(queryId, last, limit);
    		result = (List<Map>) resultSet.get("result");
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return result;
    }
    
    public void removeQuery(int queryID) throws IOException {
    	try {
    		logpresso.removeQuery(queryID);
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void stopQuery(int queryID) throws IOException {
    	try {
    		logpresso.stopQuery(queryID);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    @SuppressWarnings("rawtypes")
	public List<Map> getTree(int queryID) throws IOException {
    	List<Map> result = new ArrayList<Map>();
    	Cursor cursor = null;
    	try {
    		String queryString = "system queries | search id == "+ queryID 
    				+ " | fields last_started, elapsed, commands | explode commands | parsemap overlay=t field=commands "
    				+ " | fields - commands | eval last_started = string(last_started, \"yyyy-MM-dd HH:mm:ss\")";
    		cursor = logpresso.query(queryString);
    		while(cursor.hasNext()) {
                Tuple tuple = cursor.next();
                Map map= new HashMap();
                map = tuple.toMap();
                result.add(map);
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (cursor != null)
                cursor.close();
    	}
    	return result;
    }
    
    public void closeClient() throws IOException {
        try {
            if ( logpresso != null)
                logpresso.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
