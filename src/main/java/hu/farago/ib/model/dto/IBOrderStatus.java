package hu.farago.ib.model.dto;

public class IBOrderStatus {

	private int orderId;
	private String status;
	private double filled;
	private double remaining;
	private double avgFillPrice;
	private int permId;
	private int parentId;
	private double lastFillPrice;
	private int clientId;
	private String whyHeld;

	public IBOrderStatus(int orderId, String status, double filled,
			double remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld) {
		this.orderId = orderId;
		this.status = status;
		this.filled = filled;
		this.remaining = remaining;
		this.avgFillPrice = avgFillPrice;
		this.permId = permId;
		this.parentId = parentId;
		this.lastFillPrice = lastFillPrice;
		this.clientId = clientId;
		this.whyHeld = whyHeld;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getFilled() {
		return filled;
	}

	public void setFilled(double filled) {
		this.filled = filled;
	}

	public double getRemaining() {
		return remaining;
	}

	public void setRemaining(double remaining) {
		this.remaining = remaining;
	}

	public double getAvgFillPrice() {
		return avgFillPrice;
	}

	public void setAvgFillPrice(double avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}

	public int getPermId() {
		return permId;
	}

	public void setPermId(int permId) {
		this.permId = permId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public double getLastFillPrice() {
		return lastFillPrice;
	}

	public void setLastFillPrice(double lastFillPrice) {
		this.lastFillPrice = lastFillPrice;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getWhyHeld() {
		return whyHeld;
	}

	public void setWhyHeld(String whyHeld) {
		this.whyHeld = whyHeld;
	}

}
