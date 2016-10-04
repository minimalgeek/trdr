package hu.farago.web.component.order;

import hu.farago.ib.model.dto.IBOrderStatus;
import hu.farago.ib.service.order.OrderService;
import hu.farago.web.component.GridWithActionList;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class OrderStatusPanel extends VerticalLayout {

	private static final long serialVersionUID = -5346838633309699275L;

	private GridWithActionList orders;
	private BeanItemContainer<IBOrderStatus> ordersContainer;
	
	private OrderService orderService;
	private EventBus eventBus;
	
	private Button refreshButton;
	
	@Autowired
	public OrderStatusPanel(OrderService os, EventBus eb) {
		this.orderService = os;
		this.eventBus = eb;
		this.eventBus.register(this);
		this.refreshButton = new Button("Refresh list", (e) -> {
			this.ordersContainer.removeAllItems();
			this.orderService.reqAllOpenOrders();
		});
		this.orders = new GridWithActionList(refreshButton);
		this.ordersContainer = new BeanItemContainer<IBOrderStatus>(IBOrderStatus.class);
		this.orders.getGrid().setContainerDataSource(this.ordersContainer);
		
		this.orderService.reqAllOpenOrders();
		
		this.addComponents(orders);
	}
	
	@Subscribe
	public void openOrder(IBOrderStatus orderStatus) {
		this.ordersContainer.addBean(orderStatus);
	}

}
