package my.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.javatuples.Pair;

public class Stats {
	private static Stats instance = null;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private List<IP> listIP = new ArrayList<>();
	private Set<String> uniqueIP = new HashSet<>();
	private ConcurrentHashMap<String, Pair<Long, Date>> ipRequests = new ConcurrentHashMap<>();

	private Map<String, Integer> countURL = new TreeMap<>();
	private long totalRequests = 0;
	private long openConnections = 0;
	private String firstIP;

	public synchronized static Stats getInstance() {
		if (instance == null) {
			instance = new Stats();
		}
		return instance;
	}

	public synchronized String getReport() {
		String table1 = "<html><head><center><font size=10>STATISTICS</font></center></head>"
				.concat("<table border = 1><tr><b>Total requests: </b>"
						+ totalRequests + "<p><b>Unique requests: </b>"
						+ uniqueIP.size() + "<p><b>Open connections:  </b>"
						+ openConnections + "<tr></table>");
		//
		String table2 = "<table border = 1><tr><b>REQUESTS</b></tr><tr><th>IP</th><th>Count</th><th>Last Connection</th></tr>";

		for (Entry<String, Pair<Long, Date>> k : ipRequests.entrySet()) {
			table2 += "<tr><th>" + k.getKey() + "</th>"
					+
					// "<th>" + k.getValue() + "</th>"+
					"<th>" + k.getValue().getValue(0) + "</th>" + "<th>"
					+ dateFormat.format(k.getValue().getValue(1)) + "</tr>";// dateFormat.format()
		}
		table2.concat("</table>");

		String table3 = "<table border = 1><tbody><tr><b>REDIRECTS</b><tr><th>URL</th><th>Count</th></tbody></tr>";
		for (Entry<String, Integer> k : countURL.entrySet()) {
			table3 += "<tr><th>" + k.getKey() + "</th>" + "<th>" + k.getValue()
					+ "</th></tr>";
		}
		table3.concat("</table>");

		String table4 = "<table border = 1><tr><th>IP</th><th>URI</th><th>Timestamp</th><th>Sent bytes</th><th>Recieved bytes</th>"
				.concat("<th>Speed(bytes/sec)</th></tr></tbody>");
		for (IP cip : listIP) {
			table4 += "<tr><th>" + cip.getIP() + "</th>" + "<th>"
					+ cip.getURL() + "</th>" + "<th>" + cip.getDate() + "</th>"
					+ "<th>" + cip.getSentBytes() + "</th>" + "<th>"
					+ cip.getRecieviedBytes() + "</th>" + "<th>"
					+ cip.getSpeed() + "</th></tr>";
		}
		table4.concat("</table></html>");
		return table1 + table2 + table3 + table4;
	}

	public synchronized void addConnection(IP ip) {
		if (listIP.size() > 15)
			listIP.remove(0);
		listIP.add(ip);
	}

	public synchronized void setTotalRequests() {//
		totalRequests++;
	}

	public synchronized void setFirstIP(String ip) {
		firstIP = ip;
	}

	public synchronized String getFirstIP() {
		return firstIP;
	}

	public synchronized void registerURL(String url) {
		if (countURL.containsKey(url)) {
			countURL.put(url, new Integer(countURL.get(url) + 1));
		} else {
			countURL.put(url, new Integer(1));
		}
	}

	public synchronized void registerRequestFromIp(String ip, Date localDateTime) {
		if (ipRequests.containsKey(ip)) {
			ipRequests.put(ip, Pair.with(ipRequests.get(ip).getValue0() + 1,
					localDateTime));
		} else {
			ipRequests.put(ip, Pair.with(1L, localDateTime));
		}
	}

	public synchronized void setCountUniqueConnection(String s) {
		if (!s.equals("/favicon.ico"))
			uniqueIP.add(s);
	}

	public synchronized void incrOpenCon() {
		openConnections++;
	}

	public synchronized void decrOpenCon() {
		openConnections--;
	}
}