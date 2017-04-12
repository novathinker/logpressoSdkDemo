package com.example.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;

import com.logpresso.client.Cursor;
import com.logpresso.client.Logpresso;
import com.logpresso.client.Tuple;

/**
 * Created by hando.kim on 2017-03-29.
 * 인스턴트 컨넥션
 * 짧게 사용하는 쿼리의 경우 쿼리 ID를 받아와서 사용하는 것 보다는 짧게 사용하고 반납하는 것을 권장
 * 스프링의 빈은 모두 싱글턴으로 되어 있기 때문에 인스턴스를 개별적으로 만들어 컨넥션을 맺으려면 Scope를 조정해 주어야 함
 */

@Scope("prototype")
public class InstantModel {
	
	private Logpresso logpresso;
	
	private Logpresso getClient() throws IOException{
        try {
            logpresso = new Logpresso();
            logpresso.connect("localhost", 8888, "root", "logpresso");
           // logpresso.connect(host, port, loginName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  logpresso;
    }
    
	public  List<Map<String, Object>> getQuery(String queryString) throws IOException  {
		Cursor cursor = null;
		List<Map<String, Object>> result = new ArrayList<>();
		InstantModel model = new InstantModel();
		try {
			Logpresso client = model.getClient();
			cursor = client.query(queryString);
			while(cursor.hasNext()) {
                Tuple tuple = cursor.next();
                Map<String, Object> map= new HashMap<>();
                map = tuple.toMap();
                result.add(map);
			}
		} finally {
            if (cursor != null)
                cursor.close();
                model.closeClient();
        }
		return result;
	}
	
    private void closeClient() throws IOException {
        try {
            if ( logpresso != null)
                logpresso.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
