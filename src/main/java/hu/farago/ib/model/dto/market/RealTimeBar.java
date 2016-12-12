package hu.farago.ib.model.dto.market;

public class RealTimeBar {
	public int reqId;
	public long time;
	public double open;
	public double high;
	public double low;
	public double close;
	public long volume;
	public double WAP;
	public int count;

	public RealTimeBar(int reqId, long time, double open, double high, double low, double close, long volume,
			double wAP, int count) {
		super();
		this.reqId = reqId;
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		WAP = wAP;
		this.count = count;
	}

}
