package jmri.jmrit.vsdecoder;

/*
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under 
 * the terms of version 2 of the GNU General Public License as published 
 * by the Free Software Foundation. See the "COPYING" file for a copy
 * of this license.
 * <P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 * <P>
 *
 * @author			Mark Underwood Copyright (C) 2011
 * @version			$Revision$
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JComponent;
import javax.swing.AbstractButton;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class MomentarySoundEvent extends SoundEvent implements PropertyChangeListener {

    JButton button;

    Trigger t; // used in setXml as a temporary holder for creating the
               // event listener class.
    ButtonTrigger bt; // used in setupButtonAction() as a temporary holder
                      // for creating the button listeners.

    public MomentarySoundEvent() {
	this(null, null);
    }

    public MomentarySoundEvent(String n) {
	this(n, n);
    }

    public MomentarySoundEvent(String n, String bl) {
	super(n, bl);
	button = null;
    }

    public boolean hasButton() {
	if ((buttontype == ButtonType.NONE) || (buttontype == ButtonType.ENGINE) || (button == null))
	    return(false);
	else
	    return(true);
    }

    public void setButton(JButton b) {
	button = b;
    }

    public JComponent getButton() {
	return(button);
    }

    public void setButtonLabel(String bl) {
	button.setText(bl);
    }

    public String getButtonLabel() {
	return(button.getText());
    }

    private void mouseDown() {
    }

    protected ButtonTrigger setupButtonAction(Element te) {
	MouseListener ml;
	bt = new ButtonTrigger(te.getAttributeValue("name"));
	button_trigger_list.put(bt.getName(), bt);
	log.debug("new ButtonTrigger " + bt.getName() + " type " + buttontype.toString());
	button.addMouseListener(bt);
	return(bt);  // cast OK since we just instantiated it up above.
    }

    public Element getXml() {
	Element me = new Element("SoundEvent");
	me.setAttribute("name", name);
	me.setAttribute("label", me.getText());
	for (Trigger t : trigger_list.values()) {
	    me.addContent(t.getXml());
	}

	return(me);
    }

    public void setXml(Element el) {
	this.setXml(el, null);
    }

    @Override
    public void setXml(Element el, VSDFile vf) {
	Element te;
	String bav;

	// Create the button first (put this in constructor?)
	button = new JButton();

	// Handle common stuff.
	super.setXml(el, vf);

	// We know it's momentary, or this class wouldn't have been constructed.
	button.setText(el.getAttributeValue("label"));

	for (ButtonTrigger bt : button_trigger_list.values()) {
	    log.debug("Button Trigger: " + bt.getName());
	    log.debug("  Target: " + bt.getTarget().getName());
	    log.debug("  Action: " + bt.getTargetAction().toString());
	} 

	MouseListener [] listeners = button.getListeners(MouseListener.class);
	for (MouseListener l : listeners) {
	    log.debug("Listener: " + l.toString());
	}

    }  // end setXml()

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MomentarySoundEvent.class.getName());
    
}