package hu.farago.web.component.order;

import hu.farago.ib.model.dto.IBOrder;
import hu.farago.ib.service.order.OrderService;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class OpenedOrders extends VerticalLayout {

	private static final long serialVersionUID = -5346838633309699275L;

	private Grid orders;
	private BeanItemContainer<IBOrder> ordersContainer;
	private OrderService orderService;
	private EventBus eventBus;
	
	private Button refreshButton;
	
	@Autowired
	public OpenedOrders(OrderService os, EventBus eb) {
		this.orderService = os;
		this.eventBus = eb;
		this.eventBus.register(this);
		this.orders = new Grid();
		this.ordersContainer = new BeanItemContainer<IBOrder>(IBOrder.class);
		this.orders.setContainerDataSource(this.ordersContainer);
		this.orders.setSizeFull();
		
		this.refreshButton = new Button("Refresh list", (e) -> {
			this.ordersContainer.removeAllItems();
			this.orderService.reqAllOpenOrders();
		});
		
		this.orderService.reqAllOpenOrders();
		
		this.setMargin(true);
		this.addComponents(refreshButton, orders);
	}
	
	@Subscribe
	public void openOrder(IBOrder order) {
		this.ordersContainer.addBean(order);
	}

}
