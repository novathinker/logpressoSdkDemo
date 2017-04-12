package com.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.LogpressoModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logpresso.client.Query;

@RestController
public class QueryController {
	
	private int getQueryId(String queryString) {
		int queryId = 0;
		try {
			queryId = LogpressoModel.getInstance().getQueryId(queryString);
		} catch (Exception e){
			e.printStackTrace();
		}
		return queryId;
	}
	
	private Query getQuery(int queryId) {
		Query query = null;
		try {
			query = LogpressoModel.getInstance().getQuery(queryId);
		} catch (Exception e){
			e.printStackTrace();
		}
		return query;
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Map> getResult(int queryID, int page) throws IOException  {
		List<Map> result = new ArrayList<Map>();
		int limit = 50;
		int last = (page-1) * limit;
		try {
			List<Map> tempResult = LogpressoModel.getInstance().getResult(queryID, last, limit);
			Iterator<Map> iter = tempResult.iterator();
			while(iter.hasNext()){
				Map row = iter.next();
				Map<String, Object> new_row = new HashMap();
				for( Object key : row.keySet()){
					Object value = row.get(key);
					if (value != null && value.getClass().getName() == "java.util.Date") {
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						new_row.put(key.toString(), sdfDate.format(value));
					} else {
						new_row.put(key.toString(), value);
					}
				}
				result.add(new_row);
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

    public static HashMap<String, Object> convertJsonToObject(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() { };
        HashMap<String, Object> object = objectMapper.readValue(json, typeReference);
        return object;
    }


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/query/result", method = RequestMethod.POST, produces = "application/json")
	private Map getInitQuery(@RequestBody String json, HttpServletRequest request, HttpServletResponse responset) throws IOException {
		Map<String, Object> queryText = convertJsonToObject(json);
		String queryString = queryText.get("queryText").toString();
		int queryID = getQueryId(queryString);
		Query query = getQuery(queryID);
        Map result = new HashMap();
        result.put("result", getResult(queryID, 1));
        result.put("queryID", queryID);
        result.put("count", query.getLoadedCount());
        result.put("status", query.getStatus());
       	return result;
    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query/result/{id}/{page}", method = RequestMethod.GET, produces = "application/json")
	private List<Map> getSubResult(@PathVariable String id, @PathVariable String page) throws IOException {
       	return getResult(Integer.parseInt(id), Integer.parseInt(page));
    }
	

	@RequestMapping(value = "/query/remove/{id}", method = RequestMethod.GET)
	private void removeQuery(@PathVariable String id) throws Exception {
		LogpressoModel.getInstance().removeQuery(Integer.parseInt(id));
		//return getQuery(Integer.parseInt(id)).getStatus();
    }	
	
	@RequestMapping(value = "/query/stop/{id}", method = RequestMethod.GET)
	private String stopQuery(@PathVariable String id) throws Exception {
		LogpressoModel.getInstance().stopQuery(Integer.parseInt(id));
		Thread.sleep(1000);
		return getQuery(Integer.parseInt(id)).getStatus();
    }	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/query/count", method = RequestMethod.GET, produces = "application/json")
	private Map getQueryCount(@RequestParam("queryId") String queryID) throws IOException {
		Query query = getQuery(Integer.parseInt(queryID));
		long count = query.getLoadedCount();
		String status = query.getStatus();
		Map<String, Object> result = new HashMap();
        result.put("count", count);
        result.put("status", status);
       	return result;
    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query/tree/{id}", method = RequestMethod.GET, produces = "application/json")
	private List<Map> getTree(@PathVariable String id) throws IOException {
		return LogpressoModel.getInstance().getTree(Integer.parseInt(id));
	}
	
}
