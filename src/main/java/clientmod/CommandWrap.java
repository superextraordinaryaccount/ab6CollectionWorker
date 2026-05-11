package clientmod;

import enviroment.commands.Commands;

public interface CommandWrap {
    Commands create(String arg, InputManager inputManager) throws Exception;
}
