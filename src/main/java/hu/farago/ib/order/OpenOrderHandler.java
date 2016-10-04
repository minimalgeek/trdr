package hu.farago.ib.order;

import hu.farago.ib.model.dao.OpenOrderDAO;
import hu.farago.ib.model.dto.IBOrder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Component
public class OpenOrderHandler {

	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private OpenOrderDAO ooDAO;
	
	@PostConstruct
    public void init() {
		eventBus.register(this);
	}
	
	@Subscribe
	public void openOrder(IBOrder oo){
		ooDAO.save(oo);
	}
	
}
