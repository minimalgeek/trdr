package hu.farago.ib.model.dto.market;

public class TickPrice {

	public int tickerId;
	public int field;
	public double price;
	public int canAutoExecute;
	
	public TickPrice(int tickerId, int field, double price, int canAutoExecute) {
		super();
		this.tickerId = tickerId;
		this.field = field;
		this.price = price;
		this.canAutoExecute = canAutoExecute;
	}
	
}
