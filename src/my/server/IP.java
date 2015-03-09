package my.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IP {
	private String ip = "";
	private String uri = "";
	private Date date = new Date();
	private int sentBytes = 0;
	private int recievedBytes = 0;
	private double speed = 0;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	IP(String ip, String uri, int s_b, int r_b, double speed) {
		this.ip = ip;
		this.uri = uri;
		this.date = new Date();
		this.sentBytes = s_b;
		this.recievedBytes = r_b;
		this.speed = speed;
	}

	public String toString() {
		return "" + ip + " " + uri + " " + date + " " + sentBytes + " "
				+ recievedBytes + " " + speed + "<br>";
	}

	public String getIP() {
		return ip + "";
	}

	public String getURL() {
		return uri + "";
	}

	public String getDate() {
		return dateFormat.format(date) + "";
	}

	public String getSentBytes() {
		return sentBytes + "";
	}

	public String getRecieviedBytes() {
		return recievedBytes + "";
	}

	public String getSpeed() {
		return speed + "";
	}
}