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
 * �ν���Ʈ ���ؼ�
 * ª�� ����ϴ� ������ ��� ���� ID�� �޾ƿͼ� ����ϴ� �� ���ٴ� ª�� ����ϰ� �ݳ��ϴ� ���� ����
 * �������� ���� ��� �̱������� �Ǿ� �ֱ� ������ �ν��Ͻ��� ���������� ����� ���ؼ��� �������� Scope�� ������ �־�� ��
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
