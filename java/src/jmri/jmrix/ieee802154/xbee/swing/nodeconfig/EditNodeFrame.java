package jmri.jmrix.ieee802154.xbee.swing.nodeconfig;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import jmri.jmrix.ieee802154.xbee.XBeeNode;
import jmri.jmrix.ieee802154.xbee.XBeeTrafficController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;


/**
 * Frame for Editing Nodes
 *
 * @author	Bob Jacobsen Copyright (C) 2004
 * @author	Dave Duchamp Copyright (C) 2004
 * @author	Paul Bender Copyright (C) 2013,2016
 */
public class EditNodeFrame extends jmri.jmrix.ieee802154.swing.nodeconfig.EditNodeFrame {

    private XBeeTrafficController xtc = null;
    private javax.swing.JTextField nodeIdentifierField = new javax.swing.JTextField();
    private NodeConfigFrame parent = null;


    /**
     * Constructor method
     *
     * @param tc, the XBeeTrafficController assocaiated with this connection.
     * @param source, the NodeConfigFrame that started this add.
     */
    public EditNodeFrame(XBeeTrafficController tc,XBeeNode node,NodeConfigFrame source) {
        super(tc,node);
        xtc = tc;
        parent = source;
        curNode = node;
    }

    /**
     * Initialize the config window
     */
    public void initComponents() {
        setTitle(Bundle.getMessage("EditNodeWindowTitle"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Set up node address and node type
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(Bundle.getMessage("LabelNodeAddress") + " "));
        panel.add(nodeAddrField);
        nodeAddrField.setToolTipText(Bundle.getMessage("TipNodeAddress"));
        panel.add(new JLabel(Bundle.getMessage("LabelNodeAddress64") + " "));
        panel.add(nodeAddr64Field);
        nodeAddr64Field.setToolTipText(Bundle.getMessage("TipNodeAddress64"));
        panel.add(new JLabel(Bundle.getMessage("LabelNodeIdentifier") + " "));
        panel.add(nodeIdentifierField);
        nodeIdentifierField.setToolTipText(Bundle.getMessage("TipNodeIdentifier"));

        initAddressBoxes();
        contentPane.add(panel);

        // Set up buttons
        JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());
        editButton.setText(Bundle.getMessage("ButtonEdit"));
        editButton.setVisible(true);
        editButton.setToolTipText(Bundle.getMessage("TipEditButton"));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                editButtonActionPerformed();
            }
        });
        panel4.add(editButton);
        panel4.add(cancelButton);
        cancelButton.setText(Bundle.getMessage("ButtonCancel"));
        cancelButton.setVisible(true);
        cancelButton.setToolTipText(Bundle.getMessage("TipCancelButton"));
        panel4.add(cancelButton);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });
        contentPane.add(panel4);

        // pack for display
        pack();
    }

    /**
     * Method to handle edit button
     */
    public void editButtonActionPerformed() {
        if(nodeAddr64Field.getText().equals("") &&
           nodeAddrField.getText().equals("")) {
           // no address, just return.
           return;
        }

        // to update the node's associated XBee Device, we have to 
        // create a new one, as the library provides no way to update 
        // the RemoteXBeeDevice object.

        // Check that a node with this address does not exist
        // if the 64 bit address field is blank, use the "Unknown" address".
        XBee64BitAddress guid;
        if(!(nodeAddr64Field.getText().equals(""))) {
           byte GUID[] = jmri.util.StringUtil.bytesFromHexString(nodeAddr64Field.getText());
           guid = new XBee64BitAddress(GUID);
        } else {
           guid = XBee64BitAddress.UNKNOWN_ADDRESS;
        }
        // if the 16 bit address field is blank, use the "Unknown" address".
        XBee16BitAddress address;
        if(!(nodeAddrField.getText().equals(""))){
           byte addr[] = jmri.util.StringUtil.bytesFromHexString(nodeAddrField.getText());
           address = new XBee16BitAddress(addr);
        } else {
           address = XBee16BitAddress.UNKNOWN_ADDRESS;
        }
        String Identifier = nodeIdentifierField.getText();
        // create the RemoteXBeeDevice for the node.
        RemoteXBeeDevice remoteDevice = remoteDevice = new RemoteXBeeDevice(xtc.getXBee(),
              guid,address,Identifier);
        // get a XBeeNode corresponding to this node address if one exists
        XBeeNode existingNode = (XBeeNode) xtc.getNodeFromXBeeDevice(remoteDevice);
        if (existingNode != null) {
            javax.swing.JOptionPane.showMessageDialog(this,Bundle.getMessage("Error1",remoteDevice),Bundle.getMessage("EditNodeErrorTitle"),JOptionPane.ERROR_MESSAGE);
            return;
        }
        // save the old remote device
        RemoteXBeeDevice oldDevice = ((XBeeNode)curNode).getXBee();
        // and then add the new device to the network
        xtc.getXBee().getNetwork().addRemoteDevice(remoteDevice);

        // remove the old one from the network
        xtc.getXBee().getNetwork().removeRemoteDevice(oldDevice);

        //and update the current node.
        ((XBeeNode)curNode).setXBee(remoteDevice);

        parent.nodeListChanged();

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Method to handle cancel button
     */
    public void cancelButtonActionPerformed() {
        // Reset 
        curNode = null;
        // Switch buttons
        editButton.setVisible(true);
        cancelButton.setVisible(false);
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    // Initilize the text boxes for the addresses.
    protected void initAddressBoxes() {
        nodeAddrField.setText(jmri.util.StringUtil.hexStringFromBytes(curNode.getUserAddress()));
        nodeAddr64Field.setText(jmri.util.StringUtil.hexStringFromBytes(curNode.getGlobalAddress()));
        nodeIdentifierField.setText(((XBeeNode)curNode).getIdentifier());
    }

    private final static Logger log = LoggerFactory.getLogger(AddNodeFrame.class.getName());

}
