package hu.farago.ib;

import hu.farago.ib.dto.IBError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;

public class MessageReceiver implements Runnable {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private EClientSocket clientSocket;
	private EReaderSignal readerSignal;
	private EventBus eventBus;

	private EReader reader;

	public MessageReceiver(EClientSocket clientSocket,
			EReaderSignal readerSignal, EventBus eventBus) {
		super();
		this.clientSocket = clientSocket;
		this.readerSignal = readerSignal;
		this.eventBus = eventBus;
		this.reader = new EReader(clientSocket, readerSignal);
		reader.start();
	}

	@Override
	public void run() {
		while (clientSocket.isConnected()) {
			readerSignal.waitForSignal();
			try {
				reader.processMsgs();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				eventBus.post(new IBError(e.getMessage()));
			}
		}
	}

}
