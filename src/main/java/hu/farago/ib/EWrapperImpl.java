package hu.farago.ib;

import hu.farago.ib.dto.IBError;
import hu.farago.ib.dto.OpenOrder;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReaderSignal;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.vaadin.spring.annotation.UIScope;

@Component
@UIScope
public class EWrapperImpl implements EWrapper {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private EReaderSignal readerSignal;
	private EClientSocket clientSocket;
	protected int currentOrderId = -1;
	
	@Value("${trdr.tws.host}")
	private String host;
	@Value("${trdr.tws.port}")
	private int port;
	@Value("${trdr.tws.account}")
	private String account;
	
	@Autowired
	private EventBus eventBus;
	
	@PostConstruct
    public void init() {
		readerSignal = new EJavaSignal();
		clientSocket = new EClientSocket(this, readerSignal);
		clientSocket.eConnect(host, port, 0);
		
		Thread thread = new Thread(new MessageReceiver(clientSocket, readerSignal, eventBus));
		thread.start();
	}

	public EReaderSignal getReaderSignal() {
		return readerSignal;
	}

	public EClientSocket getClientSocket() {
		return clientSocket;
	}
	
	public int getCurrentOrderId() {
		return currentOrderId;
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void accountDownloadEnd(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummary(int arg0, String arg1, String arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummaryEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountUpdateMulti(int arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountUpdateMultiEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bondContractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commissionReport(CommissionReport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectAck() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void contractDetails(int arg0, ContractDetails arg1) {
		LOGGER.info("ContractDetails arrived: " + arg1.toString());
		eventBus.post(arg1);
	}

	@Override
	public void contractDetailsEnd(int arg0) {
		LOGGER.info("ContractDetails end: " + arg0);
	}

	@Override
	public void currentTime(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deltaNeutralValidation(int arg0, DeltaNeutralContract arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayGroupList(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayGroupUpdated(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Exception arg0) {
		errorHandling(0, 0, arg0.getMessage());
	}

	@Override
	public void error(String arg0) {
		errorHandling(0, 0, arg0);
	}

	@Override
	public void error(int arg0, int arg1, String arg2) {
		errorHandling(arg0, arg1, arg2);
	}
	
	private void errorHandling(int arg0, int arg1, String arg2) {
		IBError error = new IBError(arg0, arg1, arg2);
		LOGGER.error(error.toString());
		eventBus.post(error);
	}

	@Override
	public void execDetails(int arg0, Contract arg1, Execution arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execDetailsEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fundamentalData(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalData(int arg0, String arg1, double arg2, double arg3,
			double arg4, double arg5, int arg6, int arg7, double arg8,
			boolean arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void managedAccounts(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marketDataType(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextValidId(int orderId) {
		LOGGER.debug("Next Valid Id: ["+orderId+"]");
		currentOrderId = orderId;		
	}

	@Override
	public void openOrder(int arg0, Contract arg1, Order arg2, OrderState arg3) {
		eventBus.post(new OpenOrder(arg0, arg1, arg2, arg3));
	}

	@Override
	public void openOrderEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void orderStatus(int arg0, String arg1, double arg2, double arg3,
			double arg4, int arg5, int arg6, double arg7, int arg8, String arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void position(String arg0, Contract arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionMulti(int arg0, String arg1, String arg2,
			Contract arg3, double arg4, double arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionMultiEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void realtimeBar(int arg0, long arg1, double arg2, double arg3,
			double arg4, double arg5, long arg6, double arg7, int arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFA(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerData(int arg0, int arg1, ContractDetails arg2,
			String arg3, String arg4, String arg5, String arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerDataEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerParameters(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void securityDefinitionOptionalParameter(int arg0, String arg1,
			int arg2, String arg3, String arg4, Set<String> arg5,
			Set<Double> arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void securityDefinitionOptionalParameterEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEFP(int arg0, int arg1, double arg2, String arg3,
			double arg4, int arg5, String arg6, double arg7, double arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickGeneric(int arg0, int arg1, double arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickOptionComputation(int arg0, int arg1, double arg2,
			double arg3, double arg4, double arg5, double arg6, double arg7,
			double arg8, double arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickPrice(int arg0, int arg1, double arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSize(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSnapshotEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickString(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccountTime(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAccountValue(String arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepth(int arg0, int arg1, int arg2, int arg3,
			double arg4, int arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepthL2(int arg0, int arg1, String arg2, int arg3,
			int arg4, double arg5, int arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNewsBulletin(int arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updatePortfolio(Contract arg0, double arg1, double arg2,
			double arg3, double arg4, double arg5, double arg6, String arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyAndAuthCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyAndAuthMessageAPI(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyCompleted(boolean arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyMessageAPI(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
