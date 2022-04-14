package libraries.discordrpc.callbacks;

import com.sun.jna.Callback;

import libraries.discordrpc.DiscordUser;

/**
 * @author Nicolas "Vatuu" Adamoglou
 * @version 1.5.1
 * <p>
 * Interface to be implemented in classes that will be registered as "ReadyCallback" Event Handler.
 * @see libraries.discordrpc.DiscordEventHandlers
 **/
public interface ReadyCallback extends Callback {

	/**
	 * Method called when the connection to Discord has been established.
	 *
	 * @param user Object containing all required information about the user executing the app.
	 * @see DiscordUser
	 **/
	void apply(DiscordUser user);
}

