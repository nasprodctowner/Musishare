package fr.nashani.musishare.Chat;

/**
 * The type Chat object.
 */
public class ChatObject {


    private String message;
    private Boolean currentUser;

    /**
     * Instantiates a new Chat object.
     *
     * @param message     the message
     * @param currentUser the current user
     */
    public ChatObject(String message, Boolean currentUser){
        this.message = message;
        this.currentUser = currentUser;
    }

    /**
     * Get message string.
     *
     * @return the string
     */
    public String getMessage(){
        return message;
    }

    /**
     * Set message.
     *
     * @param userID the user id
     */
    public void setMessage(String userID){
        this.message = message;
    }

    /**
     * Get current user boolean.
     *
     * @return the boolean
     */
    public Boolean getCurrentUser(){
        return currentUser;
    }

    /**
     * Set current user.
     *
     * @param currentUser the current user
     */
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }

}
