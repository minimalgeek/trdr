package hu.farago.ib.order;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Component
public class IBOrderListener {

	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private IBOrderDAO ooDAO;
	
	@PostConstruct
    public void init() {
		eventBus.register(this);
	}
	
	@Subscribe
	public void orderChanged(IBOrder ibOrder){
		ooDAO.save(ibOrder);
	}
	
}
