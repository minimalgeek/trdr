package hu.farago.ib.dto;

import org.springframework.core.style.ToStringCreator;

public class IBError {

	private int arg1;
	private int arg2;
	private String arg3;
	
	public IBError(int arg1, int arg2, String arg3) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
	}

	public int getArg1() {
		return arg1;
	}

	public int getArg2() {
		return arg2;
	}

	public String getArg3() {
		return arg3;
	}
	
	@Override
	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append(arg1)
			.append(arg2)
			.append(arg3);
		return tsc.toString();
	}
}
