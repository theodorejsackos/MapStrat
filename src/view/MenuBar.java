package view;

import model.SessionModel;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    private SessionModel sesh;
    public MenuBar(SessionModel sm){
        super();
        this.sesh = sm;
    }

}
