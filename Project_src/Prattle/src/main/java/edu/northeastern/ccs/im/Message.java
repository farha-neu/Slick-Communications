package edu.northeastern.ccs.im;

/**
 * Each instance of this class represents a single transmission by our IM
 * clients.
 * <p>
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Message {
    /**
     * List of the different possible message types.
     */
    protected enum MessageType {
        /**
         * Message sent by the user attempting to login using a specified username.
         */
        HELLO("HLO"),
        /**
         * Message sent by the server acknowledging a successful log in.
         */
        ACKNOWLEDGE("ACK"),
        /**
         * Message sent by the server rejecting a login attempt.
         */
        NO_ACKNOWLEDGE("NAK"),
        /**
         * Message sent by the user to start the logging out process and sent by the
         * server once the logout process completes.
         */
        QUIT("BYE"),
        /**
         * Message sent by the group
         * the msgSender would be constructed as GROUPNAME - SENDERNAME
         */
        GROUPMESSAGE("GRM"),
        /**
         * Message sent by an individual
         */
        INDIVIDUAL_MESSAGE("INDV"),
        /**
         * Message whose contents is broadcast to all connected users.
         */
        BROADCAST("BCT");
        /**
         * Store the short name of this message type.
         */
        private String tla;

        /**
         * Define the message type and specify its short name.
         *
         * @param abbrev Short name of this message type, as a String.
         */
        private MessageType(String abbrev) {
            tla = abbrev;
        }

        /**
         * Return a representation of this Message as a String.
         *
         * @return Three letter abbreviation for this type of message.
         */
        @Override
        public String toString() {
            return tla;
        }
    }

    /**
     * The string sent when a field is null.
     */
    private static final String NULL_OUTPUT = "--";

    /**
     * The handle of the message.
     */
    private MessageType msgType;

    /**
     * The first argument used in the message. This will be the sender's identifier.
     */
    private String msgSender;

    /**
     * This will be the receiver's identifier.
     */
    private String msgReceiver;

    /**
     * The second argument used in the message.
     */
    private String msgText;

    /**
     * Create a new message that contains actual IM text. The type of distribution
     * is defined by the handle and we must also set the name of the message sender,
     * message recipient, and the text to send.
     *
     * @param handle  Handle for the type of message being created.
     * @param srcName Name of the individual sending this message
     * @param receiverName Name of the individual receiving this message
     * @param text    Text of the instant message
     */
    private Message(MessageType handle, String srcName, String receiverName, String text) {
        msgType = handle;
        // Save the properly formatted identifier for the user sending the
        // message.
        msgSender = srcName;
        msgReceiver = receiverName;
        // Save the text of the message.
        msgText = text;
    }
    


    /**
     * Create simple command type message that does not include any data.
     *
     * @param handle Handle for the type of message being created.
     */
    private Message(MessageType handle) {
        this(handle, null, null, null);
    }

    /**
     * Create a new message that contains a command sent the server that requires a
     * single argument. This message contains the given handle and the single
     * argument.
     *
     * @param handle  Handle for the type of message being created.
     * @param srcName Argument for the message; at present this is the name used to
     *                log-in to the IM server.
     */
    private Message(MessageType handle, String srcName) {
        this(handle, srcName, null, null);
    }

    /**
     * Create a new message to continue the logout process.
     *
     * @return Instance of Message that specifies the process is logging out.
     */
    public static Message makeQuitMessage(String myName) {
        return new Message(MessageType.QUIT, myName, null, null);
    }

    /**
     * @param sender
     * @param text
     * @return
     */
    public static Message makeIndividualMessage(String sender, String msgReceiver,String text) {
        return new Message(MessageType.INDIVIDUAL_MESSAGE,sender, msgReceiver, text);
    }

    /**
     * Create a new message to continue the logout process.
     *
     * @return Instance of Message that specifies the process is logging out.
     */
    public static Message makeGroupMessage(String myName, String groupName, String text) {
        return new Message(MessageType.GROUPMESSAGE, myName, groupName, text);
    }

    /**
     * Create a new message broadcasting an announcement to the world.
     *
     * @param myName Name of the sender of this very important missive.
     * @param text   Text of the message that will be sent to all users
     * @return Instance of Message that transmits text to all logged in users.
     */
    public static Message makeBroadcastMessage(String myName, String text) {
        return new Message(MessageType.BROADCAST, myName, null, text);
    }

    /**
     * Create a new message stating the name with which the user would like to
     * login.
     *
     * @param text Name the user wishes to use as their screen name.
     * @return Instance of Message that can be sent to the server to try and login.
     */
    protected static Message makeHelloMessage(String text) {
        return new Message(MessageType.HELLO, null, null,text);
    }

    /**
     * Given a handle, name and text, return the appropriate message instance or an
     * instance from a subclass of message.
     *
     * @param handle  Handle of the message to be generated.
     * @param srcName Name of the originator of the message (may be null)
     * @param text    Text sent in this message (may be null)
     * @return Instance of Message (or its subclasses) representing the handle,
     * name, & text.
     */
    public static Message makeMessage(String handle, String srcName, String text) {
        Message result = null;
        if (handle.compareTo(MessageType.QUIT.toString()) == 0) {
            result = makeQuitMessage(srcName);
        } else if (handle.compareTo(MessageType.HELLO.toString()) == 0) {
            result = makeSimpleLoginMessage(srcName);
        } else if (handle.compareTo(MessageType.BROADCAST.toString()) == 0) {
            result = makeBroadcastMessage(srcName, text);
        } else if (handle.compareTo(MessageType.ACKNOWLEDGE.toString()) == 0) {
            result = makeAcknowledgeMessage(srcName);
        } else if (handle.compareTo(MessageType.NO_ACKNOWLEDGE.toString()) == 0) {
            result = makeNoAcknowledgeMessage();
        }
        return result;
    }

    public static Message makeMessage(String handle, String srcName, String msgReceiver, String text) {
        Message result = null;
        if (handle.compareTo(MessageType.INDIVIDUAL_MESSAGE.toString()) == 0) {
            result = makeIndividualMessage(srcName, msgReceiver, text);
        } else if (handle.compareTo(MessageType.GROUPMESSAGE.toString()) == 0) {
            result = makeGroupMessage(srcName, msgReceiver, text);
        }
        return result;
    }

    /**
     * Create a new message to reject the bad login attempt.
     *
     * @return Instance of Message that rejects the bad login attempt.
     */
    public static Message makeNoAcknowledgeMessage() {
        return new Message(MessageType.NO_ACKNOWLEDGE);
    }

    /**
     * Create a new message to acknowledge that the user successfully logged as the
     * name <code>srcName</code>.
     *
     * @param srcName Name the user was able to use to log in.
     * @return Instance of Message that acknowledges the successful login.
     */
    public static Message makeAcknowledgeMessage(String srcName) {
        return new Message(MessageType.ACKNOWLEDGE, srcName);
    }

    /**
     * Create a new message for the early stages when the user logs in without all
     * the special stuff.
     *
     * @param myName Name of the user who has just logged in.
     * @return Instance of Message specifying a new friend has just logged in.
     */
    public static Message makeSimpleLoginMessage(String myName) {
        return new Message(MessageType.HELLO, myName);
    }
    /**
     * Return the name of the sender of this message.
     *
     * @return String specifying the name of the message originator.
     */
    public String getName() {
        return msgSender;
    }

    /**
     * Return the name of the receiver of this message.
     *
     * @return String specifying the name of the message originator.
     */
    public String getMsgReceiver() {
        return msgReceiver == null ? "NO one": msgReceiver ;
    }

    /**
     * Return the text of this message.
     *
     * @return String equal to the text sent by this message.
     */
    public String getText() {
        return msgText;
    }
    
    /**
     * Return the type of this message.
     *
     * @return String equal to the type of this message.
     */
    public String getType() {
        return msgType.toString();
    }

    /**
     * Determine if this message is an acknowledgement message.
     *
     * @return True if the message is an acknowledgement message; false otherwise.
     */
    public boolean isAcknowledge() {
        return (msgType == MessageType.ACKNOWLEDGE);
    }

    /**
     * Determine if this message is broadcasting text to everyone.
     *
     * @return True if the message is a broadcast message; false otherwise.
     */
    public boolean isBroadcastMessage() {
        return (msgType == MessageType.BROADCAST);
    }

    /**
     * Determine if this message contains text which the recipient should display.
     *
     * @return True if the message is an actual instant message; false if the
     * message contains data
     */
    public boolean isDisplayMessage() {
        return (msgType == MessageType.BROADCAST);
    }

    /**
     * Determine if this message is sent by a group to the server.
     *
     * @return True if the message is a group message; false otherwise
     */
    public boolean isGroupMessage() {
        return (msgType == MessageType.GROUPMESSAGE);
    }
    
    /**
     * Determine if this message is sent by an individual.
     *
     * @return True if the message is an individual message; false otherwise
     */
    public boolean isIndividualMessage() {
        return (msgType == MessageType.INDIVIDUAL_MESSAGE);
    }

    /**
     * Determine if this message is sent by a new client to log-in to the server.
     *
     * @return True if the message is an initialization message; false otherwise
     */
    public boolean isInitialization() {
        return (msgType == MessageType.HELLO);
    }

    /**
     * Determine if this message is a message signing off from the IM server.
     *
     * @return True if the message is sent when signing off; false otherwise
     */
    public boolean terminate() {
        return (msgType == MessageType.QUIT);
    }


    /**
     * Representation of this message as a String. This begins with the message
     * handle and then contains the length (as an integer) and the value of the next
     * two arguments.
     *
     * @return Representation of this message as a String.
     */
    @Override
    public String toString() {
        String result = msgType.toString();
        if (msgSender != null) {
            result += " " + msgSender.length() + " " + msgSender;
        } else {
            result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
        }
        if(msgReceiver != null){
            result += " " + msgReceiver.length() + " " + msgReceiver;
        }
        if (msgText != null) {
            result += " " + msgText.length() + " " + msgText;
        } else {
            result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
        }
        return result;
    }
}
