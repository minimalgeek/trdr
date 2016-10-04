package hu.farago.web.component;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class GridWithActionList extends VerticalLayout {

	private static final long serialVersionUID = 2052892648300212851L;

	private Grid grid;
	private CssLayout actions;
	
	public GridWithActionList(Button... buttons) {
		this.grid = new Grid();
		this.actions = new CssLayout(buttons);
		this.actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		if (buttons.length > 0){
			buttons[0].setStyleName(ValoTheme.BUTTON_PRIMARY);
			buttons[0].setClickShortcut(ShortcutAction.KeyCode.ENTER);
		}
		
		this.setMargin(true);
		this.setSpacing(true);
		this.grid.setSizeFull();
		this.grid.setResponsive(true);
		this.grid.setColumnReorderingAllowed(true);
		this.setSizeFull();

		this.addComponents(grid, actions);
	}
	
	public Grid getGrid() {
		return grid;
	}
}
