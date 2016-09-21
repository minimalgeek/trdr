package hu.farago.ib;

import hu.farago.AbstractRootTest;
import hu.farago.ib.EWrapperImpl;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

public class EWrapperImplTest extends AbstractRootTest {

	@Autowired
	private EWrapperImpl wrapper;
		
	@After
	public void after() throws InterruptedException {
		Thread.sleep(1000);
	}
	
	@Test
	public void contractRequestTest() {
		EClientSocket client = wrapper.getClientSocket();
		
		Contract contract = new Contract();
        contract.symbol("AAPL");
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("ISLAND");
		
        client.reqContractDetails(wrapper.getCurrentOrderId() + 1, contract);
	}
}
