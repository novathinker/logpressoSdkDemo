package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.InstantModel;

@RestController
public class HomeController {
	
    private List<Map<String, Object>> getResult(String queryString) throws IOException {
        List<Map<String, Object>> result = null;
        try {
            InstantModel model = new InstantModel();
          	result = model.getQuery(queryString);
        } catch (Exception e){
        	e.printStackTrace();
        }

       	return result;
    }
	
	@RequestMapping(value = "/home/cpuInfo", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> cpuInfo() throws IOException {
        String queryString = "table duration=10m sys_cpu_logs | eval total = kernel + user | sort _time ";
       	return getResult(queryString);
    }
	
	@RequestMapping(value = "/home/speed", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> speed() throws IOException {
        String queryString = "table limit=1 sys_logger_trends | eval rate = round(delta * 1000 / interval) | fields rate";
       	return getResult(queryString);
	}
	
	@RequestMapping(value = "/home/logTrend", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> logTrend() throws IOException {
        String queryString = "table duration=5m sys_logger_trends | eval speed = delta * 1000 / interval | fields _time, speed | sort _time";
       	return getResult(queryString);
	}
	
	@RequestMapping(value = "/home/threads", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> threads() throws IOException {
        String queryString = "proc sys_thread_count() | rename state as label, count as value";
       	return getResult(queryString);
	}
	
	@RequestMapping(value = "/home/inputTable", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> inputTable() throws IOException {
        String queryString = "table duration=10m sys_table_trends | stats sum(count) as count by table | sort limit=10 -count";
       	return getResult(queryString);
	}	
	
	@RequestMapping(value = "/home/javaHeap", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> javaHeap() throws IOException {
        String queryString = "table limit=1 sys_mem_logs | eval rows = array(array(\"Èü ÃÖ´ëÄ¡\", heap_max), array(\"Èü ÇÒ´ç·®\", heap_total), array(\"Èü »ç¿ë·®\", heap_used)) | explode rows | eval category = valueof(rows, 0) | eval value = valueof(rows, 1), value = round(value/1024/1024,0) | fields category, value";
       	return getResult(queryString);
	}	

	@RequestMapping(value = "/home/disk", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> disk() throws IOException {
        String queryString = "table duration=1d sys_disk_logs | search scope == \"partition\" | stats first(used) as used, first(free) as free by partition | eval used = round(used/1024/1024/1024,0), free = round(free/1024/1024/1024,0)";
       	return getResult(queryString);
	}

	@RequestMapping(value = "/home/diskTrend", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> diskTrend() throws IOException {
        String queryString = "table duration=7d sys_disk_logs | search scope == \"partition\" |  timechart span=1h max(round((total - free)/1024/1024/1024,0)) by partition";
       	return getResult(queryString);
	}
	
	@RequestMapping(value = "/home/gc", method = RequestMethod.GET, produces = "application/json")
    private List<Map<String, Object>> gc() throws IOException {
        String queryString = "table duration=10m sys_gc_logs | boxplot duration by type";
       	return getResult(queryString);
	}
}
