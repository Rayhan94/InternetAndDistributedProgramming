//this is the GUI
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2ClientConnected;
import packets.Packet3ClientDisconnect;
import packets.Packet4Chat;
class Main implements ActionListener
{
    //creating class main that is a child class of ActionListener, this implements the ActionListener of the interface
    //this interacts with the method actionPerformed, it checks to see if a button was pressed
    private final Client client;
    //setting the Client
    private String user;
    //creating a string variable for the user
    private final JFrame frameWindow = new JFrame("The Clients Chat!");
    //creating a frame for the window called The Clients Chat! this appears at the top of the GUI
    private final JTextArea textArea = new JTextArea();
    //creating a new textArea, which is at the bottom
    private final JTextField textSpace = new JTextField(25);
    //creating a new TextField, this can take up to 25 characters
    private final JButton sendButton = new JButton("Send Message");
    //creating a button called send message
    public Main()
    {
        //
        client = new Client();
        //declaring the Client
        client.start();
        //starting the client
        try
        {
            //try and connect to the client
            client.connect(5000, "127.0.0.1", 23900, 23901);
            //this connects the client to the server: 5000 timeout, connecting to local host address, TCP port is:23900 and the UDP port is: 23901
        }
        catch (IOException e)
        {
            //using a catch statement if the client cannot connect to the server
            JOptionPane.showMessageDialog(null, "I am sorry but cannot connect to the server at this time.");
            //this will pop up if the connection fails
            return;
            //after message appears it returns back to the screen
        }
        //this is registering all the packets
        client.getKryo().register(Packet.class);
        client.getKryo().register(Packet1Connect.class);
        client.getKryo().register(Packet2ClientConnected.class);
        client.getKryo().register(Packet3ClientDisconnect.class);
        client.getKryo().register(Packet4Chat.class);
        client.addListener(new Listener()
        {
            //creating a listener
            public void received(Connection connection, Object object)
            {
                if(object instanceof Packet)
                {
                    //using a conditional if statement to check if it is a packet
                    if(object instanceof Packet2ClientConnected)
                    {
                        //if we get a packet that is client connected packet
                        Packet2ClientConnected p2 = (Packet2ClientConnected) object;
                        //casting the packet 2 connect
                        System.out.println("connected");
                        textArea.append(p2.clientName + " connected");
                        //this shows who connected and goes to next line
                    }
                    else if(object instanceof Packet3ClientDisconnect)
                    {
                        //this is when client disconnects
                        Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
                        //
                        textArea.append(p3.clientName +"disconnected");
                        //when a person disconnects and next line
                    }
                    else if(object instanceof Packet4Chat)
                    {
                        //this is for the chats
                        Packet4Chat p4 = (Packet4Chat) object;
                        //casting
                        textArea.append(p4.username+ ":" + p4.message + "");
                        //
                    }
                }
            }
        }
        );
        user = JOptionPane.showInputDialog("please enter your username");
        //this shows a pop up asking a new user to enter their username, it sets the username to what the new user sets it to and everything runs
        Packet1Connect p1 = new Packet1Connect();
        //creating a new packet1connect
        p1.username = user;
        //setting the username to what the new user enters
        client.sendTCP(p1);
        //continues on from that
        frameWindow.setLocationRelativeTo(null);
        //this sets the location of the gui window, setting it to null sets it in the middle
        frameWindow.setSize(450, 375);
        //setting the frame size, the width and height
        frameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this closes the program properly
        Panel p = new Panel();
        //creating a panel that organises everything to be added to the frame
        sendButton.addActionListener(this);
        //adding the ActionListener button to this (when a button is pressed) call the actionPerformed function
        textArea.setWrapStyleWord(true);
        //setting this so you can move it left and right
        textArea.setEditable(false);
        //setting it to non editable so you can type in the textArea
        textArea.setEditable(true);
        //setting the editable to true
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        //adding the scrollPanel to the textArea
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //creating a vertical scrollbar to the left and right
        areaScrollPane.setPreferredSize(new Dimension(430, 275));
        //setting the scrollPanel preferred size to 430 wide and 275 high
        //adding them to the panel in order
        p.add(areaScrollPane);
        //first add the scrollPane
        p.add(textSpace);
        //adding the textField
        p.add(sendButton);
        //adding the send button
        frameWindow.add(p);
        //adding the panel to the frame
        frameWindow.setVisible(true);
        //setting the visible to true so it works
    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        String message = textSpace.getText();
        //when the send button is pressed a message appears from the textField
        if(!message.equalsIgnoreCase(""))
        {
            //if nothing is typed in
            textSpace.setText("");
            //sets the textField to nothing
            Packet4Chat p4 = new Packet4Chat();
            //creating a new packet4chat
            p4.username = user;
            //setting the variable username to our username
            p4.message = message;
            //setting the variable message and packet to the message that is typed
            client.sendTCP(p4);
            //sends the packet to the server once send button is sent
            textSpace.setText("");
            //setting the textField to nothing
        }
    }
    public static void main(String[] args)
    {
        //main function the runs the application
        new Main();
        //calling the new main function
    }
}