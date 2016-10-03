package hu.farago.web;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = VaadinUI.class)
public class WebsocketServlet extends VaadinServlet {

    private static final long serialVersionUID = 1L;

}